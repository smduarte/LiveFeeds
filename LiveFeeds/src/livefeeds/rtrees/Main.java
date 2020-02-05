package livefeeds.rtrees;

import static livefeeds.rtrees.config.Config.Config;
import static livefeeds.rtrees.stats.Statistics.Statistics;
import static simsim.core.Simulation.DisplayFlags.SIMULATION;
import static simsim.core.Simulation.DisplayFlags.THREADS;
import static simsim.core.Simulation.DisplayFlags.TIME;

import java.util.EnumSet;

import livefeeds.rtrees.gui.DeadNodesDisplay;
import livefeeds.rtrees.gui.ViewDisplay;

import simsim.core.Displayable;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.core.Task;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.XY;


public class Main extends Simulation implements Displayable {


	public Main() {
		super(1, EnumSet.of(TIME, THREADS, SIMULATION));
	}
	
	public Main init() {

		Gui.setDesktopSize(1280, 800);
		Gui.setFrameRectangle("MainFrame", 600, 0, 768, 768);
//		Gui.addDisplayable("Network", Network, 0.01);
//		Gui.setFrameRectangle("Network", 0, 504, 216, 216);
//		Gui.setFrameTransform("Network", 1000, 1000, 0.01, true);

		Gui.addDisplayable("View", new ViewDisplay(), 1);
		Gui.setFrameRectangle("View", 220, 504, 216, 216);
		Gui.setFrameTransform("View", 1000, 1000, 0.0, false);
//		
//		Gui.addDisplayable("DeadNodes", new DeadNodesDisplay(), 5);
//		Gui.setFrameRectangle("DeadNodes", 700, 10, 512, 512);
//		Gui.setFrameTransform("DeadNodes", 1000, 1000, 0.0, false);
		
		Gui.setDesktopSize(1600, 1000) ;
//		Gui.maximizeFrame("LatencyHistogram");
		
//		new Task( Config.churn.nextArrival() ) {
//			public void run() {
//				new Task(0) {
//					public void run() {
//						new BroadcastNode().init();
////						for( CatadupaNode i : GlobalDB.liveNodes() ) {
////							((BroadcastNode)i).chordRT.populate() ;
////						}
//					}
//				};
//				if( GlobalDB.size() < 50000 + rg.nextInt(1000) || currentTime() < Config.MAX_SESSION_DURATION / 2 ) 
//					reSchedule( Config.churn.nextArrival() / (1 + currentTime()/100 ));
//			}
//		};
		int N, h = 1 ;
		double B = Config.BROADCAST_MAX_FANOUT ;
		do {
			h++ ;
			N = (int)(Math.pow(B, h+1) - 1 / ( B - 1 )) ;
		} while( N < 2000) ;
		
		for( int i = N + 1; --i >= 0 ; ) {
			new BroadcastNode().init();
		}
		
		new PeriodicTask( 60.0 + rg.nextDouble() ) {
			public void run() {
//				GlobalDB.gc() ;
			}
		};
			
		new PeriodicTask( Config.BROADCAST_START, 30) {
			public void run() {
//				for( CatadupaNode i : GlobalDB.liveNodes() ) {
//					System.out.println( ((BroadcastNode)i).received );
////					//((BroadcastNode)i).chordRT.populate() ;
//				}
			}
		};

		new PeriodicTask( Config.BROADCAST_START, 1) {
			public void run() {
				double size = Config.BROADCAST_PAYLOAD_MIN_SIZE + rg.nextDouble() * ( Config.BROADCAST_PAYLOAD_MAX_SIZE - Config.BROADCAST_PAYLOAD_MIN_SIZE );
				BroadcastNode root = GlobalDB.randomLiveNode() ;
				root = Config.BROADCAST_RANDOM_ROOT ? root : (BroadcastNode)GlobalDB.succ(GlobalDB.MAX_KEY/2) ;
				root.startBroadcast( (int)size ) ;
			}
		};

		
		new PeriodicTask(300.0 + rg.nextDouble() ) {
			public void run() {
//				ArrivalsDB.gc();	
//				ArrivalsDB.redundancy() ;				
			}
		};
		
		new Task( Config.BROADCAST_START + 4.1 * 3600 ) {
			public void run() {
				System.exit(0) ;
			}
		};
		
//		new PeriodicTask(300 + rg.nextDouble() ) {
//			public void run() {
//				int aM = Integer.MIN_VALUE, am = Integer.MAX_VALUE ;
//				int bM = Integer.MIN_VALUE, bm = Integer.MAX_VALUE ;
//				for( CatadupaNode i : GlobalDB.liveNodes() ) {
//					if( i.state.joined) {
//						aM = Math.max( aM, i.state.db.knownNodes.top) ;
//						am = Math.min( am, i.state.db.knownNodes.base) ;
//
//						bM = Math.max( bM, i.state.db.view.stamps.top) ;
//						bm = Math.min( bm, i.state.db.view.stamps.base) ;
//					}
//				}
//			}
//		};

		//Process2.injectEvents() ;
		
		super.setSimulationMaxTimeWarp(1e10);

		View.init() ;
		Statistics.init() ;
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
			i.displayOn(canvas);
		}

//		XY mouse = canvas.sMouse();
//		BroadcastNode closest = null ;
//		for (CatadupaNode i : GlobalDB.liveNodes() ) {
//			BroadcastNode j = (BroadcastNode)i ;
//			if (closest == null || mouse.distance( j.chordRT.pos) < mouse.distance( closest.chordRT.pos))
//				closest = j;
//		}
//
//		// If mouse pointer is close to a node, then show its routing table
//		if( closest != null && mouse.distance( closest.chordRT.pos ) < 9.0 ) {
//			canvas.gs.setColor( RGB.RED ) ;
//			canvas.sFill( new Circle( closest.chordRT.pos, 20 )) ;			
//			closest.chordRT.displayOn( canvas ) ;
//		}
//		
		

	}
}






