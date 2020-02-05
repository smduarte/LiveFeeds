package meeds.sys.util;

import java.io.*;
import java.util.* ;


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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}