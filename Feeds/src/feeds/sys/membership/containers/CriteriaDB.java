package feeds.sys.membership.containers;

import java.util.*;

import feeds.api.*;
import feeds.sys.core.*;

public interface CriteriaDB {

	public Map<ID, Set<Criteria<?>>> pData();
	public Map<ID, Set<Criteria<?>>> fData();
	
	public boolean isEmpty() ;
	
	interface Updater {
		
		public void pUpdate( ID channel, Set<Criteria<?>> c ) ;		
		public void pUpdate( ID channel, Collection<Criteria<?>> c ) ;
		
		public void fUpdate( ID channel, Set<Criteria<?>> c ) ;		
		public void fUpdate( ID channel, Collection<Criteria<?>> c ) ;		

	}
}
