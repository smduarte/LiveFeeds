package feeds.sys.core;

import java.io.* ;

public class Encoder extends DataOutputStream {

	public Encoder( OutputStream os ) throws IOException {
		super( os );
	}

	public void writeID( ID value ) throws IOException {
		value.writeTo( this ) ;
	}
	
	public void writeObject( Object value ) throws IOException {
		if( oos == null ) {
			oos = new ObjectOutputStream( this ) ;
		}
		oos.writeObject( value ) ;
		oos.flush() ;
	}
	
	private ObjectOutputStream oos ;
}