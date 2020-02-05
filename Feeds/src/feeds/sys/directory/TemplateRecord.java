package feeds.sys.directory;

import java.io.* ;

@SuppressWarnings("serial")
public class TemplateRecord implements Serializable {
    
    public TemplateRecord(String name, String className) {
        this.name = name ;
        this.className = className ;
    }
    
    @SuppressWarnings("unchecked")
	synchronized public <T> T newInstance() {
        try {
        	return (T) Class.forName( className ).newInstance() ;
        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
        return null ;
    }
    
    public String template() {
        return name ;
    }
    
    public String toString() {
        return "<" + name + ">" ;
    }
    
    String name ;
    String className ;
}
