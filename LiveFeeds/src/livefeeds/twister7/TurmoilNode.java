package livefeeds.twister7;

import static livefeeds.twister7.config.Config.Config;
import static livefeeds.twister7.stats.Statistics.Statistics;
import static simsim.core.Simulation.rg;
import static simsim.logging.Log.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import livefeeds.twister7.msgs.TurmoilAccept;
import livefeeds.twister7.msgs.TurmoilCast;
import livefeeds.twister7.msgs.TurmoilPayload;
import livefeeds.twister7.msgs.TurmoilReject;
import livefeeds.twister7.msgs.TurmoilSocketHandler;

import simsim.core.PeriodicTask;
import simsim.sockets.Socket;
import simsim.sockets.SocketFactory;

public class TurmoilNode extends CatadupaNode implements TurmoilSocketHandler {

	private int id_counter = 0;

	public double targetFanout = Config.PUBSUB_MAX_FANOUT ;
	
	protected final SocketFactory socket;

	protected TurmoilNode() {
		super();
		state.filter = new Filter3();
		socket = new SocketFactory(address.endpoint(3, this), this, this);
	}

	public void init() {
		super.init();
	}

	@Override
	public boolean accepts(Event e) {
//		return state.filter.accepts(e) ;
		return state.joined && state.filter.accepts(e) ;
	}


	public static void injectEvents() {
		new PeriodicTask(2*3600, 1) {
			public void run() {
				//if( GlobalDB.size() < 25000 ) return ;
				
				TurmoilNode p = (TurmoilNode) GlobalDB.nodes.randomElement();
				Event e = new Event3( p.key).init() ;
				if (p != null)
					p.publish( e ) ;

			}
		};
	}

	public void notify( Event e ) {
		Thread.dumpStack() ;
	}
	
	public void notify( Event e, List<?> path ) {
		Thread.dumpStack() ;
	}
	
	public void shutdown() {
		super.shutdown();
		
		if( state.joined )
			Statistics.evStats.accountDeadNode( this ) ;
		
		System.out.println(index + " exiting...");
	}

	public void publish() {
	}

	public void publish( Event ev ) {
		System.out.println( ev );
		Statistics.evStats.events.add(ev) ;
		
		Log.finest("Publishing:" + index + "/" + ev.serial);

		new PendingTreeDispatch(new TurmoilCast(0, id_counter++, new Range(), state.db.view, ev).append(this)).dispatch();
	}
	
	public int dynamicBroadcastFanout(int level) {
		if (level == 0)
			return 1;

		double adjust = 1 - (state.stats.turmoilTraffic.upload() - state.stats.turmoilTraffic.download()) / (1 + state.stats.turmoilTraffic.download());
				
		targetFanout = Math.max(2, Math.min(targetFanout * adjust, Config.PUBSUB_MAX_FANOUT));

		int floor = (int) targetFanout;
		return floor + (rg.nextDouble() < (targetFanout - floor) ? 1 : 0);
	}

	
	public void onReceive(Socket sock, TurmoilCast m) {
		
		assert this.accepts(m.payload);

		boolean inView = state.db.view.contains(m.view);

		if (state.db.loadedFilters && inView) {
			m.payload.inView++ ;
			socket.send(sock.src, new TurmoilAccept(key, m.id), 0);
		}
		else {
			m.payload.offView++ ;
			socket.send(sock.src, new TurmoilReject(key, m.id), 0);
			onReceive(sock, new TurmoilPayload(m.payload));
			return;
		}

		if( m.payload.duplicate( this.index ) ) {
			Thread.dumpStack() ;
			return;
		}
		
		onReceive( sock, new TurmoilPayload(m.payload) ) ;
		
		Range r = m.range;
//		Collection<CatadupaNode> P = m.level > 0 ? r.nodeList(state.db) : new ArrayList<CatadupaNode>() ;
		
		r = r.advancePast(this.key);
		
		Collection<CatadupaNode> L = r.nodeList(state.db);
		assert !L.contains(this);

//		P.removeAll(L);
//		for (CatadupaNode i : P) {
//			if( i.key != this.key ) {
//				m.payload.evaluatedBy( this, i ) ;
//				if( i.accepts( m.payload ) ) {
//					socket.send(i.endpoint, new TurmoilPayload(m.payload).append(m.path, this), 0);
//					m.payload.forwardedBy( this ) ;
//				}
//			}
//		}
		int fanout = dynamicBroadcastFanout(m.level) ;
		if (L.size() <= fanout) {
			for (CatadupaNode i : L) {	
				if( i.key != this.key ) {
					m.payload.evaluatedBy( this, i ) ;
					if( i.accepts( m.payload ) ) {
						socket.send(i.endpoint, new TurmoilPayload(m.payload).append(m.path, this), 0);
						m.payload.forwardedBy( this ) ;
					}
				}
			}
		} else {
			for (Range s : r.sliceNodes(fanout, state.db)) {
				new PendingTreeDispatch(new TurmoilCast(m.level + 1, id_counter++, s, m.view, m.payload).append(m.path, this)).dispatch();
			}
		}
	}

	
	public void onReceive(Socket sock, TurmoilPayload m) {
		m.payload.notify( this, m.path ) ;
	}

	

