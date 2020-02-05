package livefeeds.sift0;

import static livefeeds.sift0.config.Config.Config;
import static livefeeds.sift0.stats.Statistics.Statistics;
import static simsim.core.Simulation.rg;

import java.util.HashSet;
import java.util.List;

import livefeeds.sift0.gui.NodeDisplay;
import livefeeds.sift0.msgs.CatadupaCast;
import livefeeds.sift0.msgs.CatadupaCastPayload;
import livefeeds.sift0.msgs.CatadupaSocketHandler;
import livefeeds.sift0.msgs.CatadupaSocketcReplyHandler;
import livefeeds.sift0.msgs.CatadupaUpdate;
import livefeeds.sift0.msgs.DbRepairReply;
import livefeeds.sift0.msgs.DbRepairRequest;
import livefeeds.sift0.msgs.DbUploadAccept;
import livefeeds.sift0.msgs.DbUploadEndpoints;
import livefeeds.sift0.msgs.DbUploadEndpointsRequest;
import livefeeds.sift0.msgs.DbUploadFilters;
import livefeeds.sift0.msgs.DbUploadFiltersRequest;
import livefeeds.sift0.msgs.DbUploadReject;
import livefeeds.sift0.msgs.FailureNotice;
import livefeeds.sift0.msgs.JoinRequest;
import livefeeds.sift0.msgs.JoinRequestAccept;
import livefeeds.sift0.msgs.SliceCast;
import livefeeds.sift0.msgs.SliceCastPayload;
import livefeeds.sift0.stats.NodeStats;

import simsim.core.AbstractNode;
import simsim.core.Displayable;
import simsim.core.EndPoint;
import simsim.core.Message;
import simsim.core.PeriodicTask;
import simsim.core.Task;
import simsim.gui.canvas.Canvas;
import simsim.gui.geom.XY;
import simsim.sockets.Socket;
import simsim.sockets.SocketFactory;
import simsim.sockets.SocketMessage;
import simsim.utils.Pair;

import umontreal.iro.lecuyer.stat.Tally;

public class CatadupaNode extends AbstractNode implements CatadupaSocketHandler, Displayable, Comparable<CatadupaNode> {

	static int XXX = 157, YYY = 8;
	public long key;
	protected int __storage_index;
	public int index, offline_index;

	public State state;

	public static class State {
		public XY pos;

		public DB db;

		public SliceDB sdb;

		public NodeStats stats;
		public NodeDisplay displayable;

		Task sequencerTask, repairTask;

		public boolean joined = false;

		public Filter filter;

		public double lastReceivedBroadCast;
		public double sessionBegin = 0, sessionEnd = 0;

		public double lastSequencerRun = Double.NEGATIVE_INFINITY;
		public double lastDepartureSend = Double.NEGATIVE_INFINITY;

		public int aggregation_level = Integer.MAX_VALUE;

		double targetFanout = Config.BROADCAST_MAX_FANOUT;

		HashSet<Long> keys = new HashSet<Long>();
		HashSet<Integer> joins = new HashSet<Integer>();
		HashSet<Integer> rejoins = new HashSet<Integer>();
		HashSet<Integer> departures = new HashSet<Integer>();

		double prematureReJoins = 0;
	};

	final SocketFactory lPriority, hPriority;

	public CatadupaNode() {
		super();

		GlobalDB.store(this);

		state = new State();
		state.db = new DB(this);
		state.sdb = new SliceDB(this);

		state.stats = new NodeStats();
		state.displayable = new NodeDisplay(this);
		state.joined = false;

		lPriority = new SocketFactory(address.endpoint(1, this), this, this);
		hPriority = new SocketFactory(address.endpoint(2, this), this, this);

		hPriority.endpoint.setBandwidthRates(Config.CAT_H_PEAK_CAPACITY, Config.CAT_H_CAPACITY);
		lPriority.endpoint.setBandwidthRates(Config.CAT_L_PEAK_CAPACITY, Config.CAT_L_CAPACITY);
	}

	public boolean accepts(Event e) {
		throw new RuntimeException("Unexpected call to CatadupaNode.accepts()");
	}

	public double upTime() {
		return Math.min(Config.MAX_SESSION_DURATION, currentTime() - state.sessionBegin);
	}

