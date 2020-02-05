package simsim.gui.geom;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Line extends Line2D.Double {

	public Line( XY a, XY b ) {
		super( a.x, a.y, b.x, b.y) ;
	}
	
	public Line( double x1, double y1, double x2, double y2) {
		super( x1, y1, x2, y2) ;
	}

	public Line( Point2D a, Point2D b ) {
		super( a.getX(), a.getY(), b.getX(), b.getY() ) ;
	}
	
	public XY interpolate( double t ) {
		t = Math.max(0, Math.min( 1, t));
		return new XY( t * x1 + (1-t) * x2, t * y1 + (1-t) * y2 ) ;
	}
	
	public XY extrapolate( double t ) {
		return new XY( t * x1 + (1-t) * x2, t * y1 + (1-t) * y2 ) ;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
