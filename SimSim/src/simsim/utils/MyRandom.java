package simsim.utils;

import simsim.core.Simulation;

public class MyRandom extends java.util.Random {

	private static final long serialVersionUID = 1L;


	public MyRandom() {}

	public MyRandom( long seed ) {
		super(seed) ;
	}
	
	public boolean 	nextBoolean() {
		boolean res = super.nextBoolean() ;
		log( res ) ;
		return res ;
	}
	
	public void nextBytes(byte[] bytes) {		
		log("") ;
		super.nextBytes(bytes) ;
	}

	public double nextDouble() {
		double res = super.nextDouble() ;
		log( res ) ;
		return res ;
	}

	public float nextFloat() {
		float res = super.nextFloat() ;
		log(res) ;
		return res ;
	}

	public double nextGaussian() {
		double res = super.nextGaussian() ;
		log( res ) ;
		return res ;
	}

	public int nextInt() {
		int res = super.nextInt() ;
		log( res) ;
		return res ;
	}

	public int nextInt(int n) {
		int res = super.nextInt( n ) ;
		log( res ) ;
		return res ;
	}

	public long nextLong() {
		long res = super.nextLong() ;
		log( res) ;
		return res ;
	}

	public void setSeed(long seed) {
		log(seed) ;
		super.setSeed(seed) ;
	}
	
	
	void log( Object res ) {
		StackTraceElement[] sta = Thread.currentThread().getStackTrace() ;
		try {
			System.err.printf("%.6f - %s.%s.%s.%s():%d -> %s() <- %s\n", Simulation.currentTime(), sta[4].getClassName(), sta[4].getMethodName(), sta[3].getClassName(), sta[3].getMethodName(),  sta[3].getLineNumber(), sta[2].getMethodName(), res ) ;
		} catch( Exception x ) {}
	}
}
