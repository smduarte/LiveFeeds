package simsim.sockets;

import simsim.core.EndPoint;


@SuppressWarnings("serial")
abstract public class Socket extends SocketMessage {

	final public EndPoint src, dst;
	final public boolean replyable ;
	
	protected Socket( EndPoint src, EndPoint dst, boolean replyable ) {
		this.src = src ; this.dst = dst ;
		this.replyable = replyable ;
	}
	
	public int length() {
		return length ;
	}
	
	abstract public <T> T srcNode() ;
	
	abstract public <T> T dstNode() ;
	
	abstract public void reply( SocketMessage m ) ;
	
	abstract public void reply( SocketMessage m, SocketReplyHandler h ) ;

	abstract public void reply( SocketMessage m, double appDelay ) ;
	
	abstract public void reply( SocketMessage m, double appDelay, SocketReplyHandler h ) ;

}
