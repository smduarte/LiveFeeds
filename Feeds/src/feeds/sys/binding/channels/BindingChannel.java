package feeds.sys.binding.channels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import feeds.api.Feeds;
import feeds.sys.FeedsNode;
import feeds.sys.binding.BindingReply;
import feeds.sys.binding.BindingRequest;
import feeds.sys.binding.containers.BindingTargets;
import feeds.sys.binding.containers.ClientNodes;
import feeds.sys.core.Container;
import feeds.sys.core.ContainerListener;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;
import feeds.sys.packets.fPacket;
import feeds.sys.packets.pPacket;
import feeds.sys.pipeline.BasicTemplate;

public class BindingChannel extends BasicTemplate<Void, BindingRequest, Void, BindingReply> {
   
    public void init() {
        try {
            super.init() ;
            
            if( ! FeedsNode.isPnode() ) {
	            Container.monitor( BindingTargets.class, new ContainerListener<BindingTargets>() {            	
	                public void handleContainerUpdate( BindingTargets bt ) {
	                	targets = bt.servers() ;
	                }
	            }) ;
            }
            
            if( FeedsNode.isServer() ) {
	            Container.monitor( ClientNodes.class, new ContainerListener<ClientNodes>() {            	
	                public void handleContainerUpdate( ClientNodes cn ) {
	                	clients = cn.transports() ;
	                }
	            }) ;
            }
            switch( FeedsNode.type() ) {
                
                case cNODE:
                	pipeline.setTemplate( new BasicTemplate<Void, BindingRequest, Void, BindingReply>( channel() ) {
                        
                		public void pRoute( pPacket< Void, BindingRequest> p ) throws Exception {
                            p.ttl(1) ;
                            if( p.isLocal )
                            	try {
                            		super.send( targets, p ) ;
                            	} catch( Exception x ) {
                            		Feeds.err.printf("Transport error while binding...[%s]\n", x.getMessage() ) ;
                            	}
                        }
                        public void fRoute( fPacket< Void, BindingReply> p ) throws Exception {
                        	if( ! p.isLocal )
                        		loq.send( p ) ;
                        }
                    }) ;
                    break ;
                    
                    
                case sNODE:
                	pipeline.setTemplate( new BasicTemplate<Void, BindingRequest, Void, BindingReply>( channel() ) {
                        
                		public void pRoute( pPacket< Void, BindingRequest> p ) throws Exception {
                            p.ttl(1) ;
                            if( p.isLocal )
                                super.send( targets, p ) ;
                            else 
                            	loq.send( p ) ;
                        }
                        
                        public void fRoute( fPacket< Void, BindingReply> p ) throws Exception {
                        	if( p.isLocal )
                        		super.send( clients, p.dst.major(), p) ;
                        	else 
                        		loq.send( p ) ;
                        }
                        
                    }) ;
                    break ;
                    
                case pNODE:
                	pipeline.setTemplate( new BasicTemplate<Void, BindingRequest, Void, BindingReply>( channel() ) {
                		
                		public void pRoute( pPacket< Void, BindingRequest> p ) throws Exception {
                            loq.send( p ) ;
                        }
                        
                		public void fRoute( fPacket< Void, BindingReply> p ) throws Exception {     
                			if( p.isLocal )
                        		super.send( clients, p.dst.major(), p) ;
                        	else 
                        		loq.send( p ) ;
                        }
                		
                    }) ;
                    break ;
            }
        } catch( Exception x ) {
            x.printStackTrace() ;
        }
    }
     
    ID thisNode = FeedsNode.id();
    private Map<ID, Transport> clients = new HashMap<ID, Transport>() ;
    private Collection<Transport> targets = new ArrayList<Transport>() ;
}

