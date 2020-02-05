package feeds.sys.transports.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import feeds.api.FeedsException;
import feeds.sys.FeedsNode;
import feeds.sys.core.Packet;
import feeds.sys.core.Transport;
import feeds.sys.packets.cPacket;
import feeds.sys.transports.BasicTransport;


public class IncomingUdpTransport extends BasicTransport implements Runnable {
    
    IncomingUdpTransport( String urlString ) {
        super( urlString, "incoming") ;
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
    
    /*
    public void close() throws TransportCloseException {
        try {
            isOpen = false ;
            socket.close() ;
            socket = null ;
        }
        catch( Exception x ) {
            throw new TransportCloseException("Transport.close() failed.") ;
        }
    }
    */
    
    public void run() {
		for (;;) {
			try {
				datagram.setLength(65536);
				socket.receive(datagram);

				Packet packet = new Packet(datagram.getData(), 0, datagram.getLength());
//				Feeds.out.println( this.url() + " Got + " + datagram.getLength() + " bytes ") ;
//				Feeds.out.println( packet.getClass() ) ;
				dispatcher.dispatch( cPacket.decode(packet));
			}
			catch( SocketTimeoutException ste ) {
			}
			catch (Exception x) {
				x.printStackTrace();
			}
		}
	}
    
    private void setupSocket() throws Exception {
    	if( socket == null ) {
	        InetAddress addr = url.address() ;
	        datagram = new DatagramPacket( new byte[65536], 65536 ) ;

	        socket = new MulticastSocket( url.port() ) ;
	        if( (isMulticast = addr.isMulticastAddress()) )
	            socket.joinGroup( addr ) ;
	        
	        //        socket.setReceiveBufferSize(1) ;
	        setUrl("udp://" + url.hostname() + ":" + socket.getLocalPort() + "/" + url.fid() ) ;

	        FeedsNode.newThread( true, this).start() ;
    	}
    }    
    
    public boolean isMulticast() {
        return isMulticast ;
    }
    
    public boolean isUnicast() {
    	return ! isMulticast ;
    }
        
    private MulticastSocket socket = null ;
    private DatagramPacket datagram = null ;
}
