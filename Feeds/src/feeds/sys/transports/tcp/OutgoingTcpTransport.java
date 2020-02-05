package feeds.sys.transports.tcp;

import java.net.Socket;

import feeds.api.Feeds;
import feeds.api.FeedsException;
import feeds.sys.FeedsNode;
import feeds.sys.core.Transport;
import feeds.sys.packets.cPacket;
import feeds.sys.transports.BasicTransport;

/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 */
public class OutgoingTcpTransport extends BasicTransport implements TcpConnection.Monitor {
    
    /** Creates a new instance of OutgoingTcpConnection */
    public OutgoingTcpTransport(String urlString) {
        super( urlString, "outgoing") ;
    }
    
    public Transport open() throws FeedsException {
        try {
            if( socket == null ) {
                socket = new Socket( url.hostname(), url.port() ) ;
                connection = new TcpConnection( socket, url, FeedsNode.plm().dispatcher() )  ;
                connection.addConnectionMonitor(this) ;
            }
        }
        catch( Exception x ) {
        	socket = null ;
        	connection = null ;
            throw new FeedsException("Transport.open() failed.<" + x.getMessage() +">" + url()) ;
        }
        return this ;
    }
    
    public void close() throws FeedsException{
        try {
            if( socket != null ) {
                socket = null ;
            	connection.close() ;
            	connection = null ;
            }
        }
        catch( Exception x ) {
        	socket = null ;
        	connection = null ;
            throw new FeedsException("Transport.close() failed.") ;
        }
    }
    
    synchronized public void send( cPacket p) throws FeedsException {    	
    	try {
        	connection.send(p) ;
    	} 
    	catch( NullPointerException npe ) {
    		open() ;
    		if( connection == null ) {
            	Feeds.err.printf("Transport.open() failed.<:%s>\n", url) ;
            } else 
            	send( p ) ;
    	}
    	catch( Exception x ) {
        	socket = null ;
            throw new FeedsException("Transport.close() failed.") ;    		
    	}
    }

	public void handleBrokenConnection(TcpConnection con) {
		connection = null ;
		Feeds.err.printf("Broken connection on : %s <%s>\n", this.url(), "outgoing" ) ;
	}

    private Socket socket ;
    private TcpConnection connection ;    
}
