package simsim.sockets;


import simsim.core.EndPoint;
import simsim.core.Message;
import simsim.core.MessageHandler;
import simsim.core.TcpChannel;

public class SocketFactory implements MessageHandler {

	SocketHandler handler;
	public EndPoint endpoint;
	MessageHandler failureHandler ;
	
	public SocketFactory(EndPoint endpoint, SocketHandler handler, MessageHandler failureHandler) {
		this.handler = handler;
		this.endpoint = endpoint;
		this.endpoint.setHandler( this ) ;
		this.failureHandler = failureHandler ;
	}

	public double eta( int bytes ) {
		return endpoint.bm.expectedDelay( bytes + 1) ;
	}
	
	public boolean request(EndPoint dst, final SocketMessage m, final SocketReplyHandler replyHandler) {
		return this.request(dst, m, 0, replyHandler) ;
	}

	public boolean request(EndPoint dst, final SocketMessage m, double appDelay, final SocketReplyHandler replyHandler) {
		return send( dst, m, appDelay, replyHandler) ;
	}

	public boolean request(EndPoint dst, final SocketMessage m, double appDelay) {
		return send( dst, m, appDelay ) ;
	}

	public boolean send(EndPoint dst, final SocketMessage m, final SocketReplyHandler replyHandler) {
		return this.request(dst, m, 0, replyHandler) ;
	}

	public boolean send(EndPoint dst, final SocketMessage m, double appDelay, final SocketReplyHandler replyHandler) {
		dst = dst.address.endpoints[endpoint.port] ;
		return endpoint.rawSend( dst, new RPC_Request(endpoint, dst, m, replyHandler), appDelay);
	}
	
	public boolean send(EndPoint dst, final SocketMessage m, double appDelay) {
		dst = dst.address.endpoints[endpoint.port] ;
		return endpoint.rawSend( dst, new RPC_Request(endpoint, dst, m, null), appDelay);
	}

	public void onSendFailure(EndPoint dst, Message m) {
		if (m instanceof RPC_Request) {
			RPC_Request call = (RPC_Request) m;
			if( call.replyHandler != null ) {
				call.replyHandler.onFailure();
			}
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

class RPC_Request extends Socket {

	SocketMessage payload;
	SocketReplyHandler replyHandler;

	
	RPC_Request(EndPoint src, EndPoint dst, SocketMessage payload, SocketReplyHandler replyHandler) {
		super(src, dst, true);
		this.payload = payload;
		this.length = payload.length();			
		this.replyHandler = replyHandler;
	}

	public void deliverTo(EndPoint src, MessageHandler handler) {
		if( length > 0 ) {
			double dstUpload = length * tcpHeaderOverhead + (replyHandler != null ? 1 : 2) * tcpHeaderLength ; 
			double srcUpload = length * (1 + tcpHeaderOverhead ) + (replyHandler != null ? 2 : 4) * tcpHeaderLength ; 

			src.address.uploadedBytes += srcUpload ;
			src.address.downloadedBytes += dstUpload ;

			dst.address.uploadedBytes += dstUpload ;
			dst.address.downloadedBytes += srcUpload ;

			//System.out.println( src + " -> " + dst + " REQ: " + payload.getClass() + "<" + length + "->>" + srcUpload + ", " + dstUpload + ">");

			if( src != dst )
				payload.accountTransfer(this, srcUpload, dstUpload) ;
		}
		payload.deliverTo(this, ((SocketFactory) handler).handler);
	}

	public void reply(SocketMessage m) {
		reply( m, 0 ) ;
	}

	public void reply(SocketMessage m, SocketReplyHandler replyHandler2) {
		reply( m, 0, replyHandler2 ) ;
	}
	
	public void reply(SocketMessage m, double appDelay ) {
		dst.rawSend(src, new RPC_ReplySansReply(dst, src, m, replyHandler));
	}

	public void reply(SocketMessage m, double appDelay, SocketReplyHandler replyHandler2) {
		dst.rawSend(src, new RPC_ReplyWithReply(dst, src, m, replyHandler, replyHandler2 ));
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

class RPC_ReplyWithReply extends Socket {

	SocketMessage payload;
	SocketReplyHandler replyHandler;
	SocketReplyHandler replyHandler2;

	RPC_ReplyWithReply(EndPoint src, EndPoint dst, SocketMessage payload, SocketReplyHandler replyHandler, SocketReplyHandler replyHandler2 ) {
		super(src, dst, true);
		this.payload = payload;
		this.length = payload.length();
		this.replyHandler = replyHandler;
		this.replyHandler2 = replyHandler2;
	}

	public void deliverTo(EndPoint src, MessageHandler handler) {
		if( length > 0 ) {
			double dstUpload = length * tcpHeaderOverhead;
			double srcUpload = length * ( 1 + tcpHeaderOverhead );

			src.address.uploadedBytes += srcUpload ;
			src.address.downloadedBytes += dstUpload ;

			dst.address.uploadedBytes += dstUpload ;
			dst.address.downloadedBytes += srcUpload ;
			
			//System.out.println( src + " -> " + dst + " RwR: " + payload.getClass() + "<" + length + "->>" + srcUpload + ", " + dstUpload + ">");

			if( src != dst )
				payload.accountTransfer(this, srcUpload, dstUpload) ;
		}
		payload.deliverTo(this, replyHandler);
	}

	public void reply(SocketMessage m) {
		reply( m, 0) ;
	}

	public void reply(SocketMessage m, SocketReplyHandler replyHandler3) {
		reply( m, 0, replyHandler3 ) ;
	}

	
	public void reply(SocketMessage m, double appDelay ) {
		dst.rawSend(src, new RPC_ReplySansReply(dst, src, m, replyHandler2), appDelay);
	}

	public void reply(SocketMessage m, double appDelay, SocketReplyHandler replyHandler3) {
		dst.rawSend(src, new RPC_ReplyWithReply(dst, src, m, replyHandler2, replyHandler3), appDelay);
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

class RPC_ReplySansReply extends Socket {

	SocketMessage payload;
	SocketReplyHandler replyHandler;

	RPC_ReplySansReply(EndPoint src, EndPoint dst, SocketMessage payload, SocketReplyHandler replyHandler) {
		super(src, dst, false);
		this.payload = payload;
		this.length = payload.length();
		this.replyHandler = replyHandler;
	}

	public void deliverTo(EndPoint src, MessageHandler handler) {
		if( length > 0 ) {

			double dstUpload = 1 * tcpHeaderLength + length * tcpHeaderOverhead;
			double srcUpload = 2 * tcpHeaderLength + length * ( 1 + tcpHeaderOverhead );

			src.address.uploadedBytes += srcUpload ;
			src.address.downloadedBytes += dstUpload ;

			dst.address.uploadedBytes += dstUpload ;
			dst.address.downloadedBytes += srcUpload ;

			//System.out.println( src + " -> " + dst + " RsR: " + payload.getClass() + "<" + length + "->>" + srcUpload + ", " + dstUpload + ">");

			if( src != dst )
				payload.accountTransfer(this, srcUpload, dstUpload) ;
		}
		payload.deliverTo(this, replyHandler);
	}

	public void reply(SocketMessage m) {
	}

	public void reply(SocketMessage m, SocketReplyHandler replyHandler2) {
	}

	public void reply(SocketMessage m, double appDelay ) {
	}

	public void reply(SocketMessage m, double appDelay, SocketReplyHandler replyHandler2) {
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