package livefeeds.twister7.msgs;

import static livefeeds.twister7.config.Config.Config;

import java.util.List;

import livefeeds.twister7.CatadupaNode;
import livefeeds.twister7.View;

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
	
	public int bytes() {
		final double w = Config.DB_FILTER_REDUNDANCY ;
		int res = view.length();
		for (CatadupaCastPayload i : data) {
			length += 4 + i.stamp.length() + i.rejoins.size() * (Config.ENDPOINT_SIZE + Config.FILTER_KEY_SIZE) ;
			length += i.joins.size() * ( Config.ENDPOINT_SIZE + (1-w) * Config.FILTER_DATA_SIZE + w * Config.FILTER_KEY_SIZE ) ;
		}
		return res;
	}
	
	public int length() {
		if (length < 0  ) 
			length = accountTraffic ? bytes() : 0 ;

		return length;
	}


	public void accountTransfer( Socket call, double srcUpload, double dstUpload ) {	
		CatadupaNode srcNode = call.srcNode(), dstNode = call.dstNode();
				
		srcNode.state.stats.catadupaTraffic.dbRepairReply_upload += srcUpload;
		srcNode.state.stats.catadupaTraffic.dbRepairReply_download += dstUpload ;		

		dstNode.state.stats.catadupaTraffic.dbRepairReply_upload += dstUpload;
		dstNode.state.stats.catadupaTraffic.dbRepairReply_download += srcUpload ;
	}

	public void deliverTo( Socket call, SocketReplyHandler handler) {		
		if( call.replyable )
			((CatadupaSocketcReplyHandler) handler).onReply( call, this ) ;			
		else
			((CatadupaSocketcReplyHandler) handler).onReply( this ) ;
	}

	public static boolean accountTraffic = true ;
}
