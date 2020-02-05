package feeds.sys.backbone;

import java.io.* ;
import java.util.* ;
import feeds.sys.core.* ;

@SuppressWarnings("serial")
public class DiscoveryRequest implements Serializable {
    
    public DiscoveryRequest(String srcURL) {
        this(srcURL, "") ;
    }
    
    public DiscoveryRequest(String url, String list) {
        this.url = url ;
        this.list = list ;
    }
    
    public Collection<ID> list() {
        ArrayList<ID> res = new ArrayList<ID>() ;
        StringTokenizer st = new StringTokenizer( list, ";") ;
        while( st.hasMoreTokens() ) res.add( new ID( st.nextToken() ) ) ;
        return res ;
    }
    
    public String url() {
        return url ;
    }
    
    public void add( ID node ) {
        list = list + node + ';' ;
    }
    
    public void add( String node ) {
        list = list + node + ';' ;
    }
    
    public boolean isEmpty() {
        return list.length() == 0 ;
    }
    
    public String toString() {
        return "DiscoveryRequest for :" + list ;
    }
    
    String url ;
    String list ;
}
