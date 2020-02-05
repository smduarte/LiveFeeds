package livefeeds.test;

import static simsim.core.Simulation.DisplayFlags.SIMULATION;
import static simsim.core.Simulation.DisplayFlags.THREADS;
import static simsim.core.Simulation.DisplayFlags.TIME;

import java.util.EnumSet;

import simsim.core.Displayable;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;

import umontreal.iro.lecuyer.probdist.ExponentialDist;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.util.Num;

public class Main extends Simulation implements Displayable {
	
	final double MEAN = 0.2 ;	
	final RandomVariateGen aGen = new RandomVariateGen(new MRG32k3a(), new ExponentialDist(1.0/MEAN));
	
	final int[] arrivals = new int[ 1000 ] ;
	

	Main() {
		super(5, EnumSet.of( TIME, THREADS, SIMULATION ));
	}
	
	public Main init() {
		
		
		new PeriodicTask(1) {
			public void run() {
				int i = (int)(aGen.nextDouble() * arrivals.length) ;
				if( i >= 0 && i < arrivals.length )
					arrivals[i]++ ;
				
				System.out.println( arrivals[0]) ;
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
		
		
		double T = 0, M = Integer.MIN_VALUE ;
		for( int i : arrivals ) {
			T += i ;
			M = Math.max( M, i ) ;
		}
		
		new Pen( RGB.BLUE, 3).useOn( canvas.gs ) ;

		double sX = 1000.0 / arrivals.length, sY = 1000.0 / M ;

		for( int i = 0 ; i < arrivals.length ; i++ ) {
			double x = i * sX ;
			double y = 1000 - arrivals[i] * sY ;
			canvas.sDraw( new Circle(x,y,2) ) ;
		}
	}
}

class Gamma {
	static double func(double x) {
		return Math.exp( Num.lnGamma(x)) ;
	}
}