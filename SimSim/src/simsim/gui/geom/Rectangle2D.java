package simsim.gui.geom;

import java.awt.geom.Point2D;

public class Rectangle2D extends java.awt.geom.Rectangle2D.Double {

	public Rectangle2D( XY xy,  XY wh) {
		super( xy.x, xy.y, wh.x, wh.y ) ;
	}
	
	public Rectangle2D( XY xy, double w, double h) {
		super( xy.x, xy.y, w, h ) ;
	}
	
	public Rectangle2D( double x, double y, double w, double h) {
		super( x, y, w, h ) ;
	}
	
	public Rectangle2D( Point2D xy, double w, double h ) {
		super( xy.getX(), xy.getY(), w, h) ;
	}
	
	public Rectangle2D( Point2D xy, Point2D wh ) {
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
