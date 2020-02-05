package feeds.sys.directory;

import feeds.api.* ;
import feeds.sys.* ;

public class ChannelDirectory implements Directory {

	@SuppressWarnings("unchecked")
	public <T> T lookup(String name, Object... extraArgs) throws FeedsException {
		ChannelRecord r = DirectoryStorage.lookupChannelRecord( name, 30) ;
		return (T)FeedsNode.plm().getStub( r, extraArgs) ;
	}

	public synchronized <T> T clone(String template, String name, Object ... extraArgs) throws FeedsException {
        try {
            DirectoryStorage.createChannelRecord( template, name, Integer.MAX_VALUE ) ;
            return lookup( name, extraArgs ) ;
        }
        catch( Exception x ) {
        	x.printStackTrace() ;
            throw new FeedsException("ChannelDirectory.clone() failed [" + x.getMessage() + "]") ;
        }
    }


}
