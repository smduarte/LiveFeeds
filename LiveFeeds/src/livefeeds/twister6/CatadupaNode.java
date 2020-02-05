package livefeeds.twister6;

import static livefeeds.twister6.config.Config.Config;
import static livefeeds.twister6.stats.Statistics.Statistics;
import static simsim.core.Simulation.rg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import livefeeds.twister6.gui.NodeDisplay;
import livefeeds.twister6.msgs.CatadupaCast;
import livefeeds.twister6.msgs.CatadupaMessageHandler;
import livefeeds.twister6.msgs.CatadupaSocketHandler;
import livefeeds.twister6.msgs.CatadupaSocketcReplyHandler;
import livefeeds.twister6.msgs.DbRepairReply;
import livefeeds.twister6.msgs.DbRepairRequest;
import livefeeds.twister6.msgs.DbUploadAccept;
import livefeeds.twister6.msgs.DbUploadEndpoints;
import livefeeds.twister6.msgs.DbUploadEndpointsRequest;
import livefeeds.twister6.msgs.DbUploadFilters;
import livefeeds.twister6.msgs.DbUploadFiltersRequest;
import livefeeds.twister6.msgs.DbUploadReject;
import livefeeds.twister6.msgs.DepartureCast;
import livefeeds.twister6.msgs.Departures;
import livefeeds.twister6.msgs.JoinRequest;
import livefeeds.twister6.msgs.NewArrivals;
import livefeeds.twister6.stats.NodeStats;

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
import simsim.utils.Threading;

import umontreal.iro.lecuyer.stat.Tally;

public class CatadupaNode extends AbstractNode implements CatadupaMessageHandler, CatadupaSocketHandler, Displayable, Comparable<CatadupaNode> {

	public long key;
	public int index, offline_index;

	public State state;

	protected int __storage_index;

	public static class State {
		public XY pos;

		public DB db;

		public NodeStats stats;
		public NodeDisplay displayable;

		Task sequencerTask, repairTask;

		public boolean joined = false;

		public Filter filter;

		public double lastReceivedBroadCast;
		public double sessionBegin = 0, sessionEnd = 0;

		int c_serial = -1, p_serial = -1, m_serial = -1;
		public double lastSequencerRun = Double.NEGATIVE_INFINITY;

		double targetFanout = Config.BROADCAST_MAX_FANOUT;

		HashSet<Long> keys = new HashSet<Long>();
		HashSet<Integer> joins = new HashSet<Integer>();
		HashSet<Integer> rejoins = new HashSet<Integer>();
		HashSet<Integer> departures = new HashSet<Integer>();

		Map<Integer, Double> failureDetectorCache = new HashMap<Integer, Double>();
	};

	final SocketFactory lPriority, hPriority;

	public CatadupaNode() {
		super();

		GlobalDB.store(this);

		state = new State();
		state.db = new DB(this);
		state.filter = new Filter();

		state.stats = new NodeStats();
		state.displayable = new NodeDisplay(this);
		state.joined = false;

		lPriority = new SocketFactory(address.endpoint(1, this), this, this);
		hPriority = new SocketFactory(address.endpoint(2, this), this, this);

		hPriority.endpoint.setBandwidthRates(50 * 1024, 24 * 1024);
		lPriority.endpoint.setBandwidthRates(50 * 1024, 24 * 1024);
	}

	private Stamp nextStamp() {
		state.p_serial = state.c_serial;
		state.c_serial = state.db.view.maxSerial() + 1;
		return new Stamp(key, state.c_serial, state.p_serial);
	}

	public boolean accepts(Event e) {
		return state.filter.accepts(e) && super.isOnline() && state.joined;
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
			Statistics.recordNodeTraffic(CatadupaNode.this);
		}

		if (state.lastSequencerRun > 0)
			ArrivalsDB.gc(key);

