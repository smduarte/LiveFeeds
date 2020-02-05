package livefeeds.twister6;

import java.security.MessageDigest;
import java.util.Arrays;

public class Hash {

	final byte[] hash ;
	
	public Hash( String s ) {
		digest.reset() ;
		hash = digest.digest( s.getBytes() ) ;
	}
	
	public Hash( byte[] b ) {
		digest.reset() ;
		hash = digest.digest( b ) ;
	}
	
	public Hash( Hash other ) {
		this.hash = new byte[ other.hash.length ] ;
		System.arraycopy(other.hash, 0, this.hash, 0, this.hash.length ) ;
	}
	
	public int hashCode() {
		return Arrays.hashCode(hash) ;
	}

	public boolean equals( Object other ) {
		return Arrays.equals( hash, ((Hash) other).hash ) ;
	}
	
	public String toString() {
		final char[] hexChars = "0123456789ABCDEF".toCharArray() ;
		final char[] r = new char[hash.length * 2 ] ;
		int k = 0 ;
		for( byte i : hash ) {
			int j = i & 255 ;
			r[k++] = hexChars[ j >>> 4 ] ;
			r[k++] = hexChars[ j & 15 ] ;
		}
		return new String( r ) ;
	}
	
	static MessageDigest digest ;

	static {
		try {
		digest = MessageDigest.getInstance("SHA-1");
		} catch( Exception x ){}
	}

}
