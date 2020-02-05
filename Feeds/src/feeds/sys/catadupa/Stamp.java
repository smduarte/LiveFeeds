package feeds.sys.catadupa;

import java.io.Serializable;

public class Stamp implements Comparable<Stamp>, Serializable {
	final public long key ;
	final public int serial ;
	
	public Stamp( long k, int s ) {
		this.key = k ; 
		this.serial = s ;
	}
		
	public int hashCode() {
		return (int)( (key >> 32) ^ (key ^ serial) & 0xFFFFFFFF);
	}
			
	public boolean equals( Stamp other ) {
		return serial == other.serial && key == other.key ;
	}
	
	public boolean equals( Object other ) {
		return equals( (Stamp) other ) ;
	}

	public int compareTo( Stamp other ) {
		int diff = serial - other.serial ;		
		return diff == 0 ? (key == other.key ? 0 : key < other.key ? -1 : 1 ) : diff ;
	}
	
	public String toString() {
		return String.format("<%d, %d>", key, serial ) ;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}