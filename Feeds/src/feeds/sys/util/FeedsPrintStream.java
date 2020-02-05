package feeds.sys.util;

import java.io.*;

import feeds.api.Feeds;
import feeds.sys.FeedsNode;

public class FeedsPrintStream extends PrintStream {
	
	public FeedsPrintStream() {
		this( System.out );
	}

	public FeedsPrintStream( PrintStream out ) {
		super( out );
	}
	
	public void write(byte[] buf, int off, int len) {
		for (int i = 0; i < len; i++)
			write(buf[off + i]);
	}

	public void write(int b) {
		if (b == '\n' || b == '\r') {
			byte[] preffix = String.format("%12.4f %-16s ", Feeds.time(), "[ " + FeedsNode.id() + " ]" ).getBytes() ;
			super.write( preffix, 0, preffix.length ) ;
			this.flush() ;			
			super.write('\n') ;
		}
		else
			tmp.write(b);
	}

	public void close() {
		flush() ;
		super.close() ;
	}
	
	public void flush() {
		int n = tmp.size() ;
			if( n > 0 ) {
			super.write( tmp.toByteArray(), 0, n ) ;
			super.flush() ;
			tmp.reset() ;
		}
	}
	
	final ByteArrayOutputStream tmp = new ByteArrayOutputStream() ;
}
