package feeds.simsim.sys;

import java.io.*;
import java.util.*;

import simsim.gui.canvas.RGB;

import feeds.api.* ;
import feeds.sys.core.*;
import feeds.sys.packets.*;
import feeds.sys.transports.* ;

class SS_OutgoingTransport extends BasicTransport {    
	private SS_Node src, dst ;
    
    SS_OutgoingTransport( ID src, ID dst, String url, String mode) {
        super( url,  mode ) ;
        this.src = SS_Node.db.get( src ) ; 
        this.dst = SS_Node.db.get( dst ) ;
    }
    
    public void send( cPacket p ) throws FeedsException {
    	try {
    		
//			System.out.printf("Packet: %s -> %s [%s - %s]\n", src.id, dst.id, p.channel, p.peek(0)) ;

			src.endpoint.udpSend( dst.endpoint, new SS_cPacket( color(p.channel), p) ) ;
			
		} catch (IOException x) {
			x.printStackTrace() ;
			throw new FeedsException( x.getMessage() ) ;
		}
    }    
    
    private static RGB color( ID channel ) {
    	RGB res = packetColors.get( channel ) ;
    	return res != null ? res : RGB.LIGHT_GRAY ;
    }
    
    static Map<ID, RGB> packetColors = new HashMap<ID, RGB>() ;
    static {
    	packetColors.put( new ID(1L), RGB.GRAY ) ;
    	packetColors.put( new ID(2L), RGB.BLACK ) ;
    	packetColors.put( new ID(3L), RGB.BLUE ) ;
    	packetColors.put( new ID(4L), RGB.GREEN ) ;
    	packetColors.put( new ID(5L), RGB.MAGENTA ) ;
    	packetColors.put( new ID(6L), RGB.CYAN ) ;
    	packetColors.put( new ID(7L), RGB.YELLOW ) ;
    	packetColors.put( new ID(8L), RGB.PINK ) ;
    	
    	packetColors.put( new ID(110L), RGB.RED ) ;
    	packetColors.put( new ID(111L), RGB.BLUE ) ;
    	packetColors.put( new ID(112L), RGB.GREEN ) ;

    	packetColors.put( new ID(1000L), RGB.PINK ) ;

    }
}
