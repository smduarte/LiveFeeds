package simsim.gui;

import javax.swing.BoxLayout;

import simsim.core.Simulation;


/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 */
@SuppressWarnings("serial")
public class GuiApplet extends javax.swing.JApplet {

	private GuiDesktop gui ;
	
	public Gui gui() {
		return gui ;
	}

	public void init() {
		getContentPane().setLayout(new BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));
		getContentPane().add( gui = new GuiDesktop( this ) );		
		Simulation.Gui = gui ; 
	}		
}
