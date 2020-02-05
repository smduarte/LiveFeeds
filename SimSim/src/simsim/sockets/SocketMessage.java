package simsim.sockets;

import simsim.core.Globals;
import simsim.core.Message;
import simsim.gui.canvas.RGB;

public class SocketMessage extends Message {

	private static final long serialVersionUID = 1L;

	public SocketMessage() {
		super( false, RGB.GRAY) ;
	}
	
	public SocketMessage( boolean visible, RGB color) {
		super( visible, color ) ;
	}
	/**
	 * This method has to be overridden in all message sub-types, derived from this class.
	 * 
	 * @param src - Source of the rpc call.
	 * @param handler - Handler of the message.
	 */
	public void deliverTo( Socket sock, SocketHandler handler ) {
		throw new RuntimeException("deliverTo() not overridden... in: " + this.getClass() ) ;
	}

	public void deliverTo( SocketReplyHandler handler ) {
		throw new RuntimeException("deliverTo() not overridden... in: " + this.getClass() ) ;
	}
	
	public void deliverTo( Socket sock, SocketReplyHandler handler ) {
		throw new RuntimeException("deliverTo() not overridden... in: " + this.getClass() ) ;
	}
	
	public void accountTransfer( Socket call, double srcUpload, double dstUpload ) {		
	}
	
	static double tcpHeaderLength = Globals.get("Net_TcpHeaderLength", 40.0 ) ;
	static double tcpHeaderOverhead = Globals.get("Net_TcpHeaderOverhead", 0.025 ) ;	
}
