package livefeeds.rtrees;

import static livefeeds.rtrees.config.Config.Config;
import static simsim.core.Simulation.rg;

import java.awt.Shape;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import simsim.core.Displayable;
import simsim.core.EndPoint;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Line;
import simsim.gui.geom.QuadCurve;
import simsim.gui.geom.XY;

import umontreal.iro.lecuyer.stat.Tally;
import livefeeds.rtrees.msgs.BroadcastMessage;
import livefeeds.rtrees.msgs.BroadcastPayload;
import livefeeds.rtrees.msgs.BroadcastRpcHandler;
import livefeeds.rtrees.msgs.ChordBroadcastMessage;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcFactory;
import livefeeds.rtrees.rpcs.RpcMessage;
import static livefeeds.rtrees.stats.Statistics.Statistics;

public class BroadcastNode extends CatadupaNode implements BroadcastRpcHandler {

	final RpcFactory socket;

	public SlidingBitSet received = new SlidingBitSet();

	double targetFanout = Config.BROADCAST_MAX_FANOUT;

	ChordRoutingTable chordRT;

	BroadcastNode() {
		super();

		socket = new RpcFactory(address.endpoint(3, this), this, this);

		socket.endpoint.setBandwidthRates(32 * 1024, 32 * 1024);

//		chordRT = new ChordRoutingTable(this);
	}

	public void init() {
		//super.init();
		state.db.knownNodes.base = 1 << 30 ;
	}

	public void shutdown() {
//		super.shutdown();

//		for (CatadupaNode i : GlobalDB.liveNodes()) {
//			((BroadcastNode) i).chordRT.populate();
//		}
	}

	public void startBroadcast( int size ) {
		Range r0 = Config.BROADCAST_RANDOM_TREES ? new Range() : new Range.SK_Range();
		onReceive((RpcCall) null, new BroadcastMessage(0, r0, new BroadcastPayload(this.key, size )));
	}

	public void onReceive(RpcCall call, BroadcastMessage m) {

		final int BroadcastFanout = broadcastFanout();
		
		assert BroadcastFanout > 0 ;
		
		// Statistics.fanoutStats.recordFanout(BroadcastFanout);

		this.onReceive(call, m.payload);

		Range r0 = m.range;

		if (m.level > 0)
			r0 = r0.advancePast(this.key);

		if (r0.sizeGreaterThan(BroadcastFanout, state.db)) {
			for (Range j : r0.slice(m.level, BroadcastFanout, state.db)) {
				for (CatadupaNode i : j.nodes(state.db)) {
					if (i.key == m.payload.src)
						continue;

					if (socket.send(i.endpoint, new BroadcastMessage(m.level + 1, j, m.payload), 0))
						break;
				}
			}
		} else {
			for (CatadupaNode i : r0.nodes(state.db))
				if (i != this)
					socket.send(i.endpoint, m.payload, 0);
//			if (i != this && i.key != m.payload.src)
//				socket.send(i.endpoint, m.payload, 0);

		}
	}

	public void onReceive(RpcCall call, BroadcastPayload m) {
		Statistics.treeStats.recordLatency( currentTime() - Config.BROADCAST_START, m.latency() ) ;
		received.set(m.serial);
	}

	private final int broadcastFanout() {
		return Config.BROADCAST_USE_DYNAMIC_FANOUT ? dynamicBroadcastFanout() : Config.BROADCAST_MAX_FANOUT;
	}

	private int dynamicBroadcastFanout() {
		double TARGET_BROADCAST_ADJUST = 1 - (state.stats.btraffic.upload() - state.stats.btraffic.download()) / (1 + state.stats.btraffic.download()) ;
//		TARGET_BROADCAST_ADJUST = 0.05 ;
//		
//		if( state.stats.btraffic.upload() > state.stats.btraffic.download() )
//			TARGET_BROADCAST_ADJUST = 1 - TARGET_BROADCAST_ADJUST ;
//		else
//			TARGET_BROADCAST_ADJUST = 1 + TARGET_BROADCAST_ADJUST ;
		
//		System.out.println(targetFanout );
		
		targetFanout *= TARGET_BROADCAST_ADJUST ;
		targetFanout = Math.max(1.5, Math.min(targetFanout, Config.BROADCAST_MAX_FANOUT));

		int floor = (int) targetFanout;
		floor += (rg.nextDouble() < (targetFanout - floor) ? 1 : 0);

		return floor ;
	}


