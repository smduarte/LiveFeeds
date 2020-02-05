package livefeeds.rtrees.msgs;

import simsim.core.EndPoint;
import simsim.core.MessageHandler;

public interface CatadupaMessageHandler extends MessageHandler {
	
	public void onReceive( EndPoint src, DepartureNotice m) ;
		
}
