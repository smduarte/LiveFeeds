package feeds.sys.registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

@SuppressWarnings("serial")
final class PHashtable extends VHashtable {
    
    private String root ;
    private XStream xstream = new XStream() ;
    
    public PHashtable() {
        super() ;
    }
    
 
    
    public PHashtable(String filename) {
        super() ;

        this.root = filename.endsWith("/") ? filename : filename + "/";
        
        File rootFile = new File( root ) ;    
        rootFile.mkdirs();
        load( rootFile ) ;
        
    }
    
    private File key2file( String key ) {
    	return new File( root + key + ".reg") ;
    }
    
    private String file2key( File f ) {
		String key = f.getPath() ;
		int i = key.lastIndexOf(".reg") ;
		if( i < 0 ) return null ;
		else return key.substring( root.length() - 1, i ) ;
    }
    
    public void put( RegistryItem ri ) {   	
        super.put( ri ) ;
        try {
			File f = key2file( ri.key ) ;
			if( ! f.exists() ) {
				f.getParentFile().mkdirs() ;
			}
			FileOutputStream fos = new FileOutputStream( f ) ;
			xstream.toXML( ri, fos ) ;
			fos.close() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		}     
    }
    
    public void remove( String key ) {
    	super.remove( key ) ;   	
    	removeFile( new File( root + key ) ) ;
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
                removeFile( key2file( current.getKey() ) ) ;
            }
            
            public RegistryItem next() {
                return current.getValue() ;
            }
        } ;
    }
    
    public void clear() {
        for( Iterator<Map.Entry<String, RegistryItem>> i = super.entrySet().iterator() ; i.hasNext() ; ) 
        try {
        	removeFile(  key2file( i.next().getKey() ) ) ;
        	i.remove();
        } catch( Exception x ) {};     
    }
    
    private void removeFile( File f ) {
    	try { new FileOutputStream( f ).close() ; } catch (Exception e) {}
    	//f.delete() ; //For now, items are only truncated to zero-length...
    	// afterwards, some proper cleanup code will be added...
    }
    
    private void load( File path ) {
    	for( File f : path.listFiles() ) {
    		if( f.isDirectory() ) load( f ) ;
    		else {
    			try {
					String key = file2key( f ) ;
					if( key == null ) continue ;					
					
					FileInputStream fis = new FileInputStream( f ) ;
					if( fis.available() > 0 ) {
						RegistryItem ri = (RegistryItem) (RegistryItem) xstream.fromXML( fis )  ;
						this.put( ri.key, ri ) ;
					}
					fis.close() ;
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
}
