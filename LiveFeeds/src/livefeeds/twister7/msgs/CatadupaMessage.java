package livefeeds.twister7.msgs;

import static livefeeds.twister7.config.Config.Config;
import livefeeds.twister7.CatadupaNode;

import simsim.core.EndPoint;
import simsim.core.Globals;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.QuadCurve;
import simsim.gui.geom.XY;
import simsim.sockets.Socket;
import simsim.sockets.SocketMessage;

@SuppressWarnings("serial")
public class CatadupaMessage extends SocketMessage {

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
	
	final protected double srcUploadWithOverhead( boolean handshake, boolean teardown, double bytes ) {
		return bytes * (1 + Config.TcpAckOverhead) + ((handshake ? 2 : 0) + (teardown ? 2 : 0 ) ) * tcpHeaderLength ;
	}
	
	final protected double dstUploadWithOverhead( boolean handshake, boolean teardown, double bytes ) {
		return bytes * (0 + Config.TcpAckOverhead) + ((handshake ? 1 : 0) + (teardown ? 1 : 0 ) ) * tcpHeaderLength ;
	}
	
	
	final protected double srcUploadWithOverhead( boolean handshake, double bytes ) {
		return srcUploadWithOverhead( handshake, false, bytes ) ;
	}
	
	final protected double dstUploadWithOverhead( boolean handshake, double bytes ) {
		return dstUploadWithOverhead( handshake, false, bytes ) ;
	}
	
	final protected double srcUploadWithOverhead( double bytes ) {
		return srcUploadWithOverhead( true, false, bytes ) ;
	}
	
	final protected double dstUploadWithOverhead( double bytes ) {
		return dstUploadWithOverhead( true, false, bytes ) ;
	}
	
	public String toString() {
		return super.getClass().toString() ;
	}
	
	public void accountTransfer( Socket sock, double srcUpload, double dstUpload ) {		
		Thread.dumpStack();
	}

	static double tcpHeaderLength = Globals.get("Net_TcpHeaderLength", 40.0 ) ;
	static double tcpHeaderOverhead = Globals.get("Net_TcpHeaderOverhead", 0.025 ) ;	
}
