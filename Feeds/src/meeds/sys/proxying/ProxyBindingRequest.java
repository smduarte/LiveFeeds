package meeds.sys.proxying;

import java.io.*;

@SuppressWarnings("serial")
public final class ProxyBindingRequest implements Serializable {
    
    public ProxyBindingRequest(String urls) {
        this.urls = urls ;
    }
    
    public String urls() {
        return urls ;
    }
    
    public String toString() {
        return "ProxyRequest Request[" + urls + "]" ; 
    }

    String urls ;
}