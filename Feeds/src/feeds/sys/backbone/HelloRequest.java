package feeds.sys.backbone;


import java.io.* ;

import feeds.api.* ;
import feeds.sys.core.* ;

@SuppressWarnings("serial")
public class HelloRequest implements Serializable {
     
    public HelloRequest( ID src, String url) {
        this.src = src ;
        this.url = url ;
        this.timeStamp = Feeds.time() ;
    }
    
    public ID src() {
        return src ;
    }
    
    public String url() {
        return url ;
    }
    
    public String toString() {
        return "HelloRequest(" + url + ")" ;
    }
    
    ID src ;
    String url ;
    double timeStamp ;   
}
