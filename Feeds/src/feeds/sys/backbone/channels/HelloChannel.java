package feeds.sys.backbone.channels;

import java.util.* ;

import feeds.sys.* ;
import feeds.sys.core.* ;
import feeds.sys.packets.*;
import feeds.sys.pipeline.*;
import feeds.sys.backbone.*;
import feeds.sys.backbone.containers.*;

public class HelloChannel extends BasicTemplate<ID, HelloRequest, Long, HelloReport> {
    
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
                	pipeline.setTemplate( new BasicTemplate<ID, HelloRequest, Long, HelloReport>( channel() ) {
                        public void pRoute( pPacket< ID, HelloRequest> p ) throws Exception {
                        }
                        
                        public void fRoute( fPacket< Long, HelloReport> p ) throws Exception {
                        }
                    }) ;
                    break ;
                    
                    
                case sNODE:
                	pipeline.setTemplate( new BasicTemplate<ID, HelloRequest, Long, HelloReport>( channel() ) {
                        public void pRoute( pPacket< ID, HelloRequest> p ) throws Exception {
                        }
                        
                        public void fRoute( fPacket< Long, HelloReport> p ) throws Exception {
                        }
                    }) ;
                    break ;
                    
                case pNODE:
                	pipeline.setTemplate( new BasicTemplate<ID, HelloRequest, Long, HelloReport>( channel() ) {
                		public void pRoute( pPacket<ID, HelloRequest> p ) throws Exception {
                            if( p.isLocal() ) {
                                send( transports, p.envelope(), p ) ;
                            }
                            else {
                            	p.setTarget( thisNode ) ;
                                loq.send( p ) ;
                            }
                        }
                        
                		public void fRoute( fPacket< Long, HelloReport> p ) throws Exception {  
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

