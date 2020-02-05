package simsim.gui.geom;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Random;


/**
 * A class used to represent a point in 2D Cartesian space.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class XY implements Serializable {
	public double x, y ;

	
	public XY( Random rg ) {
		x = rg.nextDouble() ; y = rg.nextDouble() ;
	}
	public XY( double x, double y ) {
		this.x = x ; this.y = y ;
	}
	
	public XY scale( double s ) {
		x *=s ; y *=s ;
		return this ;
	}
	
	public double distance( XY other ) {
		double dx = x - other.x ; double dy = y - other.y ;
		return Math.sqrt( dx * dx + dy * dy );
	}
	
	public double distanceSq( XY other ) {
		double dx = x - other.x ; double dy = y - other.y ;
		return dx * dx + dy * dy;
	}
	
	public double normalize() {
		double m = Math.sqrt( x*x + y*y ) ;
		if( m != 0 ) {
			x /= m ; y /= m ;
		}
		return m ;
	}
	public int X() {
		return (int)x ;
	}

	public int Y() {
		return (int)y ;	
	}
	
	public float fX() {
		return (float)x ;
	}

	public float fY() {
		return (float)y ;	
	}
	
	public String toString() {
		return String.format("(%1.3f, %1.3f)", x, y ) ;
	}

	public double getX() {
		return x;
	}

	public XY add( double ox, double oy ) {
		return new XY( x + ox, y + oy ) ;
	}

	public XY add( XY other ) {
		return new XY( x + other.x, y + other.y ) ;
	}

	public XY sub( XY other ) {
		return new XY( x - other.x, y - other.y ) ;
	}

	public XY mult( double v ) {
		return new XY( x * v, y * v) ;
	}

	public XY div( double v ) {
		return new XY( x/v , y/v );
	}

	public double getY() {
		return y;
	}

	public void setLocation(double x, double y) {
		this.x = x ; this.y = y ;
	}
	
	public Point2D point2D() {
		return new Point2D.Double(x,y) ;
	}
	
	static public XY interpolate( double t, XY src, XY dst ) {
        return new XY( src.x + t * ( dst.x - src.x ), src.y +  t * ( dst.y - src.y ) ) ;
    }
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}