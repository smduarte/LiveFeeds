package meeds.simsim.sys;

import meeds.sys.transports.hoq.HomebaseOutputQueue;

public class SS_HoqTransport  extends HomebaseOutputQueue {
	public SS_HoqTransport(String url, String mode) {
        super( url,  mode ) ;
	}   
}
