package feeds.sys.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Packet extends ByteArrayOutputStream implements Cloneable, Serializable {
   
	transient protected Encoder enc ;
	transient protected Decoder dec ;
	transient private int used = 0 ;
	
    public Packet() {
        super( 1024 ) ;
    }

    public Packet(byte[] buf) {
    	super.buf = buf ;
    }
    
    public Packet( DataInput di ) throws IOException {
    	super(di.readInt()) ;
    	di.readFully( buf ) ;
    }
    
    protected Packet( Packet p ) {
    	this( p.buf ) ;
    }
    
    public Packet( Decoder dec ) throws IOException {
    	super( dec.readInt() ) ;
    	dec.readFully( super.buf ) ;
    }
    
    public Packet(byte[] buf, int off, int len ) {
    	super( len ) ;
    	System.arraycopy( buf, off, super.buf, 0, len ) ;
    }
    
    public ByteBuffer toByteBuffer() {
    	return ByteBuffer.wrap( buf, 0, length() ) ;
    }
    
    public DatagramPacket toDatagramPacket( DatagramPacket d ) {
    	d.setData( buf ) ;
    	d.setLength( length() ) ;
    	return d ;
    }
    
    public DatagramPacket toDatagramPacket( InetAddress addr, int port ) {
    	return new DatagramPacket( buf, 0, length(), addr, port) ;
    }
    
    public byte[] toByteArray() {
    	byte[] ba = new byte[ length() ] ;
    	System.arraycopy( buf, 0, ba, 0, ba.length ) ;
    	return ba ;
    }
    
    public void writeTo( DataOutput out ) throws IOException {
    	int length = length()  ;
    	out.writeInt( length ) ;
    	out.write( buf, 0, length ) ;
    }
    
    public Packet readFrom( DataInput in ) throws IOException {
    	count = used = 0 ;
    	int length = in.readInt() ;
    	if( buf.length < length ) buf = new byte[length] ;
    	in.readFully( buf, 0, length ) ;
    	return this ;
    }
    
    public int size() {
    	return length() ;
    }
    
    public int length() {
    	return used + count == 0 ? buf.length : Math.max(used, count);
    }
    public byte peek( int index ) {
    	return buf[index] ;
    }

    public void rewind( int position ) {
    	used = Math.max( used, count) ;
    	count = position ;
    }

    public synchronized Encoder encoder() {
		if( enc == null ) {
			try { enc = new Encoder( this ) ; } catch( Exception x ) { x.printStackTrace() ; return null ; }
		}
		return enc ;
	}

	public synchronized Decoder decoder() {
		if( dec == null ) {
			rewind(0) ;
			try { dec = new Decoder( new ByteArrayInputStream( super.buf ) ) ; } catch( Exception x ) {  x.printStackTrace() ; return null ; }
		}
		return dec ;		
	}	
	
	public Object clone() {
    	try {
			return super.clone() ;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return this ;
		}
    }	
	
	private static final long serialVersionUID = 1L;
}