package livefeeds.foo;

import static simsim.core.Simulation.DisplayFlags.SIMULATION;
import static simsim.core.Simulation.DisplayFlags.THREADS;
import static simsim.core.Simulation.DisplayFlags.TIME;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;

import simsim.core.Displayable;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.core.Task;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Line;

import umontreal.iro.lecuyer.probdist.ExponentialDist;
import umontreal.iro.lecuyer.probdist.TruncatedDist;
import umontreal.iro.lecuyer.probdist.WeibullDist;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.util.Num;

public class Main extends Simulation implements Displayable {
	
	public static final double AVERAGE_ARRIVAL_RATE = 3.8;
	public static final double MAX_SESSION_DURATION = 8 * 3600;
	public static final double MEAN_SESSION_DURATION = 4 * 3600;

	final double aRate = AVERAGE_ARRIVAL_RATE ;	

	double shape = 1.8 ;
	private double lambda = MEAN_SESSION_DURATION / Gamma.func(1 + 1 / shape);


	final RandomVariateGen aGen = new RandomVariateGen(new MRG32k3a(), new ExponentialDist(AVERAGE_ARRIVAL_RATE));
	final RandomVariateGen sGen = new RandomVariateGen(new MRG32k3a(), new TruncatedDist(new WeibullDist(shape, 1 / lambda, 0), 0, MAX_SESSION_DURATION));
	
	final int[] sessionTimes = new int[ 200 ] ;
	
	Tally size = new Tally("Size");
	Tally arrivals = new Tally("Arrivals");
	Tally sessions = new Tally("Sessions");
		
	HashSet<Node> nodes = new HashSet<Node>() ;
	
	Main() {
		super(5, EnumSet.of( TIME, THREADS, SIMULATION ));
	}
	
	public Main init() {
		
		System.out.println("Here");
		
		new Task( aGen.nextDouble()) {
			public void run() {				
				new Node( sGen.nextDouble() ) ;
				reSchedule( aGen.nextDouble());
			}
		};

//		new PeriodicTask( rg.nextDouble() * 30 * 60,  0.1 ) {
//			public void run() {
//				
//			}
//		};

		new PeriodicTask( rg.nextDouble() * 30 * 60,  9 * 3600 ) {
			public void run() {
				final int[] D = new int[1] ;
				for( Node i : new ArrayList<Node>(nodes) )
					if( rg.nextDouble() < 0.725 ) {
						i.kill() ;
						D[0]++;
					}
				
				
				new Task( aGen.nextDouble()) {
					int N = D[0] ;
					public void run() {				
						new Node( sGen.nextDouble() ) ;
						if( N-- > 0 )
							reSchedule( aGen.nextDouble()/100);					
					}
				};
			}
		};

		return this ;
	}
	/**
	 * @param args
	 */
	public static void main( String[] args) {
		new Main().init().start() ;
	}



	
	public void displayOn( Canvas canvas) {
		
		double total = nodes.size() ;
		canvas.uDraw( RGB.BLACK, String.format("Total:%.0f", total ), 20, 60) ;
		
		double T = 0, M = Integer.MIN_VALUE ;
		for( int i : sessionTimes ) {
			T += i ;
			M = Math.max( M, i ) ;
		}
		
		new Pen( RGB.BLUE, 3).useOn( canvas.gs ) ;
		
		double s = 1000.0 / M ;
		double x0 = 0, y0 = 1000, x = 0, dx = 1000.0 / sessionTimes.length;
		for( int i : sessionTimes ) {
			x += dx ;
			double y = 1000 - s * i ;
			canvas.sDraw( new Line(x0, y0, x, y) ) ;
			x0 = x ;
			y0 = y ;
		}
		new Pen( RGB.GREEN, 3).useOn( canvas.gs ) ;
		
		double I = 0 ;
		s = 1000 / T ;
		x0 = 0; y0 = 1000; x = 0;
		for( int i : sessionTimes ) {
			x += dx ;
			double y = 1000 - s * I ;
			canvas.sDraw( new Line2D.Double(x0, y0, x, y) ) ;
			x0 = x ;
			y0 = y ;
			I += i ;
		}
	}
	
	
	class Node extends Task {
		
		double startTime = currentTime() ;
		
		Node( double due ) {
			super( due ) ;
			nodes.add(this) ;
		}
		
		public void run() {
			nodes.remove(this) ;
			
			double duration = currentTime() - startTime ;	
			//System.out.println(duration/3600);
			sessions.add( duration / 3600.0 ) ;
			int i = (int)( (sessionTimes.length * duration / MAX_SESSION_DURATION) ) ;
			if( i >= 0 && i < sessionTimes.length) 
				sessionTimes[i]++ ;				
		}
		
		public void kill() {
			super.cancel() ;
			run() ;
		}
	}
}

class Gamma {
	static double func(double x) {
		return Math.exp( Num.lnGamma(x)) ;
	}
}