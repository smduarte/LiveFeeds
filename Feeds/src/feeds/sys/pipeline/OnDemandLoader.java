package feeds.sys.pipeline;

import feeds.api.Feeds;
import feeds.sys.FeedsNode;
import feeds.sys.core.ID;
import feeds.sys.packets.cPacket;
import feeds.sys.packets.fPacket;
import feeds.sys.packets.pPacket;
import feeds.sys.tasks.Task;

public class OnDemandLoader<E, P, F, Q> extends BasicTemplate<E, P, F, Q> {
    
    
    public OnDemandLoader( ID channel ) {
    	super( channel ) ;
    }
    
    void requestLoading() {
    	Thread.dumpStack() ;
    	if( FeedsNode.time() - timeStamp > 60.0 ) {
    		timeStamp = FeedsNode.time() ;    		
            new Task( 1.0 ) {
                public void run() {
                    try {
                    	Feeds.out.println("TODO: request template loading...") ;
                    	FeedsNode.plm().pipeline(channel) ;
                    }
                    catch( Exception x ) {
                        Feeds.out.println( FeedsNode.id() + " - Unable to load template for:" + channel ) ;
                        x.printStackTrace() ;
                    }
                }
            };
    	}
    }
    
	synchronized public void cRoute(cPacket p) throws Exception {
		requestLoading() ;
	}

	synchronized public void pRoute(pPacket<E, P> p) throws Exception {
		requestLoading() ;
	}

	synchronized public void fRoute(fPacket<F, Q> p) throws Exception {
		requestLoading() ;
	}

	double timeStamp = 0 ;
}
