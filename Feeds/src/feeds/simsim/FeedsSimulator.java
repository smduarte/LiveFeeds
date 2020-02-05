package feeds.simsim;

import static simsim.core.Simulation.DisplayFlags.SIMULATION;
import static simsim.core.Simulation.DisplayFlags.SPANNER;
import static simsim.core.Simulation.DisplayFlags.THREADS;
import static simsim.core.Simulation.DisplayFlags.TIME;
import static simsim.core.Simulation.DisplayFlags.TRAFFIC;

import java.util.EnumSet;

import simsim.core.Displayable;
import simsim.core.Globals;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;

public class FeedsSimulator extends Simulation implements Displayable {

	public static final int TOTAL_pNODES = 10;
	public static final int TOTAL_sNODES = 5;
	public static final int TOTAL_cNODES = 20;

	FeedsSimulator() {
		super( 5, EnumSet.of( SPANNER, TIME, TRAFFIC, THREADS, SIMULATION ));
	}
	
	FeedsSimulator init() {
		Spanner.setThreshold(1e10) ;
		
		Gui.setFrameRectangle("MainFrame", 0, 0, 480, 480);		
		Gui.maximizeFrame("MainFrame") ;

		for (int i = 0; i < TOTAL_pNODES; i++)
			new pNode();

		for( int i = 0; i < TOTAL_sNODES; i++) 
			new sNode() ;
	
		System.out.println("Allocated secondary nodes...") ;

		for( int i = 0; i < TOTAL_cNODES; i++)
			new cNode() ;
		
		System.out.println("Allocated client nodes...") ;

		
		for (Node i : Node.nodes())
			i.init();

		
		//super.setSimulationMaxTimeWarp(50);

		return this;
	}

	public static void main(String[] args) throws Exception {

		Globals.set("Net_Jitter", 0.0) ;
		Globals.set("Sim_RandomSeed", 1L);
		Globals.set("Net_RandomSeed", 3L);

		Globals.set("Net_FontSize", 18.0f ) ;
		Globals.set("Net_Euclidean_NodeRadius", 15.0);
		Globals.set("Net_Euclidean_CostFactor", 0.0001);		
		Globals.set("Net_Euclidean_DisplayNodeLabels", true);
		Globals.set("Net_Euclidean_MinimumNodeDistance", 500.0);

		Globals.set("Traffic_DeadPacketHistory", 5.0) ;
		Globals.set("Traffic_DisplayDeadPackets", true ) ;
		Globals.set("Traffic_DisplayDeadPacketsHistory", "time" ) ;

		new FeedsSimulator().init().start();
	}

	final Pen pen = new Pen( RGB.BLACK, 1) ; 
	// ------------------------------------------------------------------------------------------------------------------
	public void displayOn( Canvas canvas ) {
		pen.useOn( canvas.gs) ;
		for( Node i : Node.nodes() )
			i.displayOn( canvas ) ;
	}
}