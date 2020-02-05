package feeds.simsim.sys;

import java.util.* ;

import feeds.sys.* ;
import feeds.sys.core.* ;
import feeds.sys.transports.* ;

public class SS_Transports extends Transports {
    
    public SS_Transports() {
    	soqs = new HashMap<ID, Transport>();
        incoming = new HashMap<ID, Transport>();
        outgoing = new HashMap<String, Transport>();
    }
    
    public Transport openTransport( String url, String mode ) {
    	ID src = FeedsNode.id();
        ID dst = new Url( url ).fid() ;
        
        if( url.startsWith("soq") ) {
        	Transport res = soqs.get(src) ;
        	if( res == null ) {
        		res = new SS_SoqTransport( url, mode ) ;
        		soqs.put(src, res) ;
        	}
        	return res;
        }
        
        if( mode.equals("incoming") || dst.equals( src) ) return getIncomingTransport( dst ) ;
        else if( mode.equals("outgoing") ) return getOutgoingTransport( src, dst ) ;
        else return null ;
    }
    
    static Transport getIncomingTransport( ID dst ) {
        Transport t = incoming.get( dst ) ;
        if( t == null ) {
            t = new SS_IncomingTransport( dst, "ss://-/" + dst + "/", "incoming") ;
            incoming.put( dst,  t ) ;
        }
        return t ;
    }
    
    static Transport getOutgoingTransport( ID src, ID dst ) {
        String key = src + "/" + dst ;
        Transport t = outgoing.get( key ) ;
        if( t == null ) {
            t = new SS_OutgoingTransport(src, dst, "ss://-/" + dst + "/", "outgoing") ;
            outgoing.put( key, t ) ;
        }
        return t ;
    }

    public static Transports factory() {
    	if( factory == null ) {
    		factory = new SS_Transports() ;
    	}
    	return factory ;
    }
    
    static Map<ID, Transport> soqs ;
    static Map<ID, Transport> incoming ;
    static Map<String, Transport> outgoing ;
    public static SS_Transports factory ;
}
