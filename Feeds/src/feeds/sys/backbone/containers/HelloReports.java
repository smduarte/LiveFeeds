package feeds.sys.backbone.containers;

import java.util.* ;
import feeds.sys.core.*;
import feeds.sys.backbone.*;

public interface HelloReports {


	public Collection<ID> nodes() ;

	public boolean isNewServer( ID s) ;
	
	public Map<ID, HelloReport> reports() ;
	
	interface Updater {
	
		public void remove( ID node) ;

		public void add(HelloReport hr) ;		
	}
}