	// --------------------------------------------------------------------------------------------------------------------------
	public void doChordBroadcast() {
		onReceive( (RpcCall) null, new ChordBroadcastMessage(0, -10000) ) ;
	}
	
	public void onReceive(RpcCall call, ChordBroadcastMessage m) {	
		Set<EndPoint> sent = new HashSet<EndPoint>() ;
		
		for( int i = m.level ; i < chordRT.fingers.length - 1; i++ ) {
			EndPoint dst = chordRT.fingers[i].endpoint ; 
			if( ! sent.contains( dst ) ) {
				socket.send(dst, new ChordBroadcastMessage(i+1,  m.payload, m.serial), 0 ) ;
				sent.add( dst ) ;
			}
		}
		received.set( m.serial ) ;
	}

	class ChordRoutingTable implements Displayable {

		final BroadcastNode owner;
		final long KEY_RANGE = GlobalDB.MAX_KEY + 1L;

		final XY pos;

		final RTableEntry[] fingers = new RTableEntry[Config.NODE_KEY_LENGTH];
		final Shape shape;

		ChordRoutingTable(BroadcastNode owner) {
			this.owner = owner;

			final double R = 450.0;

			double a = owner.key * 2 * Math.PI / KEY_RANGE - Math.PI / 2;
			pos = new XY(500 + R * Math.cos(a), 500 + R * Math.sin(a));
			shape = new Line(pos.x, pos.y, pos.x + 1, pos.y);

		}

		void populate() {

			long j = 2;
			for (int i = 0; i < Config.NODE_KEY_LENGTH; i++) {
				long fingerKey = (owner.key + KEY_RANGE / j) % KEY_RANGE;

				BroadcastNode f = GlobalDB.succ(fingerKey + 1L);
				fingers[i] = new RTableEntry(f.key, f.endpoint, f.chordRT);
				j *= 2;
			}

		}

		EndPoint nextHop(long dst) {
			if (dst != owner.key) {
				long d = distanceBetween(dst, owner.key);
				for (RTableEntry i : fingers) {
					if (i != null && distanceBetween(dst, i.key) < d)
						return i.endpoint;
				}
			}
			return null;
		}

		long distanceBetween(long a, long b) {
			long diff = a - b;
			return diff > 0 ? diff : diff + (GlobalDB.MAX_KEY + 1L);
		}

		void dump() {
			System.out.println("RTable for :" + owner.key);
			for (RTableEntry i : fingers)
				System.out.printf("%d\n", i.key);

		}

		class RTableEntry {
			long key;
			EndPoint endpoint;
			ChordRoutingTable table;

			RTableEntry(long k, EndPoint e, ChordRoutingTable t) {
				key = k;
				table = t;
				endpoint = e;
			}
		}

		final Pen pen = new Pen(RGB.DARK_GRAY, 2);

		public void displayOn(Canvas canvas) {
			pen.useOn(canvas.gs);
			canvas.sFill(new Circle(pos, 5));
			for (RTableEntry i : fingers) {
				if (i != null)
					canvas.sFill(new Circle(i.table.pos, 10));
			}

			int k = 1;
			for (RTableEntry i : fingers)
				if (i != null) {
					XY m = pos.add(i.table.pos).mult(0.5);
					XY c = new XY(m.x + (500 - m.x) / k, m.y + (500 - m.y) / k);
					canvas.sDraw(new QuadCurve(pos, c, i.table.pos));
					k++;
				}
		}
	}
}
