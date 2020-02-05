package feeds.sys.core;

import feeds.api.* ;

public interface Template<E, P, F, Q> extends Router<E, P, F, Q> {

	public void setChannel( ID channel ) ;
	
    public Object getStub( String name, Object ... extraArgs ) throws FeedsException ;
    
}
