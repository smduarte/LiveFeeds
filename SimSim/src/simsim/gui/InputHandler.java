package simsim.gui;

import simsim.gui.geom.XY;

public interface InputHandler {

	public void onMouseMove( XY pu, XY ps ) ;
	
	public void onMouseClick( int button, XY pu, XY ps ) ;

	public void onMouseDragged( int button, XY pu, XY ps ) ;

	public void mouseWheelMoved( XY pu, XY ps, double units ) ;
}
