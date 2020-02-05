package livefeeds.twister6;

import static livefeeds.twister6.config.Config.Config;
import static simsim.logging.Log.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import livefeeds.twister6.msgs.TurmoilAccept;
import livefeeds.twister6.msgs.TurmoilCast;
import livefeeds.twister6.msgs.TurmoilPayload;
import livefeeds.twister6.msgs.TurmoilReject;
import livefeeds.twister6.msgs.TurmoilSocketHandler;

import simsim.core.PeriodicTask;
import simsim.sockets.Socket;
import simsim.sockets.SocketFactory;

public class TurmoilNode extends CatadupaNode implements TurmoilSocketHandler {

	private int id_counter = 0;

	public double targetFanout = Config.PUBSUB_MAX_FANOUT ;
	
	protected final SocketFactory socket;

	protected TurmoilNode() {
		super();
		state.filter = new Filter();
		socket = new SocketFactory(address.endpoint(3, this), this, this);
	}

	public void init() {
		super.init();
	}

	@Override
	public boolean accepts(Event e) {
		return state.joined && state.filter.accepts(e) ;
	}

	public boolean notify( Event e ) {
		Thread.dumpStack() ;
		return true ;
	}
	
	public boolean notify( Event e, List<?> path ) {
		System.out.println( e.getClass() );
		Thread.dumpStack() ;
		return true ;
	}
	
	public void shutdown() {
		super.shutdown();
			
		System.out.println(index + " exiting...");
	}

	public void publish() {
	}

	public void publish( Event ev ) {

		Log.finest("Publishing:" + index + "/" + ev.serial);

		new PendingTreeDispatch( new TurmoilCast(0, id_counter++, new Range(), state.db.view, ev).append(this));
	}
		
	public void onReceive(Socket sock, TurmoilCast m) {
		
		assert this.accepts(m.payload);

		boolean inView = state.db.view.contains(m.view);

		if (state.db.loadedFilters && inView) {
			socket.send(sock.src, new TurmoilAccept(key, m.id), 0);
		}
		else {
			socket.send(sock.src, new TurmoilReject(key, m.id), 0);
			onReceive(sock, new TurmoilPayload(m.payload));
			return;
		}
		
		if( ! m.payload.notify( this, m.path )  )
			return ;
		
		Range r = m.range;		
		r = r.advancePast(this.key);
		
		Collection<CatadupaNode> L = r.nodeList(state.db);
		assert !L.contains(this);

		if (L.size() <= Config.PUBSUB_MAX_FANOUT) {
			for (CatadupaNode i : L) {	
				if( i.key != this.key ) {
					if( i.accepts( m.payload ) ) {
						socket.send(i.endpoint, new TurmoilPayload(m.payload).append(m.path, this), 0);
					}
				}
			}
		} else {
			for (Range s : r.sliceNodes(Config.PUBSUB_MAX_FANOUT, state.db)) {
				new PendingTreeDispatch(new TurmoilCast(m.level + 1, id_counter++, s, m.view, m.payload).append(m.path, this)).dispatch();
			}
		}
	}

	
	public void onReceive(Socket sock, TurmoilPayload m) {
		m.payload.notify( this, m.path ) ;
	}

	

	public void onReceive(Socket sock, TurmoilAccept m) {
		treeDispatchQueue.remove(m.id);
	}

	public void onReceive(Socket sock, TurmoilReject m) {

		PendingTreeDispatch x = treeDispatchQueue.get(m.id);
		if (x != null) {
			x.advancePast(m.key) ;
			x.dispatch();
		}
	}

	Map<Integer, PendingTreeDispatch> treeDispatchQueue = new HashMap<Integer, PendingTreeDispatch>();

	static {
		new PeriodicTask(0.1) {
			public void run() {
				for( CatadupaNode i : GlobalDB.liveNodes() ) {
					TurmoilNode j = (TurmoilNode) i ;
					if( j.state.joined && j.treeDispatchQueue.size() > 0 ) {
						for (PendingTreeDispatch k : new ArrayList<PendingTreeDispatch>(j.treeDispatchQueue.values())) {
							k.process();
						}						
					}
				}
			}
		};
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
				if (i.accepts(m.payload)) {
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