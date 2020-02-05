package feeds.sys.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import feeds.api.Feeds;
import feeds.api.Payload;
import feeds.api.Receipt;
import feeds.api.Subscriber;
import feeds.sys.FeedsNode;
import feeds.sys.FeedsRegistry;
import feeds.sys.binding.containers.ClientNodes;
import feeds.sys.core.Container;
import feeds.sys.core.Transport;

public class BindingService {
    
    String dataTransfer = "" ;
    public static float KEEP_ALIVE_PERIOD = 130.0f ;
    
    public void init() {
        try {
            try{            	
            	String acceptorUrls = FeedsRegistry.get("/Local/System/Binding/Acceptors") ;            	
            	Feeds.out.println("Accepting clients at: " + acceptorUrls ) ;
            	
                List<Transport> acceptors = new ArrayList<Transport>() ;
                List<Transport> incomings = new ArrayList<Transport>() ;

            	Scanner s = new Scanner( acceptorUrls ).useDelimiter(";") ;
            	while( s.hasNext() )
            		acceptors.add( FeedsNode.openTransport( s.next(), "incoming" ).open() ) ;
            	
         
            	String dataTransferUrls = FeedsRegistry.get("/Local/System/Binding/DataTransfer") ;            	
            	s = new Scanner( dataTransferUrls).useDelimiter(";") ;
            	while( s.hasNext() ) {
                    Transport t = FeedsNode.openTransport( s.next(), "incoming" ).open() ;
                    incomings.add( t ) ;
                    dataTransfer += t.url() + ";" ;
            	}
            	
            }
            catch( Exception x ) {
                Feeds.out.println("Binding service aborted.\nTrouble opening transports.\n[" + x.getMessage() + "]\n") ;
                x.printStackTrace() ;
                return ;
            }
            
            final ClientNodes.Updater cnu = Container.byClass(ClientNodes.class);

            // Handle BindingRequest operation performed by incoming client connections
            FeedsNode.bc().subscribe( new Subscriber<Void, BindingRequest>()  {
                public void notify( final Receipt r, final Void e, final Payload<BindingRequest> p) {
                	BindingRequest request = p.data() ;
                	cnu.put( request.urls ) ;
                	FeedsNode.bc().feedback( r, null, new BindingReply( request.timeStamp(), KEEP_ALIVE_PERIOD, dataTransfer ) ) ;
                }
            }) ;

        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
    }
  
    static public void start() {
        new BindingService().init() ;
    }
    
}
