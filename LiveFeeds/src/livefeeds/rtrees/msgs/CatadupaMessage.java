package livefeeds.rtrees.msgs;

import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.rpcs.RpcMessage;

import simsim.core.EndPoint;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.QuadCurve;
import simsim.gui.geom.XY;

@SuppressWarnings("serial")
public class CatadupaMessage extends RpcMessage {

	CatadupaMessage() {
		this(false) ;
	}
	
	CatadupaMessage( boolean visible ) {
		super( visible, RGB.GRAY ) ;
	}
	
	CatadupaMessage( boolean visible, RGB color ) {
		super( visible, color ) ;
	}
		
	public void displayOn( Canvas canvas, EndPoint src, EndPoint dst,  double t, double p) {
		CatadupaNode a = (CatadupaNode) src.handler ;
		CatadupaNode b = (CatadupaNode) dst.handler ;
    	
    	double x = (a.state.pos.x + b.state.pos.x) * 0.5 ;
    	double y = (a.state.pos.y + b.state.pos.y) * 0.5 ;
    	
    	XY c = new XY( x + (500 - x), y + (500-y)) ;
    	
    	canvas.sDraw( new Pen( color, 1), new QuadCurve( a.state.pos, c, b.state.pos) ) ;
	}
	
	
	public String toString() {
		return super.getClass().toString() ;
	}
}
