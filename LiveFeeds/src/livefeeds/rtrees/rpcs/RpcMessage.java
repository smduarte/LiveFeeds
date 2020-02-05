package livefeeds.rtrees.rpcs;

import simsim.core.Message;
import simsim.gui.canvas.RGB;

public class RpcMessage extends Message {

	private static final long serialVersionUID = 1L;

	public RpcMessage() {
		super( false, RGB.GRAY) ;
	}
	
	public RpcMessage( boolean visible, RGB color) {
		super( visible, color ) ;
	}
	/**
	 * This method has to be overridden in all message sub-types, derived from this class.
	 * 
	 * @param src - Source of the rpc call.
	 * @param handler - Handler of the message.
	 */
	public void deliverTo( RpcCall call, RpcHandler handler ) {
		throw new RuntimeException("deliverTo() not overridden... in: " + this.getClass() ) ;
	}

	public void deliverTo( RpcReplyHandler handler ) {
		throw new RuntimeException("deliverTo() not overridden... in: " + this.getClass() ) ;
	}
	
	public void deliverTo( RpcCall call, RpcReplyHandler handler ) {
		throw new RuntimeException("deliverTo() not overridden... in: " + this.getClass() ) ;
	}
}
