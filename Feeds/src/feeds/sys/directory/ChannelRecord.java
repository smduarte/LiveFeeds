package feeds.sys.directory;

import java.io.* ;
import feeds.sys.core.* ;

@SuppressWarnings("serial")
public class ChannelRecord implements Serializable {
    
    public ChannelRecord(String template, String name) {
    	this( template, name, new ID() ) ;
    	isMonitorable = isTunnelable = true ;
    }
    
    public ChannelRecord(String template, String name, ID id) {
        this.id = id ;
        this.name = name ;
        this.template = template ;
        this.isMonitorable = this.isTunnelable = false ;
    }
    
    public String name() {
        return name ;
    }
    
    public ID channel() {
        return id ;
    }
    
    public String template() {
        return template ;
    }

    public String toString() {
    	return String.format("CR: <%s | %s | %s>", name, id, template) ;
    }
    
    public boolean monitorSubscriptions() {
    	return isMonitorable ;
    }
    
    public ChannelRecord monitorable( boolean flag ) {
    	isMonitorable = flag ;
    	return this ;
    }
    
    public boolean tunneling() {
    	return isTunnelable ;
    } 

    public ChannelRecord tunnelable( boolean flag ) {
    	isTunnelable = flag ;
    	return this ;
    }
    
    ID id ;
    String name ;
    String template ;
    boolean isTunnelable ;
    boolean isMonitorable ;
}
