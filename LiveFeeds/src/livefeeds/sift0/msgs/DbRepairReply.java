package livefeeds.sift0.msgs;

import static livefeeds.sift0.config.Config.Config;

import java.util.List;

import livefeeds.sift0.CatadupaNode;
import livefeeds.sift0.View;

import simsim.sockets.Socket;
import simsim.sockets.SocketReplyHandler;

@SuppressWarnings("serial")
public class DbRepairReply extends CatadupaMessage {
	
	public View view;
	public List<CatadupaCastPayload> data ;
	
	public DbRepairReply( List<CatadupaCastPayload> data, View view ) {
		super(false) ;
		this.data = data ;
		this.view = view.clone() ;
	}

	public int length() {
		if (length < 0) {
			if (accountTraffic) {
				final double w = Config.DB_FILTER_REDUNDANCY ;
				length = view.length();
				for (CatadupaCastPayload i : data) {
					length += 4 + i.stamp.length() + i.rejoins.size() * (Config.ENDPOINT_SIZE + Config.FILTER_KEY_SIZE) ;
					length += i.joins.size() * ( Config.ENDPOINT_SIZE + (1-w) * Config.FILTER_DATA_SIZE + w * Config.FILTER_KEY_SIZE ) ;
				}
			} else
				length = 0;
		}
		return length;
	}

		
	public void deliverTo( Socket call, SocketReplyHandler handler) {
		
		if( call.src != call.dst ) {
			CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();

			int bytes = length();
			
			double srcUpload = bytes * (1 + Config.TcpAckOverhead) ;
			double dstUpload = bytes * Config.TcpAckOverhead ;
				
			srcNode.state.stats.catadupaTraffic.dbRepairReply_upload += srcUpload ;		
			dstNode.state.stats.catadupaTraffic.dbRepairReply_download += srcUpload ;
			
			dstNode.state.stats.catadupaTraffic.dbRepairReply_upload += dstUpload; 
			srcNode.state.stats.catadupaTraffic.dbRepairReply_download += dstUpload ;
		}
		
		if( call.replyable )
			((CatadupaSocketcReplyHandler) handler).onReply( call, this ) ;			
		else
			((CatadupaSocketcReplyHandler) handler).onReply( this ) ;
	}

	public static boolean accountTraffic = true ;
}
