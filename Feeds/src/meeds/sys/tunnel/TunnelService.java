package meeds.sys.tunnel;

import meeds.api.*;

public class TunnelService {

	void init() {		
		Meeds.lookup("/System/Meeds/TunnelChannel") ;
	}
	
	static public void start() {
        new TunnelService().init() ;
    }
}
