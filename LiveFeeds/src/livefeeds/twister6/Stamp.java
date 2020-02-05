package livefeeds.twister6;

public class Stamp implements Comparable<Stamp> {
	
	final public long key ;
	final public int c_serial ;
	final public int p_serial ;
	final public int g_serial = g_counter++;
		
	public static int g_counter = 0 ;
	
	public Stamp( long k, int cs, int ps ) {
		this.key = k ; 
		this.c_serial = cs ;
		this.p_serial = ps ;
	}
	
	public Stamp( long k, int s ) {
		this.key = k ; 
		this.c_serial = s;
		this.p_serial = -1 ;
	}
	
	public Stamp clone() {
		return new Stamp( key, c_serial, p_serial ) ;
	}
	
	public int hashCode() {
		return g_serial ;
	}
			
	public boolean equals( Stamp other ) {
		return g_serial == other.g_serial ;
	}
	
	public boolean equals( Object other ) {
		return equals( (Stamp) other ) ;
	}
	
	public String toString() {
		return String.format("<%d, %d/%d/%d>", key, c_serial, p_serial, g_serial ) ;
	}
		
	final public int length() {
		return 16 + 2*4 ;
	}

	private static final long serialVersionUID = 1L;

	public int compareTo(Stamp other) {
		return g_serial - other.g_serial ;
	}
}