package simsim.core;

import simsim.gui.canvas.Canvas;

/**
 * Interface used when the implementing object needs to be drawn in a simulator window.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public interface Displayable {

	/**
	 * Draws the implementing object in a simulator internal frame/window.
	 * @param c The Canvas graphics context on which to draw the element
	 */
	public void displayOn( Canvas c ) ;
	 
}
