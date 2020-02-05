package meeds.sys.proxying;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import meeds.sys.MeedsNode;
import meeds.sys.proxying.containers.ProxyClients;
import feeds.api.Feeds;
import feeds.api.Payload;
import feeds.api.Receipt;
import feeds.api.Subscriber;
import feeds.sys.FeedsNode;
import feeds.sys.FeedsRegistry;
import feeds.sys.core.Container;
import feeds.sys.core.Transport;

public class ProxyBindingService {
    
    String dataTransfer = "" ;
    public static float KEEP_ALIVE_PERIOD = 30.0f ;
    
    public void init() {
        try {
            try{            	
            	String acceptorUrls = FeedsRegistry.get("/Local/System/Meeds/Proxying/Acceptors") ;            	
            	
            	List<Transport> incomings = new ArrayList<Transport>() ;
            	Scanner s = new Scanner( acceptorUrls ).useDelimiter(";") ;                
            	while( s.hasNext() ) {
                    Transport t = FeedsNode.openTransport( s.next(), "incoming" ).open() ;
                    incomings.add( t ) ;
                    dataTransfer += t.url() + ";" ;
            	}
            	Feeds.out.println("Proxy ready at: " + incomings ) ;
            	
            }
            catch( Exception x ) {
                Feeds.out.println("Proxy service aborted.\nTrouble opening transports.\n[" + x.getMessage() + "]\n") ;
                x.printStackTrace() ;
                return ;
            }
            
            final ProxyClients.Updater pcu = Container.byClass(ProxyClients.class);

            // Handle ProxyBindingRequest operation performed by mobile clients
            MeedsNode.pxc().subscribe( new Subscriber<Void, ProxyBindingRequest>()  {
                public void notify( final Receipt r, final Void e, final Payload<ProxyBindingRequest> p) {
                	ProxyBindingRequest request = p.data();
                	pcu.put( request.urls() ) ;
                	//Feeds.err.printf("ProxyService: %s %s\n", r.source(), request ) ;
                	MeedsNode.pxc().feedback( r, null, new ProxyBindingReply( KEEP_ALIVE_PERIOD, dataTransfer ) ) ;
                }
            }) ;

        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
    }
  
    static public void start() {
        new ProxyBindingService().init() ;
    }
    
}
