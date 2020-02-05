package livefeeds.sift0;

import static livefeeds.sift0.config.Config.Config;
import static livefeeds.sift0.stats.Statistics.Statistics;
import static simsim.core.Simulation.DisplayFlags.THREADS;
import static simsim.core.Simulation.DisplayFlags.TIME;
import static simsim.logging.Log.Log;

import java.util.EnumSet;

import livefeeds.sift0.gui.DeadNodesDisplay;
import livefeeds.sift0.gui.MainDisplay;
import livefeeds.sift0.gui.ViewDisplay;

import simsim.core.Displayable;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.core.Task;

public class Main extends Simulation implements Displayable {


	public Main() {
		super(1, EnumSet.of(TIME, THREADS));
	}
	
	public Main init() {

		Statistics.init() ;
		ArrivalsDB.init() ;
		GlobalDB.init() ;
		View.init() ;
		
		new Task( Config.churn.nextArrival() ) {
			public void run() {
					new CatadupaNode().init();
	//				new TurmoilNode().init();
				reSchedule( Config.churn.nextArrival() );
			}
		};
		
//		new PeriodicTask( rg.nextDouble() * 30 * 60,  4 * 3600 ) {
//			public void run() {
//				final int[] D = new int[1] ;
//				for( CatadupaNode i : new ArrayList<CatadupaNode>(GlobalDB.liveNodes()) )
//					if( rg.nextDouble() < 0.725 ) {
//						i.shutdown() ;
//						D[0]++;
//					}
//				
////				
////				new Task( Config.churn.nextArrival() ) {
////					int N = D[0] ;
////					public void run() {				
////						new CatadupaNode().init() ;
////						if( N-- > 0 )
////							reSchedule( Config.churn.nextArrival()/100);					
////					}
////				};
//			}
//		};
		
//		TurmoilNode.injectEvents() ;
		
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
		
		super.setSimulationMaxTimeWarp(1e10);

		new Task( Config.BATCH_TIME_LIMIT * Config.MAX_SESSION_DURATION ) {
			public void run() {
				System.exit(0) ;
			}
		};
		
		if( false ) {
			Gui.setDesktopSize(1280, 800);
			Gui.setFrameRectangle("MainFrame", 0, 504, 216, 216);
			Gui.addDisplayable("MainFrame", new MainDisplay(), 1) ;
			
			Gui.addDisplayable("Network", Network, 0.01);
			Gui.setFrameRectangle("Network", 0, 504, 216, 216);
			Gui.setFrameTransform("Network", 1000, 1000, 0.01, true);
	
			Gui.addDisplayable("View", new ViewDisplay(), 5);
			Gui.setFrameRectangle("View", 220, 504, 216, 216);
			Gui.setFrameTransform("View", 1000, 1000, 0.0, false);
	
			Gui.addDisplayable("DeadNodes", new DeadNodesDisplay(), 5);
			Gui.setFrameRectangle("DeadNodes", 700, 10, 512, 512);
			Gui.setFrameTransform("DeadNodes", 1000, 1000, 0.0, false);
		}

		
		Gui.setDesktopSize(1600, 1000) ;
//		Gui.maximizeFrame("LatencyHistogram");

		super.start() ;
		return this ;
	}

	
}






