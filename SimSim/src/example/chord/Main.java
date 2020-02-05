package example.chord;

import static simsim.core.Simulation.DisplayFlags.SIMULATION;
import static simsim.core.Simulation.DisplayFlags.TIME;
import static simsim.core.Simulation.DisplayFlags.TRAFFIC;

import java.util.EnumSet;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;

import simsim.core.Displayable;
import simsim.core.Globals;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.XY;
import simsim.ssj.BinnedTally;
import simsim.ssj.charts.BinnedTallyDisplay;

public class Main extends Simulation implements Displayable {
	
	public static final int TOTAL_NODES = 1024 ;

	Main() {
		super( 20, EnumSet.of( SIMULATION, TIME,TRAFFIC ) ) ;
	}

	static public BinnedTally stretch = new BinnedTally( 50.0, "Overlay Stretch") ;		
	
	Main init() {

		super.setSimulationMaxTimeWarp(1) ;

		Gui.setDesktopSize(1030, 513) ;
		Gui.setFrameRectangle("MainFrame", 2, 2, 512, 512);
				
		//Create the simulation nodes
		for( int i = 0 ; i < TOTAL_NODES ; i++ ) 
			new Node() ;

		
		//Initialize the simulation nodes
		for( Node i : NodeDB.nodes() ) 
			i.init() ;
				
		
		//Test of Chord recursive routing to a known node...
		new PeriodicTask(0.2) {
			public void run() {
				NodeDB.randomNode().routeTo( NodeDB.randomNode().key ) ;
			}
		};

		new BinnedTallyDisplay("Overlay Stretch", stretch ) {						
			protected void init() {
				super.init() ;
								
				chart.chart().getXYPlot().setRangeAxis( new NumberAxis("accum %")) ;
				chart.chart().getXYPlot().setDomainAxis( new LogarithmicAxis("overlay stretch %") ) ;
				chart.setYRange(false, 0, 100) ;
				chart.setXRange(false, 100, 10000) ;
				chart.chart().setTitle("Cummulative Overlay Stretch") ;
				chart.chart().removeLegend();
				
				for( BinnedTally i : items )
					chart().setSeriesLinesAndShapes( i.name, true, false ) ;						
			}
		}.cumulative();
		Gui.setFrameRectangle("Overlay Stretch", 512+4, 2, 512, 512);
		System.out.println("Init complete...") ;
		return this ;
	}
	
	public static void main( String[] args ) throws Exception {
		new Main().init().start() ;
	}

	public void displayOn( Canvas canvas ) {

		// Find closest node to the mouse pointer
		XY mouse = canvas.sMouse();
		Node closest = null ;
		for (Node i : NodeDB.nodes() )
			if (closest == null || false || mouse.distance( i.pos) < mouse.distance( closest.pos))
				closest = i;

		// If mouse pointer is close to a node, then show its routing table
		if( closest != null && mouse.distance( closest.pos ) < 9.0 || false) {
			canvas.gs.setColor( RGB.RED ) ;
			canvas.sFill( new Circle( closest.pos, 20 )) ;			
			closest.rtable.displayOn( canvas ) ;
		}
		
		// Display the nodes
		final Pen pen = new Pen( RGB.BLACK, 1) ;
		pen.useOn( canvas.gs ) ;
		for( Node i : NodeDB.nodes() )
			i.displayOn( canvas ) ;
		
	}
	
	static {
		Globals.set("Gui_NoTitleBars", true);
		Globals.set("Sim_RandomSeed", 0L);
		Globals.set("Net_RandomSeed", 1L);
		
		Globals.set("Net_Jitter", 0.0);
		
		Globals.set("Net_Euclidean_NodeRadius", 10.0);
		Globals.set("Net_Euclidean_CostFactor", 0.0001);		
		Globals.set("Net_Euclidean_DisplayNodeLabels", false);
		Globals.set("Net_Euclidean_MinimumNodeDistance", 50.0);
		
		Globals.set("Traffic_DrawMessageDot", true) ;
		Globals.set("Traffic_DeadPacketHistory", 0.5) ;
		Globals.set("Traffic_DisplayDeadPackets", true ) ;
		Globals.set("Traffic_DisplayDeadPacketsHistory", "time" ) ;
	}
}
