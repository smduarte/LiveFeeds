package feeds.sys.registry;

import java.io.* ;

import feeds.api.* ;
import feeds.sys.* ;
import feeds.sys.core.* ;

@SuppressWarnings("serial")
final public class RegistryItem implements Serializable {
    
    public RegistryItem( String key, Object value ) {
    	this( key, value, 0, -1, FeedsNode.id() ) ;
    }
    
    public RegistryItem( String key, Object value, int scope, double expiration, ID creator ) {
        this.key = key ;
        this.value = value ;
        this.scope = scope ;
        this.creator = creator ;
        this.creation = Feeds.time() ;
        this.expiration = expiration < 0 ? Double.MAX_VALUE : creation + expiration * 60 ; 
    }
    
    public String toString() {
        return "{" + key.toString() + "->" + value + " [" + expiration + ", " + scope + ", " + creator + "]" + "}";
    }
    
    public String key() {
        return key ;
    }
    
    @SuppressWarnings("unchecked")
	public <T> T value() {
        return (T) value ;
    }
    
    public ID creator() {
        return creator ;
    }
    
    public boolean isExpired() {
        return expiration < Feeds.time() ;
    }

    public void takeOwnership() {
        this.creator = FeedsNode.id() ;
    }
    
    public boolean same( RegistryItem ri ) {
        return key.equals( ri.key ) ;
    }
    
    public boolean newer( RegistryItem ri ) {
        return creation > ri.creation ;
    }
    
    public void setValue( Object newValue ) {
        this.value = newValue ;
    }
    
    int scope ;
    int length ;
    String key ;
    ID creator ;
    Object value ;
    double creation ;
    double expiration ;
}
