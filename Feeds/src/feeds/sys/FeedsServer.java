package feeds.sys;

import feeds.sys.core.* ;
import feeds.sys.registry.*;

public class FeedsServer {
    
    public static void main(String args[]) {
        try {
        	if( args.length == 0 ) {
        		System.err.println("usage: java feeds.sys.FeedsServer registryDir") ;
        		System.exit(-1) ;
        	}

        	for(int i = 0 ; i < 100 ; i++) System.out.println() ;
            
        	new ServerNodeContext( new NodeRegistry( args[0] ) ).init();        	
        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
    }    
}
