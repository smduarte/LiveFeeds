package simsim.net.euclidean;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

import simsim.core.Globals;
import simsim.core.MessageHandler;
import simsim.core.NetAddress;
import simsim.core.Network;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.XY;

public class EuclideanNetwork extends Network {

	private Random random ;
	private float fontSize ;
	private double minCost;
	private double costFactor;
	private double nodeRadius ;
	private boolean displayLabels ;
	private double minNodeDistance;
	private boolean toggleNodeShape;
	private double squareSideLength ;
	
	private final WeakHashMap<NetAddress, Integer> deadNodes = new WeakHashMap<NetAddress, Integer>();
	private final HashSet<EuclideanAddress> liveNodes = new HashSet<EuclideanAddress>();

	public Network init() {
		long seed = Globals.get("Net_RandomSeed", 0L ) ;
		setRandomSeed( seed ) ;

		this.fontSize = Globals.get("Net_FontSize", 12.0f) ;
		this.nodeRadius = Globals.get("Net_Euclidean_NodeRadius", 10.0) ;
		this.costFactor = Globals.get("Net_Euclidean_CostFactor", 0.00001) ;
		this.minCost = Globals.get("Net_Euclidean_MinCost", 0.000) ;
		this.displayLabels = Globals.get("Net_Euclidean_DisplayNodeLabels", false ) ;
		this.minNodeDistance = Globals.get("Net_Euclidean_MinimumNodeDistance", 15.0) ;
		this.toggleNodeShape = Globals.get("Net_Euclidean_ToggleDrawNodeMode", false ) ;	
		this.squareSideLength = Globals.get("Net_Euclidean_SquareSideLength", 1000.0 ) ;
		
		return this ;
	}
	
	public void setRandomSeed( long seed ) {
		random = seed == 0L ? new Random() : new Random( seed ) ;		
	}

	public NetAddress createAddress( MessageHandler handler) {
		EuclideanAddress res = new EuclideanAddress(handler, getRandomPosition());
		liveNodes.add( res);
		return res;
	}

	public NetAddress replaceAddress( NetAddress addr) {
		EuclideanAddress res = new EuclideanAddress( addr.endpoint.handler, addr.pos);
		liveNodes.add( res ) ;
		return res;
	}

	public void disposeOf( NetAddress addr ) {
		liveNodes.remove( addr ) ;
		deadNodes.put( addr, 0 ) ;
	}

	final Pen pen = new Pen( RGB.BLACK, 0.5) ;
	public void displayOn( Canvas canvas ) {
		canvas.sFont( fontSize ) ;
		pen.useStrokeOn(canvas.gs) ;

		for( EuclideanAddress i : liveNodes )
			i.displayOn( canvas ) ;
		
		for (Map.Entry<NetAddress, Integer> i : deadNodes.entrySet()) {
			NetAddress n = i.getKey();
			if (n != null)
				n.displayOn( canvas );
		}
	}
	
	private XY getRandomPosition() {
		int tries = 0 ;
		Searching: for (;;) {
			if( ++tries % 99 == 0 ) {
				//System.err.println("Euclidean network plane too crowded...") ;
				//System.err.println("Reducing minumum allowed node distance by 15%") ;
				minNodeDistance *= 0.85 ;
			}
			XY pos = new XY( random).scale( squareSideLength );
			if (minNodeDistance > 0)
				for (EuclideanAddress i : liveNodes )
					if (i != null && i.isOnline() && pos.distance(i.pos) < minNodeDistance)
						continue Searching;

			return pos;
		}
	}

	public Set<? extends NetAddress> addresses() {
		return Collections.unmodifiableSet( liveNodes );
	}

	class EuclideanAddress extends NetAddress {

		EuclideanAddress(MessageHandler handler, XY pos) {
			super(handler);
			this.pos = pos;
			this.shape = new Circle( pos, nodeRadius ) ;			
		}

		@Override
		public double latency(NetAddress other) {
			return this == other ? 0 : pos.distance(((EuclideanAddress) other).pos) * costFactor + minCost;
		}

		public void displayOn( Canvas canvas ) {
			canvas.gs.setColor(color);
			if( shape.x != pos.x || shape.y != pos.y )
				shape = new Circle( pos, nodeRadius ) ;
			
			if (online && !toggleNodeShape )
				canvas.sFill( shape );
			else
				canvas.sDraw( shape );

			if( online && displayLabels ) {
				canvas.gs.setColor( RGB.black );
				canvas.sDraw( super.toString(), shape.x, shape.y - 0.5 * shape.height);
			}
		}

		@Override
		public NetAddress replace() {
			this.dispose() ;
			return replaceAddress( this);
		}
		
		public void dispose() {
			super.dispose() ;
			disposeOf( this ) ;
		}
		private Circle shape ; 
	}
}