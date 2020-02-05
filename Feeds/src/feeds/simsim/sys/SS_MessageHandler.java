package feeds.simsim.sys;

import simsim.core.* ;

public interface SS_MessageHandler {

	public void onReceive( EndPoint src, SS_cPacket p) ;	

	public void onReceive( TcpChannel src, SS_cPacket p) ;	

}
