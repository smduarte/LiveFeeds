package feeds.sys.backbone;

import java.io.* ;

@SuppressWarnings("serial")
public final class DiscoveryReport implements Serializable {
    
    public DiscoveryReport() {
        this("") ;
    }
    
    public DiscoveryReport(String list) {
        this.list = list ;
    }
      
    public String list() {
        return list ;
    }
        
    public void add( String e ) {
        list += e + ';' ;
    }
    
    public boolean isEmpty() {
        return list.length() == 0 ;
    }
    
    public String toString() {
        return "DiscoveryReport with " + list ;
    }
    
    String list ;
}