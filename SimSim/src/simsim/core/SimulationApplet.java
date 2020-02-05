package simsim.core;

import simsim.gui.GuiApplet;
import simsim.utils.Threading;


abstract public class SimulationApplet extends GuiApplet {

	private Simulation sim ;
	
	abstract protected Simulation getSimulation() ;
	
	public void init() {
		 try {
		        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
		            public void run() {		
		            	SimulationApplet.super.init();
		            	sim = getSimulation() ;
		            }
		        });
		    } catch (Exception e) {
		        System.err.println("createGUI didn't successfully complete");
		    }
		 Threading.newThread( new Runnable() {
			 public void run() {
				 sim.start() ;
			 }
		 }, false).start() ;
	}
	
	public void start() {
		Thread.dumpStack();
	}
	
	public void stop() {
		Thread.dumpStack();
	}
	
	private static final long serialVersionUID = 1L;
}
