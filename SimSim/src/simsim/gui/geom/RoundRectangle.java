package simsim.gui.geom;

import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class RoundRectangle extends RoundRectangle2D.Double {

	public RoundRectangle( XY xy,  XY wh, XY rr) {
		super( xy.x - wh.x * 0.5, xy.y - wh.y * 0.5, wh.x, wh.y, rr.x, rr.y ) ;
	}
	
	public RoundRectangle( XY xy, double w, double h, double rw, double rh) {
		super( xy.x - w * 0.5, xy.y - h * 0.5, w, h, rw, rh ) ;
	}
	
	public RoundRectangle( double x, double y, double w, double h, double rw, double rh) {
		super( x - w * 0.5, y - h * 0.5, w, h, rw, rh ) ;
	}
	
	public RoundRectangle( Point2D xy, double w, double h, double rw, double rh ) {
		super( xy.getX(), xy.getY(), w, h, rw, rh) ;
	}
	
	public RoundRectangle( Point2D xy, Point2D wh, double rw, double rh ) {
		super( xy.getX(), xy.getY(), wh.getX(), wh.getY(), rw, rh ) ;
	}

	public XY pos() {
		return new XY(x, y) ;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
