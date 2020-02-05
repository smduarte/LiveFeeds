package feeds.sys.backbone.channels;

import java.util.* ;

import feeds.sys.* ;
import feeds.sys.core.* ;
import feeds.sys.graphs.*; 
import feeds.sys.packets.*;
import feeds.sys.pipeline.*;
import feeds.sys.backbone.containers.*;

public class LinkStateChannel extends BasicTemplate<SpanningTreeEncoding, Void, Void, Object> {
    
	public void init() {
        try {
            super.init() ;
            
            Container.monitor( BackboneNodes.class, new ContainerListener<BackboneNodes>() {            	
                public void handleContainerUpdate( BackboneNodes bn ) {
                	transports = bn.transports() ;
                }
            }) ;
            switch( FeedsNode.type() ) {
                
                case cNODE:
                	pipeline.setTemplate( new BasicTemplate<SpanningTreeEncoding, Void, Void, Object>( channel() ) {
                        public void pRoute( pPacket<SpanningTreeEncoding, Void> p ) throws Exception {
                        }
                        
                        public void fRoute( fPacket< Void, Object> p ) throws Exception {
                        }
                    }) ;
                    break ;
                    
                    
                case sNODE:
                	pipeline.setTemplate( new BasicTemplate<SpanningTreeEncoding, Void, Void, Object>( channel() ) {
                        public void pRoute( pPacket<SpanningTreeEncoding, Void> p ) throws Exception {
                        }
                        
                        public void fRoute( fPacket< Void, Object> p ) throws Exception {
                        }
                    }) ;
                    break ;
                    
                case pNODE:
                	pipeline.setTemplate( new BasicTemplate<SpanningTreeEncoding, Void, Void, Object>( channel() ) {
                        public void pRoute( pPacket<SpanningTreeEncoding, Void> p ) throws Exception {
                            SpanningTreeEncoding ste = p.envelope() ;
                            send( transports, ste.children( thisNode ), p ) ;
                        	loq.send( p ) ;
                        }
                        
                        public void fRoute( fPacket< Void, Object> p ) throws Exception {
                        	ID target = p.dst.major() ;
                            if( target.equals( thisNode ) ) loq.send( p ) ;
                            else send( transports, target, p ) ;
                        }
                    }) ;
                    break ;
            }
        } catch( Exception x ) {
            x.printStackTrace() ;
        }
    }
     
	private final ID thisNode = FeedsNode.id() ;
    private Map<ID, Transport> transports = new HashMap<ID, Transport>() ;
}