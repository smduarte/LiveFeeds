package simsim.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;

import simsim.utils.Threading;


/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 */
@SuppressWarnings("serial")
public class GuiFrame extends javax.swing.JFrame {

	private GuiDesktop gui ;
	
	public Gui gui() {
		return gui ;
	}
	
	public GuiFrame() {
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

		getContentPane().add( gui = new GuiDesktop( this ) );

		setVisible(true);
		setTitle("SimSimulator");		
		pack() ;
		
		setVisible(true);
		this.setPreferredSize( gui.getPreferredSize() );

		while (!this.isShowing())
			Threading.sleep(500);
		
	}

	public void setSize( Dimension d ) {
		super.setSize( (int)d.getWidth(), (int)d.getHeight() + getInsets().top + 3) ;		
	}
}
