package meeds.simsim.osm;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import rlc.Point;
import simsim.gui.geom.XY;

public class GeoPos extends GeoPosition implements Point, Comparable<GeoPos> {

//	public GeoPos( XY xy ) {
//		super( xy.y, xy.x) ;
//	}

	static final double P = 1000000.0 ;
	
	public GeoPos( double lat, double lon ) {
		super(lat, lon) ;
	}
	
	public GeoPos( GeoPosition other ) {
		super( other.getLatitude(), other.getLongitude() ) ;
	}

	public double distance(GeoPos other) {
		double a = super.getLatitude() - other.getLatitude() ;
		double b = super.getLongitude() - other.getLongitude() ;
		return Math.sqrt(a*a + b*b) ;
	}
	
	public double distance(Point point) {
		try {
			GeoPos other = (GeoPos) point;
			double a = super.getLatitude() - other.getLatitude() ;
			double b = super.getLongitude() - other.getLongitude() ;
			return Math.sqrt(a*a + b*b) ;
		} catch( ClassCastException cce ) {
			OsmSegment other = (OsmSegment) point;
			double a = super.getLatitude() - 0.5*(other.v.getLatitude() + other.w.getLatitude());
			double b = super.getLongitude() - 0.5*(other.v.getLongitude() + other.w.getLongitude());
			return Math.sqrt(a*a + b*b) ;
		}
	}

	public int compareTo(GeoPos other) {
		if( getLatitude() != other.getLatitude() ) return getLatitude() < other.getLatitude() ? -1 : 1 ;
		else
			if( getLongitude() != other.getLongitude() ) return getLongitude() < other.getLongitude() ? -1 : 1 ;
			else return 0 ;
	}
	
	public boolean equals( GeoPos other ) {
		return other != null && getLatitude() == other.getLatitude() && getLongitude() == other.getLongitude() ;
	}
	
	public boolean equals( Object other ) {
		return equals( (GeoPos) other ) ;
	}	
	
	public int hashCode() {
		return (int)(getLatitude() * 10000) ^ (int)(getLongitude() * 10000) ;  
	}
	
	static public GeoPos interpolate( double t, GeoPos src, GeoPos dst ) {
        return new GeoPos( src.getLatitude() + t * ( dst.getLatitude() - src.getLatitude() ), src.getLongitude() +  t * ( dst.getLongitude() - src.getLongitude() ) ) ;
    }
}
