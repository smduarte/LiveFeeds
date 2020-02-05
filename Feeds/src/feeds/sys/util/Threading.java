package feeds.sys.util;

public class Threading {
	
	static public Thread newThread( Runnable r, boolean daemon ) {
		Thread res = new Thread( r) ;
		res.setDaemon( daemon ) ;
		return res ;
	}
	
	static public void sleep( int ms ) {
		try { Thread.sleep( ms ) ;} catch( Exception x ){};
	}

	static public void sleep( int ms, int ns ) {
		try { Thread.sleep( ms, ns ) ;} catch( Exception x ){};
	}

	static public void waitOn( Object o ) {
		try { o.wait() ;} catch( Exception x ){ x.printStackTrace() ; }
	}

	static public void waitOn( Object o, int ms ) {
		try { o.wait( ms) ;} catch( Exception x ){ x.printStackTrace() ; }
	}
	
	static public void notifyOn( Object o ) {
		o.notify() ;
	}
	
	static public void notifyAllOn( Object o ) {
		o.notifyAll() ;
	}

	
	static public void synchronizedWaitOn( Object o ) {
		synchronized( o ) {
			try { o.wait() ;} catch( Exception x ){ x.printStackTrace() ; }
		}
	}

	static public void synchronizedWaitOn( Object o, int ms ) {
		synchronized( o ) {
			try { o.wait( ms) ;} catch( Exception x ){ x.printStackTrace() ; }
		}
	}
	
	static public void synchronizedNotifyOn( Object o ) {
		synchronized( o ) {			
			o.notify() ;
		}
	}
	
	static public void synchronizedNotifyAllOn( Object o ) {
		synchronized( o ) {
			o.notifyAll() ;
		}
	}
}
