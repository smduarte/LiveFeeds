package feeds.sys.membership.containers.impl;

import java.util.*;

import feeds.api.*;
import feeds.sys.core.*;
import feeds.sys.membership.containers.CriteriaDB;

public class CriteriaDB_Impl extends Container<CriteriaDB> implements CriteriaDB, CriteriaDB.Updater {
	
	public Map<ID, Set<Criteria<?>>> pData() {
		return Collections.unmodifiableMap(pData) ;
	}

	public Map<ID, Set<Criteria<?>>> fData() {
		return Collections.unmodifiableMap(fData) ;
	}

	public boolean isEmpty() {
		return pData.isEmpty() && fData.isEmpty() ;
	}
	
	public void pUpdate(ID channel, Set<Criteria<?>> newSet ) {
		Set<Criteria<?>> oldSet = pData.get( channel ) ;
		//Feeds.out.printf("oldSet:%s, newSet:%s - equals:%s\n", oldSet, newSet, oldSet == null ? "?" : oldSet.equals(newSet)) ;

		if( oldSet == null || ! oldSet.equals( newSet )) {
			pData.put( channel, newSet ) ;
			super.notifyUpdateNow() ; //now is debug
		}
	}

	public void fUpdate(ID channel, Set<Criteria<?>> newSet ) {
		Set<Criteria<?>> oldSet = fData.get( channel ) ;
		//Feeds.out.printf("oldSet:%s, newSet:%s - equals:%s\n", oldSet, newSet, oldSet == null ? "?" : oldSet.equals(newSet)) ;

		if( oldSet == null || ! oldSet.equals( newSet )) {
			fData.put( channel, newSet ) ;
			super.notifyUpdateNow() ; // now is debug
		}
	}
	
	public void pUpdate(ID channel, Collection<Criteria<?>> col ) {
		pUpdate(channel, new HashSet<Criteria<?>>( col ) ) ;
	}

	public void fUpdate(ID channel, Collection<Criteria<?>> col ) {
		fUpdate(channel, new HashSet<Criteria<?>>( col ) ) ;
	}

	Map<ID, Set<Criteria<?>>> pData = new HashMap<ID, Set<Criteria<?>>>() ;
	Map<ID, Set<Criteria<?>>> fData = new HashMap<ID, Set<Criteria<?>>>() ;	
}
