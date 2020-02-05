package simsim.gui.geom;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Circle extends Ellipse2D.Double implements Shape {

	public Circle( XY center, double diameter ) {
		this( center.x, center.y, diameter ) ;
	}

	public Circle( double cx, double cy, double diameter ) {
		super( cx - diameter * 0.5, cy - diameter * 0.5, diameter, diameter) ;
	}


	public Circle( Point2D xy, double diameter ) {
		this( xy.getX(), xy.getY(), diameter ) ;
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
