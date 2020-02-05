package feeds.setup;

import feeds.sys.FeedsRegistry;
import feeds.sys.core.ID;
import feeds.sys.core.NodeContext;
import feeds.sys.registry.NodeRegistry;
import feeds.sys.registry.RegistryItem;


public class Setup {
    
    //---------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------------//
    static void genServerRegistry( String root, int i, String S0 ) throws Exception {
    	    	
    	root = root + "/S" + i ;
    	System.out.println( root ) ;

        ID fid = new ID( "s" + i + "") ;
        System.out.println( fid ) ;

    	NodeContext.context = new SetupNodeContext( fid ) ;

    	NodeRegistry reg = new NodeRegistry() ;
    	
    	
        reg.create( root ) ;
        
        for( RegistryItem ri : reg.values("/", FeedsRegistry.HARDSTATE))
        	System.out.println( ri ) ;
        
    
        reg.clear() ;
        
        
        boolean master = i < 7 ;
        
       
        String port = "" + (8000 + i) ;
        int bindingPort = 39999 ;
        reg.put("/Local/System/Identity/ID", fid, FeedsRegistry.HARDSTATE ) ;
        reg.put("/Local/System/Identity/Name", "S" + i, FeedsRegistry.HARDSTATE ) ;
        reg.put("/Local/System/Identity/Master", "" + master, FeedsRegistry.HARDSTATE ) ;
        
        reg.put("/Local/System/Binding/DataTransfer", "tcp://-:0/-", FeedsRegistry.HARDSTATE) ;
        reg.put("/Local/System/Binding/Acceptors", "udp://-:" + bindingPort + "; " + "tcp://-:" + bindingPort, FeedsRegistry.HARDSTATE) ;
        
        reg.put("/Local/System/Backbone/NodesDB", S0 + ";", FeedsRegistry.HARDSTATE) ;
        reg.put("/Local/System/Backbone/DataTransfer", "udp://-:" + bindingPort + "/" + fid , FeedsRegistry.HARDSTATE) ;
        
        //DeedsRegistry.put("/Local/Directory/Config/<>/IncomingTransport", "rtp://230.0.0.1:9000", FeedsRegistry.HARDSTATE) ;
        //DeedsRegistry.put("/Local/Directory/Config/<>/OutgoingTransport", "rtp://230.0.0.1:9000", FeedsRegistry.HARDSTATE) ;
        //DeedsRegistry.put("/Local/Directory/Config/<>/OutgoingTransport", "udp://230.0.0.1:9000", FeedsRegistry.HARDSTATE) ;
        
        if( i == 0 ) {
            
            /*
            DeedsRegistry.put("/Global/Directory/Templates/ReliableMulticast", new ChannelTemplateRecord("ReliableMulticast", "deeds.sys.templates.reliable.b.ReliableMulticast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/UnreliableMulticast", new ChannelTemplateRecord("UnreliableMulticast", "deeds.sys.templates.unreliable.e.UnreliableMulticast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/UnreliableBroadcast", new ChannelTemplateRecord("UnreliableBroadcast", "deeds.sys.templates.unreliable.a.UnreliableBroadcast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/TotalOrderReliableMulticast", new ChannelTemplateRecord("TotalOrderReliableMulticast", "deeds.sys.templates.reliable.c.TotalOrderReliableMulticast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/PersistentReliableMulticast", new ChannelTemplateRecord("PersistentReliableMulticast", "deeds.sys.templates.reliable.d.PersistentReliableMulticast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/SingleSourceReliableMulticast", new ChannelTemplateRecord("SingleSourceReliableMulticast", "deeds.sys.templates.reliable.a.SingleSourceReliableMulticast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/SingleSourceUnreliableMulticast", new ChannelTemplateRecord("SingleSourceUnreliableMulticast", "deeds.sys.templates.unreliable.b.SingleSourceUnreliableMulticast"), -1, -1, nid ) ;
            
            DeedsRegistry.put("/Global/Directory/Templates/ReliableMulticast", new ChannelTemplateRecord("ReliableMulticast", "deeds.sys.templates.unreliable.b.SingleSourceUnreliableMulticast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/UnreliableMulticast", new ChannelTemplateRecord("UnreliableMulticast", "deeds.sys.templates.unreliable.b.SingleSourceUnreliableMulticast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/UnreliableBroadcast", new ChannelTemplateRecord("UnreliableBroadcast", "deeds.sys.templates.unreliable.b.SingleSourceUnreliableMulticast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/TotalOrderReliableMulticast", new ChannelTemplateRecord("TotalOrderReliableMulticast", "deeds.sys.templates.unreliable.b.SingleSourceUnreliableMulticast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/PersistentReliableMulticast", new ChannelTemplateRecord("PersistentReliableMulticast", "deeds.sys.templates.unreliable.b.SingleSourceUnreliableMulticast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/SingleSourceReliableMulticast", new ChannelTemplateRecord("SingleSourceReliableMulticast", "deeds.sys.templates.unreliable.b.SingleSourceUnreliableMulticast"), -1, -1, nid ) ;
            DeedsRegistry.put("/Global/Directory/Templates/SingleSourceUnreliableMulticast", new ChannelTemplateRecord("SingleSourceUnreliableMulticast", "deeds.sys.templates.unreliable.b.SingleSourceUnreliableMulticast"), -1, -1, nid ) ;
        */
  
        }
        System.out.println("Name = " + "S" + i + "/" + fid ) ;
    }
    
    static void generateRegistries( String root, int N ) {
        try {
            genServerRegistry( root, N, "" ) ;
            /*
            String S0url = (String) FeedsRegistry.get("/Local/System/Backbone/DataTransfer") ;
            
            S0url = new feeds.sys.transport.TransportFactory().init().getTransport(S0url,"incoming").open().url() ;
            
            for( int i = 1 ; i < N ; i++ ) {
                genServerRegistry( root, i, S0url ) ;
                DeedsRegistry.close() ;
            }
            */
            
        } catch( Exception e ) {
            e.printStackTrace() ;
        }
    }
    
    static public void main( String args[] ) {
        
        int N = 0;
        generateRegistries( args[0], N ) ;
    }
}

class SetupNodeContext extends NodeContext {

	SetupNodeContext( ID id ) {
		super(id) ;
	}
	
	public void init() {
	}
}