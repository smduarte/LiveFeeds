package livefeeds.twister6;

import static livefeeds.twister6.config.Config.Config;
import static livefeeds.twister6.stats.Statistics.Statistics;
import static simsim.core.Simulation.DisplayFlags.SIMULATION;
import static simsim.core.Simulation.DisplayFlags.THREADS;
import static simsim.core.Simulation.DisplayFlags.TIME;
import static simsim.logging.Log.Log;

import java.util.EnumSet;

import livefeeds.twister6.gui.DeadNodesDisplay;
import livefeeds.twister6.gui.ViewDisplay;

import simsim.core.Displayable;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.core.Task;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;

public class Main extends Simulation implements Displayable {


	public Main() {
		super(1, EnumSet.of(TIME, THREADS, SIMULATION));
	}
	
	
	public Main init() {

		Statistics.init() ;
		ArrivalsDB.init() ;
		GlobalDB.init() ;
		View.init() ;
		
		new Task( Config.churn.nextArrival() ) {
			public void run() {
				new Task(0) {
					public void run() {
						new CatadupaNode().init();
//						new TurmoilNode().init();
					}
				};
				reSchedule( Config.churn.nextArrival() );
			}
		};

		new PeriodicTask(300 + rg.nextDouble() ) {
			public void run() {
				int aM = Integer.MIN_VALUE, am = Integer.MAX_VALUE ;
				int bM = Integer.MIN_VALUE, bm = Integer.MAX_VALUE ;
				for( CatadupaNode i : GlobalDB.liveNodes() ) {
					if( i.state.joined) {
						aM = Math.max( aM, i.state.db.knownNodes.top) ;
						am = Math.min( am, i.state.db.knownNodes.base) ;

						bM = Math.max( bM, i.state.db.view.stamps.top) ;
						bm = Math.min( bm, i.state.db.view.stamps.base) ;
						
						//System.out.println( i.state.db.view );
					}
				}
				Log.finer(String.format("aMAX:%d  aMIN:%d\n", aM, am));
				Log.finer(String.format("bMAX:%d  bMIN:%d\n", bM, bm));
			}
		};

		//TurmoilNode.injectEvents() ;
		
		super.setSimulationMaxTimeWarp(1e10);

		Gui.setDesktopSize(1280, 800);
		Gui.setFrameRectangle("MainFrame", 0, 504, 216, 216);
//		Gui.addDisplayable("Network", Network, 0.01);
//		Gui.setFrameRectangle("Network", 0, 504, 216, 216);
//		Gui.setFrameTransform("Network", 1000, 1000, 0.01, true);

		Gui.addDisplayable("View", new ViewDisplay(), 5);
		Gui.setFrameRectangle("View", 220, 504, 216, 216);
		Gui.setFrameTransform("View", 1000, 1000, 0.0, false);
////		
		Gui.addDisplayable("DeadNodes", new DeadNodesDisplay(), 5);
		Gui.setFrameRectangle("DeadNodes", 700, 10, 512, 512);
		Gui.setFrameTransform("DeadNodes", 1000, 1000, 0.0, false);
		
		Gui.setDesktopSize(1600, 1000) ;
//		Gui.maximizeFrame("LatencyHistogram");

		
		super.start() ;
		return this ;
	}

	final Pen pen = new Pen(RGB.BLACK, 1);

	public void displayOn(Canvas canvas) {

		pen.useOn(canvas.gs);

		double ON = 0;
		for (CatadupaNode i : GlobalDB.liveNodes()) {
			if (i.isOnline())
				ON++;
		}
		double DN = GlobalDB.size();

		double JN = 0, WN = 0, LF = 0;
		for (CatadupaNode i : GlobalDB.liveNodes()) {
			if (i.isOnline()) {
				JN += (i.state.joined ? 1 : 0);
				WN += (!i.state.joined && i.state.db.loadedEndpoints ? 1 : 0);
				LF += (i.state.db.loadedEndpoints && !i.state.db.loadedFilters? 1 : 0) ;
			}
		}

		canvas.uDraw(String.format("ON: %4.0f (%4.0f)", ON, DN), 2, 48);
		canvas.uDraw(String.format("JN: %4.0f + WN: %4.0f = %4.0f (%4.0f) (LF=%4.0f%%)", JN, WN, JN + WN, ON, 100*LF/ON), 2, 68);

		View.GV.trim(Config.VIEW_CUTOFF);
		for (CatadupaNode i : GlobalDB.liveNodes()) {
			if (i.isOnline())
				i.displayOn(canvas);
		}
	}
}






