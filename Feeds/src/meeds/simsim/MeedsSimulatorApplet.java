package meeds.simsim;

import simsim.core.Simulation;
import simsim.core.SimulationApplet;

public class MeedsSimulatorApplet extends SimulationApplet {
	
	protected Simulation getSimulation() {
			return new MeedsSimulator().init() ;
	}
	
	private static final long serialVersionUID = 1L;
}
