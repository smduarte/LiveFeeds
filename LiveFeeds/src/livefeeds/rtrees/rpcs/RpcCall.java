package livefeeds.rtrees.rpcs;

import simsim.core.EndPoint;


@SuppressWarnings("serial")
abstract public class RpcCall extends RpcMessage {

	final public EndPoint src, dst;
	final public boolean replyable ;
	
	protected RpcCall( EndPoint src, EndPoint dst, boolean replyable ) {
		this.src = src ; this.dst = dst ;
		this.replyable = replyable ;
	}
	
	public int length() {
		return length ;
	}
	
	abstract public <T> T srcNode() ;
	
	abstract public <T> T dstNode() ;
	
	abstract public void reply( RpcMessage m ) ;
	
	abstract public void reply( RpcMessage m, RpcReplyHandler h ) ;

	abstract public void reply( RpcMessage m, double appDelay ) ;
	
	abstract public void reply( RpcMessage m, double appDelay, RpcReplyHandler h ) ;

}
