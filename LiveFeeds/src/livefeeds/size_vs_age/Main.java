package livefeeds.size_vs_age;

import static livefeeds.rtrees.config.Config.Config;
import static simsim.core.Simulation.Gui;
import static simsim.core.Simulation.DisplayFlags.SIMULATION;
import static simsim.core.Simulation.DisplayFlags.THREADS;
import static simsim.core.Simulation.DisplayFlags.TIME;
import static simsim.core.Simulation.DisplayFlags.TRAFFIC;

import java.util.EnumSet;

import simsim.core.Displayable;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.core.Task;
import simsim.gui.canvas.Canvas;
import simsim.gui.charts.XYLineChart;
import simsim.ssj.BinnedTally;

import umontreal.iro.lecuyer.stat.Tally;

public class Main extends Simulation implements Displayable {


	public Main() {
		super(1, EnumSet.of(TIME, THREADS, SIMULATION, TRAFFIC));
	}
	
	
	public Main init() {

		Gui.setDesktopSize(1280, 800);
		new Task( Config.churn.nextArrival() ) {
			public void run() {
				new Task(0) {
					public void run() {
						new Node().init();
					}
				};
				reSchedule( Config.churn.nextArrival() );
			}
		};

		new AgeVsSize("Age vs Size") ;
		
		
		new PeriodicTask(60) {
			public void run() {
				//System.out.println( GlobalDB.size() ) ;
			}
		};
		super.setSimulationMaxTimeWarp(1e10);

		super.start() ;

		return this ;
	}
	
	public static void main( String[] args) throws Exception {
//		new Catadupa2() ;
		new Main().init() ;
	}
}

class AgeVsSize {
	
	private String frame ;
	private XYLineChart age ;
	
	public AgeVsSize( String frame ) {
		this.frame = frame ;
		init() ;
	}
	
	public void saveChart(String pdfName ) {
		try {
			prepare() ;
			age.saveChartToPDF(pdfName, 500, 500) ;
		} catch( Exception x ) {
			x.printStackTrace() ;
		}		
	}
	
	private void prepare() {
		BinnedTally t = new BinnedTally("age") ;
		double maxAge = Config.MAX_SESSION_DURATION ;
		
		double binSize = 15 * 60 ;
		int bins = (int)(maxAge / binSize ) ;
		
		for( int i = 0 ; i < bins ; i ++) {
			double n = 0;
			for( Node j : GlobalDB.nodes )
				if( j.upTime() < (i + 1) * binSize )
					n++ ;
			
			t.bin(i).add( 100 * n / GlobalDB.size() ) ;
		}
		
		age.getSeries("1").clear() ;		
		int j = 0;
		for( Tally i : t.bins ) {
				age.getSeries("1").add( j * binSize/3600, i.max()) ;
			j++ ;
		}
	}
	
	void init() {
		age = new XYLineChart("Size vs Age", 0.0, "percentil", "age") ;
		age.setYRange(false, 0, 100) ;

		
		Gui.addDisplayable(frame, new Displayable() {
			public void displayOn( Canvas canvas) {	
				prepare() ;
				age.displayOn( canvas) ;
			}
		}, 0.5) ;
		
		Gui.setFrameRectangle(frame, 0, 0, 480, 480) ;
		Gui.setFrameTransform(frame, 500, 500, 0, false) ;		
	}
}
