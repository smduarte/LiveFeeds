package feeds.sys.core;

import feeds.sys.packets.*;

public interface Dispatcher {
	
    public void dispatch( cPacket p ) ;
    
}
