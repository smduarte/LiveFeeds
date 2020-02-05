package livefeeds.rtrees.msgs;

import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcReplyHandler;

import simsim.core.EndPoint;

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
			length = accountTraffic ? 1 : 0;
		return length;
	}

	public void deliverTo(RpcCall call, RpcReplyHandler handler) {

		if (call.src != call.dst) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			srcNode.state.stats.traffic.dbUploadReject_upload += bytes;
			dstNode.state.stats.traffic.dbUploadReject_download += bytes;
		}

		if (call.replyable)
			((CatadupaRpcReplyHandler) handler).onReply(call, this);
		else
			((CatadupaRpcReplyHandler) handler).onReply(this);
	}

	public static boolean accountTraffic = true;
}
