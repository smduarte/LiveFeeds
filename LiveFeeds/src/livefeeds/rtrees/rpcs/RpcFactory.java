package livefeeds.rtrees.rpcs;


import simsim.core.EndPoint;
import simsim.core.Message;
import simsim.core.MessageHandler;
import simsim.core.TcpChannel;

public class RpcFactory implements MessageHandler {

	RpcHandler handler;
	public EndPoint endpoint;
	MessageHandler failureHandler ;
	
	public RpcFactory(EndPoint endpoint, RpcHandler handler, MessageHandler failureHandler) {
		this.handler = handler;
		this.endpoint = endpoint;
		this.endpoint.setHandler( this ) ;
		this.failureHandler = failureHandler ;
	}

	public double eta( int bytes ) {
		return endpoint.bm.expectedDelay( bytes + 1) ;
	}
	
	public boolean request(EndPoint dst, final RpcMessage m, final RpcReplyHandler replyHandler) {
		return this.request(dst, m, 0, replyHandler) ;
	}

	public boolean send(EndPoint dst, final RpcMessage m, final RpcReplyHandler replyHandler) {
		return this.request(dst, m, 0, replyHandler) ;
	}

	public boolean request(EndPoint dst, final RpcMessage m, double appDelay, final RpcReplyHandler replyHandler) {
		dst = dst.address.endpoints[endpoint.port] ;
		return endpoint.udpSend( dst, new RPC_Request(endpoint, dst, m, replyHandler), appDelay);
	}

	public boolean send(EndPoint dst, final RpcMessage m, double appDelay, final RpcReplyHandler replyHandler) {
		dst = dst.address.endpoints[endpoint.port] ;
		return endpoint.udpSend( dst, new RPC_Request(endpoint, dst, m, replyHandler), appDelay);
	}

	public boolean request(EndPoint dst, final RpcMessage m, double appDelay) {
		dst = dst.address.endpoints[endpoint.port] ;
		return endpoint.udpSend( dst, new RPC_Request(endpoint, dst, m, null), appDelay);
	}
	
	public boolean send(EndPoint dst, final RpcMessage m, double appDelay) {
		dst = dst.address.endpoints[endpoint.port] ;
		return endpoint.udpSend( dst, new RPC_Request(endpoint, dst, m, null), appDelay);
	}

	public void onSendFailure(EndPoint dst, Message m) {
		if (m instanceof RPC_Request) {
			RPC_Request call = (RPC_Request) m;
			if( call.replyHandler != null )
				call.replyHandler.onFailure();
		}

		failureHandler.onSendFailure( dst, m) ;
	}

	public void onReceive(EndPoint src, Message m) {
		Thread.dumpStack();
	}

	public void onReceive(TcpChannel chn, Message m) {
		Thread.dumpStack();
	}

	public String toString() {
		return "" + handler;
	}
}

class RPC_Request extends RpcCall {

	RpcMessage payload;
	RpcReplyHandler replyHandler;

	RPC_Request(EndPoint src, EndPoint dst, RpcMessage payload, RpcReplyHandler replyHandler) {
		super(src, dst, true);
		this.payload = payload;
		this.length = payload.length();
		this.replyHandler = replyHandler;
	}

	public void deliverTo(EndPoint src, MessageHandler handler) {
		payload.deliverTo(this, ((RpcFactory) handler).handler);
	}

	public void reply(RpcMessage m) {
		reply( m, 0 ) ;
	}

	public void reply(RpcMessage m, RpcReplyHandler replyHandler2) {
		reply( m, 0, replyHandler2 ) ;
	}
	
	public void reply(RpcMessage m, double appDelay ) {
		dst.udpSend(src, new RPC_ReplySansReply(dst, src, m, replyHandler));
	}

	public void reply(RpcMessage m, double appDelay, RpcReplyHandler replyHandler2) {
		dst.udpSend(src, new RPC_ReplyWithReply(dst, src, m, replyHandler, replyHandler2));
	}

	@SuppressWarnings("unchecked")
	public <T> T srcNode() {
		return (T) src.address.endpoint.handler;
	}

	@SuppressWarnings("unchecked")
	public <T> T dstNode() {
		return (T) dst.address.endpoint.handler;
	}

	private static final long serialVersionUID = 1L;
}

class RPC_ReplyWithReply extends RpcCall {

	RpcMessage payload;
	RpcReplyHandler replyHandler;
	RpcReplyHandler replyHandler2;

	RPC_ReplyWithReply(EndPoint src, EndPoint dst, RpcMessage payload, RpcReplyHandler replyHandler, RpcReplyHandler replyHandler2) {
		super(src, dst, true);
		this.payload = payload;
		this.length = payload.length();
		this.replyHandler = replyHandler;
		this.replyHandler2 = replyHandler2;
	}

	public void deliverTo(EndPoint src, MessageHandler handler) {
		payload.deliverTo(this, replyHandler);
	}

	public void reply(RpcMessage m) {
		reply( m, 0) ;
	}

	public void reply(RpcMessage m, RpcReplyHandler replyHandler3) {
		reply( m, 0, replyHandler3 ) ;
	}

	
	public void reply(RpcMessage m, double appDelay ) {
		dst.udpSend(src, new RPC_ReplySansReply(dst, src, m, replyHandler2), appDelay);
	}

	public void reply(RpcMessage m, double appDelay, RpcReplyHandler replyHandler3) {
		dst.udpSend(src, new RPC_ReplyWithReply(dst, src, m, replyHandler2, replyHandler3), appDelay);
	}

	@SuppressWarnings("unchecked")
	public <T> T srcNode() {
		return (T) src.address.endpoint.handler;
	}

	@SuppressWarnings("unchecked")
	public <T> T dstNode() {
		return (T) dst.address.endpoint.handler;
	}

	private static final long serialVersionUID = 1L;
}

class RPC_ReplySansReply extends RpcCall {

	RpcMessage payload;
	RpcReplyHandler replyHandler;

	RPC_ReplySansReply(EndPoint src, EndPoint dst, RpcMessage payload, RpcReplyHandler replyHandler) {
		super(src, dst, false);
		this.payload = payload;
		this.length = payload.length();
		this.replyHandler = replyHandler;
	}

	public void deliverTo(EndPoint src, MessageHandler handler) {
		payload.deliverTo(this, replyHandler);
	}

	public void reply(RpcMessage m) {
	}

	public void reply(RpcMessage m, RpcReplyHandler replyHandler2) {
	}

	public void reply(RpcMessage m, double appDelay ) {
	}

	public void reply(RpcMessage m, double appDelay, RpcReplyHandler replyHandler2) {
	}

	@SuppressWarnings("unchecked")
	public <T> T srcNode() {
		return (T) src.address.endpoint.handler;
	}

	@SuppressWarnings("unchecked")
	public <T> T dstNode() {
		return (T) dst.address.endpoint.handler;
	}

	private static final long serialVersionUID = 1L;
}