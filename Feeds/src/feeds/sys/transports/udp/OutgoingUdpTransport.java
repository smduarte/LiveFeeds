package feeds.sys.transports.udp;

import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.PortUnreachableException;

import feeds.api.FeedsException;
import feeds.sys.core.Transport;
import feeds.sys.packets.cPacket;
import feeds.sys.transports.BasicTransport;

public class OutgoingUdpTransport extends BasicTransport {
    
    OutgoingUdpTransport(String urlString) {
        super( urlString, "outgoing") ;
    }
    
    public boolean isUnicast() {
        return ! isMulticast ;
    }
    
    public boolean isMulticast() {
        return isMulticast ;
    }
    
    public Transport open() throws FeedsException {
        try {
                super.open() ;
                this.setupSocket() ;
            }
        catch( Exception x ) {
            throw new FeedsException("Transport.open() failed.[" + x.getMessage() + "]") ;
        }
        return this ;
    }
    
    synchronized public void send( cPacket p) throws FeedsException {
        try {
        		socket.send( p.packet().toDatagramPacket( datagram ) ) ;
 
//                Feeds.out.println( "udp.sent() " + p + "--->" + datagram.getLength() ) ;
//                Feeds.out.println( this.url() + "udp.sent() " + datagram.getLength() + " bytes to: " + datagram.getAddress() + ":" + datagram.getPort() ) ;
        }
        catch( NullPointerException npe ) {
        	open() ;
        	send( p ) ;
        }
        catch( PortUnreachableException x ) {
        }
        catch( Exception x ) {
        	x.printStackTrace() ;
            throw new FeedsException( x.toString() ) ;
        }
    }
    
    private void setupSocket() throws Exception {
    	if( socket == null ) { 
	        socket = new MulticastSocket() ;
	        socket.setSendBufferSize(65535) ;
	        socket.connect( url.address(), url.port() ) ;        
	        isMulticast = url.address().isMulticastAddress() ;
	        datagram = new DatagramPacket( new byte[0], 0, url.address(), url.port() ) ;
    	}
    }
        
    private MulticastSocket socket = null ;
    private DatagramPacket datagram = null ;
}
