package example.chord;

import simsim.core.Globals;
import simsim.core.Simulation;
import simsim.core.SimulationApplet;

public class MainApplet extends SimulationApplet {
	
	protected Simulation getSimulation() {
			return new Main().init() ;
	}
	
	private static final long serialVersionUID = 1L;
	
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
