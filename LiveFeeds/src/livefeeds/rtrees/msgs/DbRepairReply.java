package livefeeds.rtrees.msgs;

import static livefeeds.rtrees.config.Config.Config;

import java.util.List;

import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.View;
import livefeeds.rtrees.rpcs.RpcCall;
import livefeeds.rtrees.rpcs.RpcReplyHandler;

@SuppressWarnings("serial")
public class DbRepairReply extends CatadupaMessage {
	
	public View view;
	public List<NewArrivals> data ;
	
	public DbRepairReply( List<NewArrivals> data, View view ) {
		super(false) ;
		this.data = data ;
		this.view = view.clone() ;
	}

	public int length() {
		if (length < 0) {
			if (accountTraffic) {
				final double w = Config.DB_FILTER_REDUNDANCY ;
				length = view.length();
				for (NewArrivals i : data) {
					length += 4 + i.stamp.length() + i.rejoins.size() * (Config.ENDPOINT_SIZE + Config.FILTER_KEY_SIZE) ;
					length += i.joins.size() * ( Config.ENDPOINT_SIZE + (1-w) * Config.FILTER_DATA_SIZE + w * Config.FILTER_KEY_SIZE ) ;
				}
			} else
				length = 0;
		}
		return length;
	}

		
	public void deliverTo( RpcCall call, RpcReplyHandler handler) {
		
		if( call.src != call.dst ) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			
			double srcUpload = bytes * (1 + Config.TcpAckOverhead) ;
			double dstUpload = bytes * Config.TcpAckOverhead ;
				
			srcNode.state.stats.traffic.dbRepairReply_upload += srcUpload ;		
			dstNode.state.stats.traffic.dbRepairReply_download += srcUpload ;
			
			dstNode.state.stats.traffic.dbRepairReply_upload += dstUpload; 
			srcNode.state.stats.traffic.dbRepairReply_download += dstUpload ;
		}
		
		if( call.replyable )
			((CatadupaRpcReplyHandler) handler).onReply( call, this ) ;			
		else
			((CatadupaRpcReplyHandler) handler).onReply( this ) ;
	}

	public static boolean accountTraffic = true ;
}
