package livefeeds.twister7.msgs;

import simsim.core.EndPoint;
import simsim.sockets.Socket;
import simsim.sockets.SocketReplyHandler;

@SuppressWarnings("serial")
public class DbUploadReject extends CatadupaMessage {

	public final int ttl;
	public final EndPoint src;

	public DbUploadReject() {
		this(null);
	}

	public DbUploadReject(EndPoint src) {
		this(src, 5);
	}

	protected DbUploadReject(EndPoint src, int ttl) {
		super(false);
		this.src = src;
		this.ttl = ttl;
	}

	public DbUploadReject decrementTTL() {
		return new DbUploadReject(src, ttl - 1);
	}

	public int length() {
		if (length < 0)
			length = 0;
		return length;
	}

	public void deliverTo(Socket call, SocketReplyHandler handler) {

		if (call.replyable)
			((CatadupaSocketcReplyHandler) handler).onReply(call, this);
		else
			((CatadupaSocketcReplyHandler) handler).onReply(this);
	}

}
