package feeds.sys.catadupa;

import java.io.*;
import java.util.*;

@SuppressWarnings("serial")
public class gView implements Serializable {

	public gView() {
		this.data = new TreeMap<Long, Integer>()  ;
	}
	
	public gView( Stamp s ) {
		this.data = new TreeMap<Long, Integer>()  ;
		this.merge(s) ;
	}
	
	private gView( gView other ) {
		this.data = new TreeMap<Long, Integer>( other.data ) ;
	}

	public gView clone() {
		return new gView( this ) ;
	}
	
	public void merge( Stamp s ) {
		data.put( s.key, s.serial ) ;
	}
	
	public void merge( gView other ) {		
		for( Map.Entry<Long, Integer> i : other.data.entrySet() ) {
			Long key = i.getKey() ;			
			Integer otherSerial = i.getValue() ;
			Integer thisSerial = data.get( key ) ;
			if( thisSerial == null ) data.put( key, otherSerial) ;
			else data.put( key, Math.max( otherSerial, thisSerial) ) ;
		}
	}
	
	public boolean isDisjoint( gView other ) {
		for( Long i : other.data.keySet() )
			if( data.containsKey(i) ) return false ;
		return true ;
	}
	
	public String toString() {
		return data.toString() ;
	}
	
	public int size() {
		return data.size() ;
	}
	
	public Map<Long, Integer> data ;
}
