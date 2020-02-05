package simsim.gui;

import simsim.core.*;
import simsim.gui.geom.XY;

public class NoGUI implements Gui {

	public void init() {		
	}
	
	public void redraw() {	
	}
	
	public void maximizeFrame(String frame) {
	}
	
	public void addDisplayable(String frame, Displayable d, double fps) {
	}

	public void addDisplayable(String frame, Displayable d, double fps, boolean titlebar) {
	}	

	public void setFrameRectangle(String frame, int x, int y, int w, int h) {
	}

	public void setFrameTransform(String frame, double virtualWidth, double virtualHeight, double offset, boolean keepRatios) {
	}

	public void addInputHandler(String frame, InputHandler ih) {
	}

	public XY getMouseXY() {
		return new XY(0,0);
	}

	public void setDesktopSize(int width, int height) {
	}

	public void setDesktopSize(String title, int width, int height) {
	}
}
