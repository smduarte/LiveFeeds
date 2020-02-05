package feeds.simsim.sys;

import java.io.*;


import simsim.core.* ;
import simsim.gui.canvas.RGB;

import feeds.sys.core.* ;
import feeds.sys.packets.*;

public class SS_cPacket extends Message {
    
	Packet packet ;
    
    public SS_cPacket( cPacket p ) throws IOException {
    	this( RGB.RED, p ) ;
    }    
    
    public SS_cPacket( RGB c, cPacket p ) throws IOException {
    	super( true, c ) ;
		packet = p.packet() ;
    }    
    
    /* (non-Javadoc)
	 * <b>IMPORTANT!!!</b> Include a copy of this method in every class that extends Message
	 * @see sim.core.Message#deliverTo(sim.net.EndPoint, sim.core.MessageHandler)
	 */
	public void deliverTo( EndPoint src, MessageHandler handler ) {
		((SS_MessageHandler)handler).onReceive( src, this ) ;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
