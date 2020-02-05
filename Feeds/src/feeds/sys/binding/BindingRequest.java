package feeds.sys.binding;

import java.io.* ;
import feeds.sys.* ;

@SuppressWarnings("serial")
public final class BindingRequest implements Serializable {
    
    public BindingRequest(String urls) {
        this.urls = urls ;
    }
    
    public String urls() {
        return urls ;
    }

    public double timeStamp() {
        return timeStamp ;
    }
    
    public String toString() {
        return "Binding Request[" + urls + "]" ; 
    }

    String urls ;
    double timeStamp = FeedsNode.time() ;
}