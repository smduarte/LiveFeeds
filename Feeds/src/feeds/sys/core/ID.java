package feeds.sys.core;

import java.io.* ;

import feeds.sys.FeedsNode;

@SuppressWarnings("serial")
final public class ID implements Serializable, Comparable<ID> {
	private static final int RADIX = 32 ;
	
	private final int minor ;
	private final long major ;
	private ID _major ;
	
	public ID() {
		minor = 0 ;
		major = (FeedsNode.rnd().nextLong() >>> 1) ;
	}
		
	public ID( DataInput is ) throws IOException {
		major = is.readLong() ;
	    minor = is.readInt();
	}

	public ID( long major ) {
		this.major = major ;
		this.minor = 0 ;
	}

	public ID( long major, int minor ) {
		this.major = major ;
		this.minor = minor ;
	}
	 
	
	public ID( String s) {
		int i = s.indexOf(':') ;
		if( i > 0 ) {
			major = Long.parseLong(s.substring(0,i), RADIX) ;
			minor = Integer.parseInt(s.substring(i+1)) ;
		} else {
			major = Long.parseLong(s, RADIX ) ;
			minor = 0 ;
		}
	}
	
	public boolean equalsIgnoreMinor( ID other ) {
		return other != null && this.major == other.major ;
	}
	
	public ID writeTo( DataOutput os ) throws IOException {
        os.writeLong( major ) ;
        os.writeInt( minor ) ;
        return this ;
    }
    
    public ID readFrom( DataInput is ) throws IOException {
        return new ID( is ) ;
    }
	
	public int compareTo( ID other ) {
		return this.major == other.major ? 0 : this.major < other.major ? -1 : 1 ;
	}
	
	public int hashCode() {
		return (int)(((major >>> 32) ^ major ^ minor) & 0x0FFFFFFFFL) ;
	}
	
	public boolean equals( Object other ) {
		return equals( (ID) other ) ;
	}
	
	public boolean equals( ID other ) {
		return other != null && this.major == other.major && this.minor == other.minor ;
	}
	
	synchronized public ID minor() {
		return new ID( major, ++g_minor ) ; 
	}
	
	synchronized public ID major() {
		return _major != null ? _major : (_major = new ID( major, 0 )) ; 
	}
	
	public long longValue() {
		return major ;
	}
	
	public String toString() {
		String r = Long.toString(major, RADIX)+ (minor == 0 ? "" : ':' + Integer.toString(minor)) ;
		return r.equals("0") ? "?" : r ;
	}
	
	private static int g_minor = 0 ;
}