	public String toString() {
		return String.format("%d", index);
	}

	public void shutdown() {

		GlobalDB.dispose(this);
		super.dispose();

		state.sessionEnd = currentTime();

		if (state.joined && state.sessionBegin > Config.MAX_SESSION_DURATION) {
			Statistics.recordCatadupaTraffic(CatadupaNode.this);
		}

		if (state.lastSequencerRun > 0)
			ArrivalsDB.gc(key);

		assert isOffline();
	}

	// --------------------------------------------------------------------------------------------------------------------------------
	// Failed Nodes...

	public void onSendFailure(EndPoint dst, Message m) {
		if (dst.address != this.address) {

			CatadupaNode failedNode = (CatadupaNode) dst.address.endpoint.handler;

			if (state.db.loadedEndpoints) {
				state.db.deadNodes.set(failedNode.offline_index);
				state.departures.add(failedNode.offline_index);

				if (!isSequencer() && state.departures.size() > 0 && (currentTime() - state.lastDepartureSend) > 10) {
					FailureNotice r = new FailureNotice(state.departures);
					CatadupaNode sequencer = state.db.sequencerFor(r.level, this.key);
					lPriority.send(sequencer.endpoint, r, 0, new CatadupaReplyHandler());
					state.lastDepartureSend = currentTime();
					state.departures.clear();
				}
			}
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------
	public void init() {
		initSequencerTask();
		initSessionManagementTask();

		db_DownloadEndpoints();
	}

	// --------------------------------------------------------------------------------------------------------------------------------
	public void initSessionManagementTask() {
		state.sessionBegin = currentTime();
		state.lastReceivedBroadCast = currentTime();

		double sessionDuration = Config.churn.sessionDuration();
		new Task(this, sessionDuration) {
			public void run() {
				shutdown();
			}
		};

		// double due = 120 * 3600 - currentTime() ; // CAUSE A MASSIVE 40%
		// CRASH AT 120Hours
		// if (due > 0 && (key % 10) <= 4)
		// new Task(this, due) {
		// public void run() {
		// shutdown();
		// System.out.println("DYING.....");
		// }
		// };
	}

	// --------------------------------------------------------------------------------------------------------------------------------
	private void db_DownloadEndpoints() {
		new PeriodicTask(this, 10) {
			public void run() {

				if (!state.db.loadedEndpoints) {
					final CatadupaNode seed = state.db.randomSeedNode(key);

					lPriority.request(seed.endpoint, new DbUploadEndpointsRequest(), 0, new CatadupaReplyHandler() {

						public void onFailure() {
							reSchedule(0.1);
						}

						public void onReply(DbUploadAccept m) {
							reSchedule(m.eta + 5);
						}

						public void onReply(DbUploadReject m) {
							reSchedule(0.1);
						}

						public void onReply(DbUploadEndpoints m) {
							if (!state.db.loadedEndpoints) {
								state.db.append(m);
								joinCatadupa();
								repairCatadupa();
								db_DownloadDbFilters();
							} else {
								System.err.println(index + " WTF.....");
								// Threading.sleep(1000);
								System.exit(0);
							}
						}
					});
				} else
					cancel();
			}
		};

	}

	public void onReceive(Socket call, DbUploadEndpointsRequest m) {
		state.db.trimView(Config.VIEW_CUTOFF);
		double eta = lPriority.eta(1);
		if (eta < 1 && (badRatio() || rg.nextDouble() < 0.01 || state.sessionBegin < 30 * 60)) {
			DbUploadEndpoints res = new DbUploadEndpoints(state.db);
			res.eta = lPriority.eta(res.length());

			call.reply(new DbUploadAccept(lPriority.eta(res.length())));
			call.reply(res);
		} else
			call.reply(new DbUploadReject());
	}

	private void db_DownloadDbFilters() {
		new PeriodicTask(this, 10) {
			public void run() {
				if (!state.db.loadedFilters) {
					final CatadupaNode seed = state.db.randomSeedNode(key);

					lPriority.request(seed.endpoint, new DbUploadFiltersRequest(state.db.nextFilterPiece()), 0, new CatadupaReplyHandler() {

						public void onFailure() {
							reSchedule(1);
						}

						public void onReply(DbUploadReject m) {
							reSchedule(0.1);
						}

						public void onReply(DbUploadAccept m) {
							reSchedule(m.eta + 5);
						}

						public void onReply(DbUploadFilters m) {
							state.db.append(m);
							if (!state.db.loadedFilters)
								reSchedule(Config.DB_FILTER_DOWNLOAD_SPAN / Config.DB_FILTER_DOWNLOAD_PIECES);
						}
					});
				} else
					cancel();
			}
		};
	}

	public void onReceive(Socket call, DbUploadFiltersRequest m) {
		double eta = lPriority.eta(1);
		if (eta < 1.0 && (badRatio() || rg.nextDouble() < 0.01 || state.sessionBegin < 30 * 60.0)) {
			SocketMessage res = new DbUploadFilters(m.piece);
			call.reply(new DbUploadAccept(lPriority.eta(res.length())));
			call.reply(res);
		} else
			call.reply(new DbUploadReject());
	}

	void joinCatadupa() {
		final boolean rejoining = rg.nextDouble() < Config.REJOIN_PROBABILITY;
		new PeriodicTask(this, 0, Config.JOIN_ATTEMPT_PERIOD) {
			double backoff = 1;

			public void run() {
				if (!state.joined) {
					JoinRequest r = new JoinRequest(key, index, rejoining);
					CatadupaNode sequencer = state.db.sequencerFor(r.level, key);
					lPriority.request(sequencer.endpoint, r, 0, new CatadupaReplyHandler() {
						public void onReply(JoinRequestAccept m) {
							backoff = Math.min(5, backoff * 1.5);
							reSchedule(Config.JOIN_ATTEMPT_PERIOD * (1 + backoff));
						}

						public void onFailure() {
							backoff = 1;
							reSchedule(0.1);
						}
					});
				} else
					cancel();
			}
		};
	}

	// --------------------------------------------------------------------------------------------------------------------------------
	public void onReceive(Socket sock, JoinRequest m) {
		sock.reply(new JoinRequestAccept(0));
		onReceive(sock, (CatadupaUpdate) m);
	}

	// --------------------------------------------------------------------------------------------------------------------------------
	public void onReceive(Socket sock, CatadupaUpdate m) {
		state.lastSequencerRun = currentTime();

		state.stats.churn.total_departures += m.failures.size();
		state.stats.churn.total_joins += m.joins.size() + m.rejoins.size();
		state.aggregation_level = Math.min(state.aggregation_level, m.level);

		state.keys.addAll(m.keys);
		state.joins.addAll(m.joins);
		state.rejoins.addAll(m.rejoins);
		state.departures.addAll(m.failures);
		
		state.db.storeDeadNodes(m.failures);
		state.db.storeFreshNodes(m.joins, m.rejoins);

		if (!state.sequencerTask.isScheduled())
			state.sequencerTask.reSchedule((state.aggregation_level > 0 ? 5 : Config.SEQUENCER_BROADCAST_PERIOD) + rg.nextDouble());
	}

	// --------------------------------------------------------------------------------------------------------------------------------
	void initSequencerTask() {
		state.sequencerTask = new Task(this, 0.01) {
			public void run() {
				if (state.joins.size() > 0 || state.rejoins.size() > 0 || state.departures.size() > 0) {
					state.lastSequencerRun = currentTime();

					if (state.aggregation_level < 1 || state.stats.churn.recentJoinRate() > Config.JOINS_TARGET_AGGREGATION_RATE)
						broadcastCatadupaUpdates();
					else
						aggregateCatadupaUpdates();

					state.keys.clear();
					state.joins.clear();
					state.rejoins.clear();
					state.departures.clear();
					state.aggregation_level = Integer.MAX_VALUE;
				}
			}
		};
	}

	private void aggregateCatadupaUpdates() {
		int level = Math.max(0, state.aggregation_level - 1);

		double A = state.stats.churn.recentJoinRate();
		double B = state.stats.churn.recentDepartureRate();

		double C = Math.max(A / Config.JOINS_TARGET_AGGREGATION_RATE, B / Config.EXITS_TARGET_AGGREGATION_RATE);
		while (level > 0 && C > 0 && C < 0.25) {
			level--;
			C *= 2;
		}
		if (state.aggregation_level > Config.SLICE_AGGREGATION_DEPTH) {
			level = Math.max(level, Config.SLICE_AGGREGATION_DEPTH);
		}
		CatadupaUpdate m = new CatadupaUpdate(level, state.joins, state.rejoins, state.keys, state.departures);
		CatadupaNode sequencer = state.db.sequencerFor(m.level, this.key);
		hPriority.send(sequencer.endpoint, m, 0);
		
		if (state.aggregation_level == Config.SLICE_AGGREGATION_DEPTH) {
			Stamp stamp = state.sdb.nextStamp();
			SliceCastPayload sp = new SliceCastPayload(stamp, state.joins, state.rejoins, state.keys, state.departures);
			onReceive((Socket) null, new SliceCast(0, state.sdb.sliceRange(), new Path().add(key), sp));
		}
	}

	private void broadcastCatadupaUpdates() {
		double A = state.stats.churn.recentJoinRate();
		double B = state.stats.churn.recentDepartureRate();

		System.out.println("------------------------->" + A + "/" + B);

		Stamp stamp = state.db.nextStamp();
		CatadupaCastPayload p = new CatadupaCastPayload(stamp, state.joins, state.rejoins, state.keys, state.departures);

		ArrivalsDB.store(p);

		onReceive((Socket) null, new CatadupaCast(0, new Range(), new Path().add(key), p));
	}

	static Tally pathLength = new Tally("Path Length");

	// --------------------------------------------------------------------------------------------------------------------------------
	// Broadcast a membership aggregate event.
	public void onReceive(Socket sock, final CatadupaCast m) {

		pathLength.add( m.path.size() ) ;
		if( pathLength.numberObs() % 1000 == 999) {
			System.out.println("-------------------------------");
			System.out.println( pathLength.report() );
		}
		final int BroadcastFanout = broadcastFanout(m.level);

		Statistics.fanoutStats.recordFanout(BroadcastFanout);

		this.onReceive(sock, m.payload);

		Range r0 = m.range;

		if (r0.sizeGreaterThan(BroadcastFanout, state.db)) {
			for (Range j : r0.slice(m.level, BroadcastFanout, state.db)) {

				double deadNodeDelay = 0;
				for (CatadupaNode i : j.nodeList(state.db)) {
					if (i != this && !m.path.contains(i.key)) {
						if (hPriority.send(i.endpoint, new CatadupaCast(m.level + 1, j, m.path.clone().add(key).retain(j), m.payload), deadNodeDelay))
							break;
						else
							deadNodeDelay += 2;
					}
				}
			}
		} else {
			for (CatadupaNode i : r0.nodes(state.db))
				if (i != this && !m.path.contains(i.key))
					hPriority.send(i.endpoint, m.payload, 0);
		}
	}

	public void onReceive(Socket sock, final SliceCast m) {

		final int BroadcastFanout = broadcastFanout(m.level);

		this.onReceive(sock, m.payload);

		Range r0 = m.range;

		if (r0.sizeGreaterThan(BroadcastFanout, state.db)) {
			for (Range j : r0.slice(m.level, BroadcastFanout, state.db)) {
				double deadNodeDelay = 0;
				for (CatadupaNode i : j.nodeList(state.db)) {
					if (i != this && ! m.path.contains(i.key)) {
						if (hPriority.send(i.endpoint, new SliceCast(m.level + 1, j, m.path.clone().add(key).retain(j), m.payload), deadNodeDelay))
							break;
						else
							deadNodeDelay += 2;
					}
				}
			}
		} else {
			for (CatadupaNode i : r0.nodes(state.db))
				if (i != this && !m.path.contains(i.key))
					hPriority.send(i.endpoint, m.payload, 0);
		}
	}

	final int broadcastFanout(int level) {
		return level == 0 ? 1 : Config.CATADUPA_DYNAMIC_FANOUT ? dynamicBroadcastFanout() : staticBroadcastFanout();
	}

	public int staticBroadcastFanout() {
		return Config.BROADCAST_MAX_FANOUT;
	}

	public int dynamicBroadcastFanout() {
		double adjust = 1 - deltaWholeSessionRatio();
		state.targetFanout = Math.max(2, Math.min(state.targetFanout * adjust, Config.BROADCAST_MAX_FANOUT));

		int floor = (int) state.targetFanout;
		return floor + (rg.nextDouble() < (state.targetFanout - floor) ? 1 : 0);
	}

	public int dynamicBroadcastFanout3() {
		final double TARGET_BROADCAST_ADJUST = 0.05;

		state.targetFanout *= (1 + TARGET_BROADCAST_ADJUST * (address.uploadedBytes > address.downloadedBytes ? -1 : 1));
		state.targetFanout = Math.max(2, Math.min(state.targetFanout, Config.BROADCAST_MAX_FANOUT));

		int floor = (int) state.targetFanout;
		return floor + (rg.nextDouble() < (state.targetFanout - floor) ? 1 : 0);
	}

	public int dynamicBroadcastFanout2() {

		double adjust = 1 - deltaRecentSessionRatio();
		state.targetFanout = Math.max(2, Math.min(state.targetFanout * adjust, Config.BROADCAST_MAX_FANOUT));

		int floor = (int) state.targetFanout;
		return floor + (rg.nextDouble() < (state.targetFanout - floor) ? 1 : 0);
	}

	public int dynamicBroadcastFanout0() {

		double adjust = 1 - Math.max(deltaWholeSessionRatio(), deltaRecentSessionRatio());
		state.targetFanout = Math.max(2, Math.min(state.targetFanout * adjust, Config.BROADCAST_MAX_FANOUT));

		int floor = (int) state.targetFanout;
		return floor + (rg.nextDouble() < (state.targetFanout - floor) ? 1 : 0);
	}

	private boolean badRatio() {
		return state.stats.catadupaTraffic.upload() < Config.CATADUPA_LOAD_BALANCE_FACTOR * state.stats.catadupaTraffic.download();
	}

	private double deltaWholeSessionRatio() {
		return (state.stats.catadupaTraffic.upload() - state.stats.catadupaTraffic.download()) / (1 + state.stats.catadupaTraffic.download());
	}

	private double deltaRecentSessionRatio() {
		return (state.stats.catadupaTraffic.recent_upload_rate() - state.stats.catadupaTraffic.recent_download_rate())
				/ (1 + state.stats.catadupaTraffic.recent_download_rate());
	}

	// ------------------------------------------------------------------------------------------------------------
	// Process a new membership aggregate event.
	// The node has joined when it is being announced in the payload.
	// Update the node membership database and the nodes membership views.
	public void onReceive(Socket call, CatadupaCastPayload m) {

		if (!state.joined && m.joins.contains(this.index) || m.rejoins.contains(this.index)) {
			state.joined = true;

			Statistics.timeStats.recordTimeToJoin(CatadupaNode.this);
		}

		if (index == XXX & m.stamp.g_serial == YYY && call != null)
			System.out.println(index + "/" + m.stamp);

		boolean duplicate = state.db.store(m) && call != null;

		// if( duplicate) {
		// System.out.println( index + "/" + m.stamp.g_serial );
		// Threading.sleep(1000) ;
		// System.exit(0) ;
		// }
		Statistics.catadupaDuplicates.add(duplicate ? 1 : 0);
		// if( Statistics.catadupaDuplicates.numberObs() % 1000 == 999 ) {
		// System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&");
		// System.out.println( Statistics.catadupaDuplicates.report() );
		// }
		Statistics.timeStats.recordCastLatency(CatadupaNode.this, m);
	}

	public void onReceive(Socket call, SliceCastPayload m) {
	}

	// ------------------------------------------------------------------------------------------------
	// EPIDEMIC MEMBERSHIP REPAIR

	double repairPeriod = Config.MEMBERSHIP_REPAIR_PERIOD;
	static Tally avg_repairPeriod = new Tally("avg_repairPeriod");
	static Tally avg_repairItems = new Tally("avg_repairItems");

	void repairCatadupa() {
		state.repairTask = new Task(this, rg.nextDouble()) {
			public void run() {

				if (state.db.loadedEndpoints) {

					state.db.trimView(Config.VIEW_CUTOFF);

					CatadupaNode other = state.db.randomNode();

					if (other != null && other.key != key) {

						hPriority.send(other.endpoint, new DbRepairRequest(state.db.view), 0, new CatadupaReplyHandler() {

							public void onFailure() {
								reSchedule(0.1);
							}

							public void onReply(Socket call, DbRepairReply other) {

								Pair<List<Integer>, List<CatadupaCastPayload>> diffs;

								diffs = state.db.view.differences(other.view, repairUploadBudget());
								state.db.missingStamps.addAll(diffs.first);

								for (CatadupaCastPayload j : other.data)
									onReceive((Socket) null, j);

								if (diffs.second.size() > 0) {
									call.reply(new DbRepairReply(diffs.second, state.db.view));
								}

								Statistics.repairStats.recordRepairPeriod(CatadupaNode.this);
								Statistics.repairStats.recordRepairReplies(other.data.size());

								repairPeriod *= state.db.missingStamps.size() > 0 ? 0 : 1.5;
								repairPeriod = Math.min(45, Math.max(10, repairPeriod));
								reSchedule(repairPeriod + rg.nextDouble());
								avg_repairPeriod.add(repairPeriod);
							}
						});
					}
					reSchedule(repairPeriod + rg.nextDouble());
				}

			}
		};
	}

	public void onReceive(Socket call, DbRepairRequest other) {

		Pair<List<Integer>, List<CatadupaCastPayload>> diffs;

		diffs = state.db.view.differences(other.view, repairUploadBudget());
		state.db.missingStamps.addAll(diffs.first);

		if (diffs.first.size() > 0 || diffs.second.size() > 0)
			call.reply(new DbRepairReply(diffs.second, state.db.view), new CatadupaReplyHandler() {

				public void onReply(DbRepairReply r) {

					avg_repairItems.add(r.data.size());

					repairPeriod *= r.data.size() == 0 ? 1.1 : 0.5;
					repairPeriod = Math.min(45, Math.max(2.5, repairPeriod));

					for (CatadupaCastPayload j : r.data)
						onReceive((Socket) null, j);

					Statistics.repairStats.recordRepairPeriod(CatadupaNode.this);
					Statistics.repairStats.recordRepairReplies(r.data.size());
				}
			});

		repairPeriod *= state.db.missingStamps.size() > 0 ? 0 : 1.5;
		repairPeriod = Math.min(45, Math.max(10, repairPeriod));
		state.repairTask.reSchedule(repairPeriod + rg.nextDouble());
	}

	private int repairUploadBudget() {
		if (state.sessionBegin > 15 * 60)
			return address.uploadedBytes < Config.CATADUPA_LOAD_BALANCE_FACTOR * address.downloadedBytes ? 5 : 2;
		else
			return 5000;

	}

	// EPIDEMIC MEMBERSHIP REPAIR
	// ------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------------

	public int compareTo(CatadupaNode other) {
		return key == other.key ? 0 : key < other.key ? -1 : 1;
	}

	protected String tab(int level) {
		final String res = ".............................................................................................................";
		return String.format("(%2d) %5d %6d, %s", level, key, key, res.substring(0, 2 * level));
	}

	public void displayOn(Canvas canvas) {
		state.displayable.displayOn(canvas);
	}

	// public boolean isSequencer() {
	// return currentTime() - state.lastSequencerRun < 2 *
	// Config.SEQUENCER_BROADCAST_PERIOD;
	// }

	public boolean isSequencer() {
		return state.aggregation_level < Integer.MAX_VALUE;
	}

	@Override
	public void onReceive(Socket call, Message m) {
		throw new RuntimeException("Unexpected generic scoket call");
	}

}

class CatadupaReplyHandler implements CatadupaSocketcReplyHandler {

	public void onFailure() {
	}

	public void onReply(Message m) {
		System.err.println(this.getClass());
		Thread.dumpStack();
	}

	public void onReply(Socket call, Message m) {
		System.err.println(this.getClass());
		Thread.dumpStack();
	}

	public void onReply(DbUploadAccept m) {
		Thread.dumpStack();
	}

	public void onReply(DbUploadReject m) {
		Thread.dumpStack();
	}

	public void onReply(DbUploadFilters m) {
		Thread.dumpStack();
	}

	public void onReply(DbUploadEndpoints m) {
		Thread.dumpStack();
	}

	public void onReply(DbRepairReply m) {
		Thread.dumpStack();
	}

	public void onReply(Socket call, DbRepairReply m) {
		Thread.dumpStack();
	}

	public void onReply(JoinRequestAccept m) {
		Thread.dumpStack();
	}
}