	public void onReceive(Socket sock, TurmoilAccept m) {
		Statistics.evStats.inView_events++;
		treeDispatchQueue.remove(m.id);
	}

	public void onReceive(Socket sock, TurmoilReject m) {
		Statistics.evStats.offView_events++;

		PendingTreeDispatch x = treeDispatchQueue.get(m.id);
		if (x != null) {
			x.advancePast(m.key) ;
			x.dispatch();
		}
	}

	Map<Integer, PendingTreeDispatch> treeDispatchQueue = new HashMap<Integer, PendingTreeDispatch>();

	static {
//		new PeriodicTask(0.1) {
//			public void run() {
//				for( CatadupaNode i : GlobalDB.liveNodes() ) {
//					TurmoilNode j = (TurmoilNode) i ;
//					if( j.state.joined && j.treeDispatchQueue.size() > 0 ) {
//						for (PendingTreeDispatch k : new ArrayList<PendingTreeDispatch>(j.treeDispatchQueue.values())) {
//							k.process();
//						}						
//					}
//				}
//			}
//		};
	}

	class PendingTreeDispatch {

		final TurmoilCast m;
		double sndStamp = -1;
		double ageStamp = currentTime();

		long lastKey = -1L ;
		
		PendingTreeDispatch(TurmoilCast m) {
			this.m = m;
			treeDispatchQueue.put(m.id, this);
		}

		double age() {
			return currentTime() - ageStamp;
		}

		void advancePast( long k ) {
			if( k >= 0 )
				m.range = m.range.advancePast(k ) ;
		}
		
		void dispatch() {
			for (CatadupaNode i : m.range.nodes(state.db)) {
				m.payload.evaluatedBy( TurmoilNode.this, i ) ;
				if (i.accepts(m.payload)) {
					if( i.key != key)
						m.payload.forwardedBy( TurmoilNode.this ) ;

					m.range.advancePast( i.key + 1L ) ;
					socket.send(i.endpoint, m, 0);
					sndStamp = currentTime();
					lastKey = i.key ;
					return ;
				}
			}
			treeDispatchQueue.remove(m.id);
		}

		void process() {

			if (currentTime() - sndStamp > 20) {
				advancePast( lastKey ) ;
				dispatch();
			}
		}

		public int hashCode() {
			return m.id;
		}

		public boolean equals(Object other) {
			return this.getClass() == other.getClass() && this.equals((PendingTreeDispatch) other);
		}

		public boolean equals(PendingTreeDispatch other) {
			return this.m.id == other.m.id;
		}

		public String toString() {
			return m.id + ": " + m.payload.serial;
		}
	}
}