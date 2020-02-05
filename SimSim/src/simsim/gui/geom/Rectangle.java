package simsim.gui.geom;

import java.awt.geom.Point2D;

public class Rectangle extends Rectangle2D.Double {

	public Rectangle( XY xy,  XY wh) {
		super( xy.x - wh.x * 0.5, xy.y - wh.y * 0.5, wh.x, wh.y ) ;
	}
	
	public Rectangle( XY xy, double w, double h) {
		super( xy.x - w * 0.5, xy.y - h * 0.5, w, h ) ;
	}
	
	public Rectangle( double x, double y, double w, double h) {
		super( x - w * 0.5, y - h * 0.5, w, h ) ;
	}
	
	public Rectangle( Point2D xy, double w, double h ) {
		super( xy.getX(), xy.getY(), w, h) ;
	}
	
	public Rectangle( Point2D xy, Point2D wh ) {
		super( xy.getX(), xy.getY(), wh.getX(), wh.getY() ) ;
	}

	public XY pos() {
		return new XY(x, y) ;
	}
	
	public XY center() {
		return new XY(x + width * 0.5, y + height * 0.5) ;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
