package livefeeds.rtrees;

import java.io.*;
import java.util.*;

import simsim.core.Simulation;

public class myRandom extends Random {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Random rg;
	PrintStream ps ;
	
	public myRandom() {
		this( System.out ) ;
	}

	public myRandom( long seed ) {
		this( seed, System.out ) ;
	}

	public myRandom( PrintStream p) {
		this( 0L, p ) ;
	}

	public myRandom(long seed, PrintStream p) {
		rg = seed == 0L ? new Random() : new Random(seed);
		ps = p ;
	}

	synchronized public void nextBytes(byte[] bytes) {
		ps.printf("%s - nextBytes: %d [%s]\n", getSeq(), bytes.length, getStack() ) ;
		rg.nextBytes(bytes);
	}

	synchronized public int nextInt() {
		int res = rg.nextInt() ;
		ps.printf("%s - nextInt:%d [%s]\n", getSeq(), res, getStack() ) ;
		return res ;
	}

	synchronized public int nextInt(int x) {
		int res = rg.nextInt(x) ;
		ps.printf("%s - nextInt(%d):%d [%s]\n", getSeq(), x, res, getStack() ) ;
		return res ;
	}

	synchronized public long nextLong() {
		long res = rg.nextLong() ;
		ps.printf("%s - nextLong:%d [%s]\n", getSeq(), res, getStack() ) ;
		return res ;
	}

	synchronized public boolean nextBoolean() {
		boolean res = rg.nextBoolean() ;
		ps.printf("%s - nextBoolean:%b [%s]\n", getSeq(), res, getStack() ) ;
		return res ;
	}

	synchronized public float nextFloat() {
		float res = rg.nextFloat() ;
		ps.printf("%s - nextFloat:%f [%s]\n", getSeq(), res, getStack() ) ;
		return res ;
	}

	synchronized public double nextDouble() {
		double res = rg.nextDouble() ;
		ps.printf("%s - nextDouble:%f [%s]\n", getSeq(), res, getStack() ) ;
		return res ;
	}

	synchronized public double nextGaussian() {
		double res = rg.nextGaussian() ;
		ps.printf("%s - nextGaussian:%f [%s]\n", getSeq(), res, getStack() ) ;
		return res ;
	}
	
	String getStack() {
		StackTraceElement[] t = Thread.currentThread().getStackTrace() ;
		String res = "" ;
		for( int i = 4 ; i < t.length ; i++ )
			res += getStackTrace(t[i]) ;
		
		return res ;
	}
	
	String getSeq() {
		return String.format("%d %f", n++, Simulation.currentTime() ) ;
	}
	
	String getStackTrace( StackTraceElement t ) {
		String method = t.getMethodName() ;
		String file = t.getFileName() ;
		file = file.substring(0, file.indexOf(".java") ) ;
		if( method.equals("processNextTask") ) return "" ;
		else return String.format("(%s %s:%d) ", method, file, t.getLineNumber() ) ;
	}
	
	int n = 0 ; 	
}
