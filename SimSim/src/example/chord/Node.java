package example.chord;

import simsim.core.AbstractNode;
import simsim.core.Displayable;
import simsim.core.EndPoint;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Line;
import simsim.gui.geom.QuadCurve;
import simsim.gui.geom.XY;

import example.chord.msgs.ChordMessage;
import example.chord.msgs.ExtendedMessageHandler;

public class Node extends AbstractNode implements ExtendedMessageHandler, Displayable {

	public long key;

	public XY pos;
	public Line shape;

	ChordRoutingTable rtable;

	public Node() {
		super();
		key = NodeDB.store(this);
		rtable = new ChordRoutingTable(this);

		final double R = 450.0;

		double a = key * 2 * Math.PI / NodeDB.KEY_RANGE - Math.PI / 2;
		pos = new XY(500 + R * Math.cos(a), 500 + R * Math.sin(a));
		shape = new Line(pos.x, pos.y, pos.x + 1, pos.y);
	}

	// Populate the node's routing table.
	public void init() {
		rtable.populate();
		super.setColor(RGB.GREEN);
	}

	public void routeTo( long dst) {
		onReceive(endpoint, new ChordMessage(endpoint, dst));
	}

	public void displayOn( Canvas canvas ) {
		canvas.sDraw(shape);
	}

	
	public String toString() {
		return Long.toString(key);
	}

	
	public void onReceive(EndPoint src, ChordMessage m) {
		
		EndPoint nextHop = rtable.nextHop( m.dst);
		if (nextHop != null && nextHop != this.endpoint) {
//			Node x = (Node) nextHop.handler;
			//System.out.printf("At:%d -> dst: %d relay-> %d\n", key, m.dst, x.key);
			this.udpSend(nextHop, new ChordMessage(m));
		} else {
			System.out.printf("Stopped at: %d-> dst: %d\n", key, m.dst);
			
			double stretch = 100.0 * ( currentTime() - m.timeStamp ) / (src.latency( endpoint ) ) ;
			Main.stretch.tally(stretch, 1.0 ) ;
		}
	}

	/* Implements the Chord Routing Table */
	class ChordRoutingTable implements Displayable {
		final long ownKey;

		final RTableEntry[] fingers = new RTableEntry[NodeDB.KEY_LENGTH];

		ChordRoutingTable(Node owner) {
			this.ownKey = owner.key;
		}

		void populate() {
			long j = 2 ;
			for( int i = 0 ; i < NodeDB.KEY_LENGTH ; i++ ) {
				Node f = NodeDB.succ( (key + NodeDB.KEY_RANGE / j) % NodeDB.KEY_RANGE ) ;
				fingers[i] = new RTableEntry(f.key, f.endpoint ) ;
				j *= 2 ;
			}
			//System.out.println( key + "/" + fingers[0].key ) ;
			//dump() ;
		}

		EndPoint nextHop(long dst) {
			if (dst != ownKey) {
				long d = distanceBetween(dst, ownKey);
				for( RTableEntry i : fingers ) {
//					long x = distanceBetween(dst, i.key) ;
					//System.out.println(i.key + "--->" + dst + "=" + d + " / " + x );
					
					if (i != null && distanceBetween(dst, i.key) < d)
						return i.endpoint;
				}
			}
			return null;
		}
		
		long distanceBetween( long a, long b ) {
			long diff = a - b ;
			return diff >= 0 ? diff : diff + NodeDB.KEY_RANGE ;
		}
		
		void dump() {
			System.out.println("RTable for :" + ownKey);
			for (RTableEntry i : fingers)
				System.out.printf("%d\n", i.key);

		}

		class RTableEntry {
			long key;
			EndPoint endpoint;

			RTableEntry(long k, EndPoint e) {
				key = k;
				endpoint = e;
			}
		}

		
		final Pen pen = new Pen( RGB.DARK_GRAY, 3) ;
		
		public void displayOn( Canvas canvas ) {
			pen.useOn( canvas.gs) ;
			for( RTableEntry i : fingers ) {
				Node other = (Node) i.endpoint.handler ;
				canvas.sFill( new Circle( other.pos, 10 ) ) ;
			}
			
			int k = 1 ;
			final XY M = new XY(500,500);
			for( RTableEntry i : fingers ) {
				Node j = (Node)i.endpoint.handler ;

				double t = 1.0 / (1.0 + 0.33*Math.pow(k, 1.75) ) ;

				XY m1 = new Line( M, pos ).interpolate(t) ;
				XY m2 = new Line( M, j.pos).interpolate(t) ;
				
				XY c = m1.add( m2 ).mult( 0.5 ) ;				
				canvas.sDraw( new QuadCurve(pos, c, j.pos) ) ;
				
				k++ ;
			}
		}
	}
}
