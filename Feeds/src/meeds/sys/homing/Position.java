package meeds.sys.homing;

import java.io.*;
import java.util.*;

import meeds.sys.util.*; 

public class Position implements Serializable, Comparator<Position> {

	final public XY xy ;
	
	public Position() {
		this( new XY(0,0)) ;
	}
	
	public Position( XY xy ) {
		this.xy = new XY( xy.x, xy.y ) ;
	}

	public Position( double x, double y ) {
		this.xy = new XY( x, y ) ;
	}
	
	public String toString() {
		return String.format("(%4.2f,%4.2f)", xy.x, xy.y ) ;
	}
	
	private static final long serialVersionUID = 1L;

	public int compare(Position a, Position b) {
		double dA = xy.distance( a.xy ) ;
		double dB = xy.distance( b.xy ) ;
		return dA == dB ? 0 : dA < dB ? -1 : 1 ;
	}
}
