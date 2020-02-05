package feeds.sys.transports.tcp.nat;

import feeds.api.Feeds;
import feeds.api.FeedsException;
import feeds.sys.FeedsNode;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;
import feeds.sys.packets.cPacket;
import feeds.sys.transports.BasicTransport;
import feeds.sys.transports.tcp.TcpConnection;
/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 */
public class OutgoingNatTransport extends BasicTransport implements TcpConnection.Monitor {
    
    /** Creates a new instance of OutgoingTcpConnection */
    public OutgoingNatTransport(String urlString) {
        super( urlString, "outgoing") ;
    }
    
    public Transport open() throws FeedsException {
    	if( connection == null ) {
    		ID dst =  url.fid() ;    		
    		if( dst.equals( thisNode ) )
    			return FeedsNode.openTransport("liq://-/-", "outgoing") ;
    		
        	connection = TcpConnection.byId( dst ) ;
        	if( connection != null )
                connection.addConnectionMonitor(this) ;
        	else 
        		Feeds.sleep(1) ;
    	}
        return this ;
    }
    
    public void close() throws FeedsException{
        try {
            if( connection != null ) {
            	connection.close() ;
                connection = null ;
            }
        }
        catch( Exception x ) {
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
            	Feeds.err.println("Transport.open() failed.<Outgoing Tcp NAT connection not found...>") ;
            } else 
            	send( p ) ;
    	}
    	catch( Exception x ) {
        	connection = null ;
            throw new FeedsException("Transport.close() failed.") ;    		
    	}
    }

    public void handleBrokenConnection(TcpConnection con) {
		connection = null ;
		Feeds.err.printf("Broken connection on : %s <%s>\n", this.url(), "outgoing" ) ;
	}
    
    private TcpConnection connection ;
    private ID thisNode = FeedsNode.id() ;
}
