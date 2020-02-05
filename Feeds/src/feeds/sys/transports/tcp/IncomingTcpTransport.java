package feeds.sys.transports.tcp;

import java.net.ServerSocket;

import feeds.api.FeedsException;
import feeds.sys.core.Transport;
import feeds.sys.transports.BasicTransport;
/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 */
public class IncomingTcpTransport extends BasicTransport implements Runnable, TcpConnection.Monitor {
    
    public IncomingTcpTransport( String urlString) {
        super( urlString, "incoming" ) ;
    }
    
    synchronized public Transport open() throws FeedsException {
        try {
            if( socket == null ) {
                socket = new ServerSocket( url.port() ) ;
                setUrl( url.protocol() + "://" + url.hostname() + ":" + socket.getLocalPort() + "/" + url.fid() ) ;
                new Thread( this ).start() ;
            }
        }
        catch( Exception x ) {
        	socket = null ;
            x.printStackTrace() ;
            throw new FeedsException( x.getMessage() ) ;
        }
        return this ;
    }
    
    public void close() throws FeedsException{
        try {
            if( socket != null ) {
                socket.close() ;
                socket = null ;
            }
        }
        catch( Exception x ) {
        	socket = null ;
            x.printStackTrace() ;
            throw new FeedsException( x.getMessage() ) ;
        }
    }
        
    public void run() {
        while( socket != null ) {
            try {
            	TcpConnection con = new TcpConnection( socket.accept(), url, dispatcher) ;
            	con.addConnectionMonitor(this) ;
            }
            catch( Exception x ) {
            	x.printStackTrace() ;
            	socket = null ;
            }
        } ;
    }
    
    private ServerSocket socket ;

	public void handleBrokenConnection(TcpConnection con) {
		
	}
}