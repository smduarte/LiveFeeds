package feeds.sys.transports.containers.impl;

import feeds.api.*;
import feeds.sys.* ;
import feeds.sys.core.* ;
import feeds.sys.transports.containers.*;

public class DefaultIncomingTransport_Impl extends Container<DefaultIncomingTransport> implements DefaultIncomingTransport, DefaultIncomingTransport.Updater {
    
    public DefaultIncomingTransport_Impl() {
        init() ;
    }

    void init() {

    	String key = FeedsNode.isServer() ? "/Local/System/Binding/DataTransfer" : "DataURL" ;
    	String dataURL = FeedsRegistry.get(key) ;
    	if( dataURL == null ) dataURL = "udp://-:0/-" ;     
            
    	defaultTransport = FeedsNode.openTransport( dataURL, "incoming").open() ;
    	Feeds.out.println( "Default transport  " + defaultTransport.url() ) ;
    }
    
    public String url() {
        return defaultTransport.url() ;
    }
    
    public Transport transport() {
        return defaultTransport ;
    }
    
    private Transport defaultTransport ;
}