		assert isOffline();
	}

	// --------------------------------------------------------------------------------------------------------------------------------
	// Failed Nodes...
	double lastDepartureSend = 0;

	public void onSendFailure(EndPoint dst, Message m) {
		if (dst.address != this.address) {

			CatadupaNode failedNode = (CatadupaNode) dst.address.endpoint.handler;

			if (state.db.loadedEndpoints) {
				state.db.deadNodes.set(failedNode.offline_index);
				state.departures.add(failedNode.offline_index);

				if (!isSequencer() && state.departures.size() > 3 &&  (currentTime() - lastDepartureSend) > 120) {
					Departures d = new Departures(state.departures);
					state.departures.clear();
					lastDepartureSend = currentTime();
					onReceive((Socket) null, new DepartureCast(0, this.key, new Range(), d));
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

//		double due = 120 * 3600 - currentTime() ;
//		if (due > 0 && (key % 10) <= 4)
//			new Task(this, due) {
//				public void run() {
//					shutdown();
//					System.out.println("DYING.....");
//				}
//			};
	}

	int rejects = 0;
	static Tally dbRejectsTally = new Tally("dbRejectsTally");

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
							rejects++;
							reSchedule(0.1);
						}

						public void onReply(DbUploadEndpoints m) {
							if (!state.db.loadedEndpoints) {
								state.db.append(m);
								joinCatadupa();
								repairCatadupa();
								db_DownloadDbFilters();
							} else {
								System.out.println(index + " WTF.....");
								Threading.sleep(1000);
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
		if (eta < 1 && (address.uploadedBytes < Config.CATADUPA_LOAD_BALANCE_FACTOR * address.downloadedBytes || rg.nextDouble() < 0.01 || state.sessionBegin < 30 * 60)) {
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
							rejects++;
							reSchedule(0.1);
						}

						public void onReply(DbUploadAccept m) {
							reSchedule(m.eta + 5);
						}

						public void onReply(DbUploadFilters m) {
							state.db.append(m);
							if (!state.db.loadedFilters)
								reSchedule(Config.DB_FILTER_DOWNLOAD_SPAN / Config.DB_FILTER_DOWNLOAD_PIECES);
							else {
								dbRejectsTally.add(rejects);
								if (dbRejectsTally.numberObs() % 1000 == -1) {
									System.out.println(rejects + "/" + dbRejectsTally.average() + "/" + dbRejectsTally.max());
									System.err.printf("U:%.0f KB D: %.0f KB\n", address.uploadedBytes / 1024.0, address.downloadedBytes / 1024.0);
									dbRejectsTally.init();
								}
							}

						}
					});
				} else
					cancel();
			}
		};
	}

	public void onReceive(Socket call, DbUploadFiltersRequest m) {
		double eta = lPriority.eta(1);
		if (eta < 1.0
				&& (address.uploadedBytes < Config.CATADUPA_LOAD_BALANCE_FACTOR * address.downloadedBytes || rg.nextDouble() < 0.01 || state.sessionBegin < 30 * 60.0)) {
			SocketMessage res = new DbUploadFilters(m.piece);
			call.reply(new DbUploadAccept(lPriority.eta(res.length())));
			call.reply(res);
		} else
			call.reply(new DbUploadReject());
	}

	void joinCatadupa() {
		final boolean rejoining = rg.nextDouble() < Config.REJOIN_PROBABILITY;

		new PeriodicTask(this, 0, Config.JOIN_ATTEMPT_PERIOD) {
			public void run() {
				if (!state.joined) {
					CatadupaNode sequencer = state.db.sequencerFor(key);

					lPriority.request(sequencer.endpoint, new JoinRequest(key, index, rejoining), 0, new CatadupaReplyHandler() {
						public void onFailure() {
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

		if (m.rejoin)
			state.rejoins.add(m.index);
		else
			state.joins.add(m.index);

		state.keys.add(m.key);

		if (!state.sequencerTask.isScheduled())
			state.sequencerTask.reSchedule(Config.SEQUENCER_BROADCAST_PERIOD + rg.nextDouble());
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	void initSequencerTask() {
		state.sequencerTask = new Task(this, 0.01) {
			public void run() {
				if (state.joins.size() > 0 || state.rejoins.size() > 0) {

					state.lastSequencerRun = currentTime();

					Stamp stamp = nextStamp();
					NewArrivals bp = new NewArrivals(stamp, state.joins, state.rejoins, state.keys, state.departures);
					
					View.GV.add(stamp);

					state.keys.clear();
					state.joins.clear();
					state.rejoins.clear();
					state.departures.clear();

					ArrivalsDB.store(bp);
					onReceive((Socket) null, new CatadupaCast(0, new Range(), bp));
				}
			}
		};
	}

	// --------------------------------------------------------------------------------------------------------------------------------
	// Broadcast a membership aggregate event.
	public void onReceive(Socket sock, final CatadupaCast m) {

		final int BroadcastFanout = broadcastFanout(m.level);

		Statistics.fanoutStats.recordFanout(BroadcastFanout);

		this.onReceive(sock, m.payload);

		Range r0 = m.range;

		if (m.level > 0)
			r0 = r0.advancePast(this.key);

		if (r0.sizeGreaterThan(BroadcastFanout, state.db)) {
			for (Range j : r0.slice(m.level, BroadcastFanout, state.db)) {
				int deadNodeDelay = 0;
				for (CatadupaNode i : j.nodes(state.db)) {
					if (i.key == m.payload.stamp.key)
						continue;

					if (hPriority.send(i.endpoint, new CatadupaCast(m.level + 1, j, m.payload), deadNodeDelay))
						break;
					else
						deadNodeDelay += 2;
				}
			}
		} else {
			for (CatadupaNode i : r0.nodes(state.db))
				if (i != this && i.key != m.payload.stamp.key)
					hPriority.send(i.endpoint, m.payload, 0);
		}
	}

	final int broadcastFanout(int level) {
		return Config.CATADUPA_DYNAMIC_FANOUT ? dynamicBroadcastFanout2(level) : staticBroadcastFanout(level);
	}

	public int dynamicBroadcastFanout3(int level) {
		final double TARGET_BROADCAST_ADJUST = 0.05;

		if (level == 0)
			return 1;

		state.targetFanout *= (1 + TARGET_BROADCAST_ADJUST * (address.uploadedBytes > address.downloadedBytes ? -1 : 1));
		state.targetFanout = Math.max(2, Math.min(state.targetFanout, Config.BROADCAST_MAX_FANOUT));

		int floor = (int) state.targetFanout;
		return floor + (rg.nextDouble() < (state.targetFanout - floor) ? 1 : 0);
	}

	public int dynamicBroadcastFanout(int level) {
		if (level == 0)
			return 1;

		double adjust = 1 - (state.stats.traffic.upload() - state.stats.traffic.download()) / (1 + state.stats.traffic.download());
		state.targetFanout = Math.max(2, Math.min(state.targetFanout * adjust, Config.BROADCAST_MAX_FANOUT));

		int floor = (int) state.targetFanout;
		return floor + (rg.nextDouble() < (state.targetFanout - floor) ? 1 : 0);
	}

	public int dynamicBroadcastFanout2(int level) {
		if (level == 0)
			return 1;

		double adjust = 1 - (state.stats.traffic.recent_upload_rate() - state.stats.traffic.recent_download_rate())
				/ (1 + state.stats.traffic.recent_download_rate());
		state.targetFanout = Math.max(2, Math.min(state.targetFanout * adjust, Config.BROADCAST_MAX_FANOUT));

		int floor = (int) state.targetFanout;
		return floor + (rg.nextDouble() < (state.targetFanout - floor) ? 1 : 0);
	}

	public int staticBroadcastFanout(int level) {
		return level == 0 ? 1 : Config.BROADCAST_MAX_FANOUT;
	}

	// Process a new membership aggregate event.
	// The node has joined when it is being announced in the payload.
	// Update the node membership database and the nodes membership views.
	public void onReceive(Socket call, NewArrivals m) {

		state.m_serial = Math.max(state.m_serial, m.stamp.c_serial) + 1;

		if (!state.joined && m.joins.contains(this.index) || m.rejoins.contains(this.index)) {
			state.joined = true;

			Statistics.timeStats.recordTimeToJoin(CatadupaNode.this);
		}

		state.db.store(m);
		Statistics.timeStats.recordCastLatency(CatadupaNode.this, m);
	}

	// ------------------------------------------------------------------------------------------------
	// DEPARTURES
	// --------------------------------------------------------------------------------------------------------------------------------
	// Broadcast a membership departure aggregate event.
	public void onReceive(Socket call, final DepartureCast m) {
		final int BroadcastFanout = broadcastFanout(m.level);

		this.onReceive(call, m.payload);

		Range r0 = m.range;

		if (m.level > 0)
			r0 = r0.advancePast(this.key);

		if (r0.sizeGreaterThan(BroadcastFanout, state.db)) {
			for (Range j : r0.slice(m.level, BroadcastFanout, state.db)) {
				int deadNodeDelay = 0;
				for (CatadupaNode i : j.nodes(state.db)) {
					if (i.key == m.root)
						continue;

					if (hPriority.send(i.endpoint, new DepartureCast(m.level + 1, m.root, j, m.payload), deadNodeDelay))
						break;
					else
						deadNodeDelay += 2;
				}
			}
		} else {
			for (CatadupaNode i : r0.nodes(state.db))
				if (i != this && i.key != m.root)
					hPriority.send(i.endpoint, m.payload, 0);
		}
	}

	static Tally g_departures = new Tally("g_departures") ;
	
	public void onReceive(Socket call, Departures m) {		
		for( int i : m.departures )
			g_departures.add( state.db.deadNodes.get(i) ? 1 : 0 ) ;
		state.db.store(m.departures);
		
		if( ! isSequencer() ) 			
			state.departures.removeAll(m.departures);		
		
//		if( (g_departures.numberObs() % 10) == 9 );
//			System.out.println( "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + g_departures.report() );
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

								Pair<List<Integer>, List<NewArrivals>> diffs;

								diffs = state.db.view.differences(other.view, repairUploadBudget());
								state.db.missingStamps.addAll(diffs.first);

								for (NewArrivals j : other.data)
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

		Pair<List<Integer>, List<NewArrivals>> diffs;

		diffs = state.db.view.differences(other.view, repairUploadBudget());
		state.db.missingStamps.addAll(diffs.first);

		if (diffs.first.size() > 0 || diffs.second.size() > 0)
			call.reply(new DbRepairReply(diffs.second, state.db.view), new CatadupaReplyHandler() {

				public void onReply(DbRepairReply r) {

					avg_repairItems.add(r.data.size());

					repairPeriod *= r.data.size() == 0 ? 1.1 : 0.5;
					repairPeriod = Math.min(45, Math.max(2.5, repairPeriod));

					for (NewArrivals j : r.data)
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

	public boolean isSequencer() {
		return currentTime() - state.lastSequencerRun < 2 * Config.SEQUENCER_BROADCAST_PERIOD;
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
}
