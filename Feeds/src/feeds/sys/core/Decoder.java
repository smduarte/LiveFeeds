package feeds.sys.core;

import java.io.* ;

public class Decoder extends DataInputStream {
	
	public Decoder( InputStream is ) throws IOException {
		super( is ) ;
	}
	
	public ID readID() throws IOException {		
		return new ID( this ) ;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T readObject() throws IOException {
		try {
			if( ois == null ) {
				ois = new ObjectInputStream( this ) ;
			}		
			return (T) ois.readObject() ;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	private ObjectInputStream ois ;
}
