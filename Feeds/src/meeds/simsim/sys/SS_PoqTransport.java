package meeds.simsim.sys;

import meeds.sys.transports.poq.PoqOutputQueue;

public class SS_PoqTransport extends PoqOutputQueue {
	
	public SS_PoqTransport(String url, String mode) {
        super( url,  mode ) ;
	}
}