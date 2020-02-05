package meeds.sys.homing;

import java.util.* ;

import feeds.api.* ;
import feeds.sys.* ;
import feeds.sys.core.* ;
import feeds.sys.util.*;

import meeds.api.Meeds;
import meeds.sys.* ;
import meeds.sys.proxying.*;
import meeds.sys.homing.containers.*;

public class HomingService {
    
    public static float KEEP_ALIVE_PERIOD = 30.0f ;

    private String dataTransfer = "" ;
    public static Map<ID, ExpirableSet<ProxyInfo>> pmap = new HashMap<ID, ExpirableSet<ProxyInfo>>() ;
	
    public void init() {
        try {
        	
            final HomingNodes.Updater hnu = Container.byClass(HomingNodes.class);
            
            try{          

            	String acceptorUrls = FeedsRegistry.get("/Local/System/Meeds/Homebase/Acceptors") ;            	
            	
            	List<Transport> incomings = new ArrayList<Transport>() ;
            	Scanner s = new Scanner( acceptorUrls ).useDelimiter(";") ;                
            	while( s.hasNext() ) {
                    Transport t = FeedsNode.openTransport( s.next(), "incoming" ).open() ;
                    incomings.add( t ) ;
                    dataTransfer += t.url() + ";" ;
            	}
            	Feeds.out.println("Homebase ready at: " + incomings ) ;
            	
            }
            catch( Exception x ) {
                Feeds.out.println("Homebase service aborted.\nTrouble opening transports.\n[" + x.getMessage() + "]\n") ;
                x.printStackTrace() ;
                return ;
            }
            final Location loc = Container.byClass( Location.class ) ;
        	           
            // Handle HomingRequest operation performed by incoming mobile connections
            MeedsNode.hbc().subscribe( new Subscriber<Void, HomingRequest>()  {
                public void notify( final Receipt r, final Void e, final Payload<HomingRequest> p) {
                	HomingRequest request = p.data() ;
                	hnu.put( request.urls() ) ;
                	
                	ProxyInfo cp = closestProxy( request.src, request.pos ) ;
                	if( cp == null ) cp = new ProxyInfo( loc.pos(), ProxyDiscoveryService.RADIUS, dataTransfer ) ;
                	
                	MeedsNode.hbc().feedback( r, null, new HomingReply( request.timeStamp(), KEEP_ALIVE_PERIOD, dataTransfer, cp ) ) ;                	
                	MeedsNode.pdc().publish( request.pos, request.src ) ;
                }
            }) ;

            MeedsNode.pdc().subscribeFeedback( new FeedbackSubscriber<ID, ProxyInfo>(){
				public void notifyFeedback(Receipt r, ID mob, Payload<ProxyInfo> p) {
					store(mob, p.data() ) ;
				}
            }) ;
        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
    }
  
    static public void start() {
        new HomingService().init() ;
    }
  
    private ProxyInfo closestProxy( ID mob, Position pos ) {
    	ExpirableSet<ProxyInfo> inRange = pmap.get( mob ) ;
    	if( inRange == null ) return null ;

    	ProxyInfo closest = null ;
    	for( ProxyInfo i : inRange )
        	if( closest == null || closest.distanceSq(pos) > i.distanceSq(pos) ) 
        		closest = i ; 

    	return closest ;
    }
    
    private void store( ID mob, ProxyInfo pi ) {
    	ExpirableSet<ProxyInfo> s = pmap.get( mob ) ;
    	if( s == null ) {
    		pmap.put( mob, (s = new ExpirableSet<ProxyInfo>(90,20)) ) ;
    	}
    	s.add( pi ) ;
    }
}
