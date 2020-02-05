package feeds.sys.registry;

import java.util.* ;

@SuppressWarnings("serial")
class VHashtable extends Hashtable<String, RegistryItem> implements Iterable<RegistryItem>{
    
    VHashtable() {
        super() ;
    }
    
    public void put( RegistryItem ri ) {
        super.put( ri.key(), ri ) ;
    }
    
    public Iterator<RegistryItem> iterator() {
    	return values().iterator() ;
    }
    
    Iterator<RegistryItem> values( final String preffix ) {
   	
        final Iterator<Map.Entry<String, RegistryItem>> i = super.entrySet().iterator() ;
        
        return new Iterator<RegistryItem>() {
        	Map.Entry<String, RegistryItem> current = null ;
        	
            public boolean hasNext() {
            	for( current = null ; i.hasNext() ; current = null ) {
            		current = i.next() ;
            		if( current.getKey().startsWith( preffix ) ) break ;
            	}
            	return current != null ;
            }
            
            public void remove() {
                i.remove() ;
            }
            
            public RegistryItem next() {
                return current.getValue() ;
            }
        } ;
    }
}
