package example.chord.msgs;


import static simsim.core.Simulation.rg;

import simsim.core.EndPoint;
import simsim.core.Message;
import simsim.core.MessageHandler;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.HSB;
import simsim.gui.canvas.Pen;
import simsim.gui.geom.Circle;
import simsim.gui.geom.QuadCurve;
import simsim.gui.geom.XY;

import example.chord.Node;

public class ChordMessage extends Message {
	
	
	public long dst ;
	final public EndPoint src ;
	final public double timeStamp  ;

	private int hopCount = 1 ;
	
	public ChordMessage( EndPoint src, long dst ) {
		this( src, dst, Simulation.currentTime() );
	}

	protected ChordMessage( EndPoint src, long dst, double ts ) {
		super(true, new HSB( rg.nextFloat(), 0.6, 0.6));
		this.dst = dst ;
		this.src = src ;
		this.timeStamp = ts ;
	}

	public ChordMessage( ChordMessage other ) {
		this( other.src, other.dst, other.timeStamp ) ;
		this.color = other.color ;
		this.hopCount = other.hopCount + 1 ;		
	}
	
	/* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((ExtendedMessageHandler)handler).onReceive( src, this ) ;
	}
	
	public void displayOn( Canvas canvas, EndPoint src, EndPoint dst, double t, double p) {
		
		Node a = (Node) src.handler ;
		Node b = (Node) dst.handler ;

		XY m = a.pos.add( b.pos).mult( 0.5) ;    	
		XY c = new XY( m.x + (500-m.x) / hopCount, m.y + (500 - m.y) / hopCount) ;

		Pen pen = new Pen( color, t < -0.1 ? 0.5 : 5.0)  ;
		pen.useOn(canvas.gs) ;
		
    	QuadCurve qc = new QuadCurve( a.pos, c, b.pos) ;
    	canvas.sDraw( qc ) ;

    	if( t >= 0)
      		canvas.sFill( new Circle( qc.interpolate( p), 16) ) ;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
