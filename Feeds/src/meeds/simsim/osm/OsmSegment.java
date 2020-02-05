package meeds.simsim.osm;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import rlc.Point;
import simsim.graphs.Link;
import simsim.gui.geom.XY;

public class OsmSegment extends Link<GeoPos> implements Point {

	public OsmWay way;

	public OsmSegment(OsmWay w, GeoPos a, GeoPos b) {
		super( a, b, length(a,b) ) ;
		way = w;
	}
	
	static public float length( GeoPos a, GeoPos b) {
		final double R = 6371 * 1000;
		double x = (lon(b) - lon(a)) * Math.cos(0.5 * (lat(a) + lat(b)));
		double y = (lat(b) - lat(a));
		return (int) (Math.sqrt(x * x + y * y) * R + 0.5);
	}
	
	public double distance(Point point) {
		try {
			GeoPosition other = (GeoPos) point;
			double a = 0.5 * (v.getLatitude() + w.getLatitude()) - other.getLatitude();
			double b = 0.5 * (v.getLongitude() + w.getLongitude()) - other.getLongitude();
			return Math.sqrt(a * a + b * b);
		} catch (ClassCastException cce) {
			OsmSegment other = (OsmSegment) point;
			double a = 0.5 * (v.getLatitude() + w.getLatitude()) - 0.5 * (other.v.getLatitude() + other.w.getLatitude());
			double b = 0.5 * (v.getLongitude() + w.getLongitude()) - 0.5 * (other.v.getLongitude() + other.w.getLongitude());
			return Math.sqrt(a * a + b * b);
		}
	}

	public double minimum_distance(GeoPosition _p) {
		XY vv = new XY(v.getLatitude(), v.getLongitude());
		XY ww = new XY(w.getLatitude(), w.getLongitude());
		XY p = new XY(_p.getLatitude(), _p.getLongitude());

		// Return minimum distance between line segment vw and point p
		final double l2 = vv.distanceSq(ww); // i.e. |w-v|^2 - avoid a sqrt
		if (l2 == 0.0)
			return p.distance(vv); // v == w case
		// Consider the line extending the segment, parameterized as v + t (w -
		// v).
		// We find projection of point p onto the line.
		// It falls where t = [(p-v) . (w-v)] / |w-v|^2
		// final double t = dot(p - v, w - v) / l2;
		double t = ((p.x - vv.x) * (ww.x - vv.x) + (p.y - vv.y) * (ww.y - vv.y)) / l2;

		if (t < 0.0)
			return p.distance(vv); // Beyond the 'v' end of the segment
		else if (t > 1.0)
			return p.distance(ww); // Beyond the 'w' end of the segment
		XY projection = new XY(vv.x + t * (ww.x - vv.x), vv.y + t * (ww.y - vv.y)); 
		return p.distance(projection);
	}
	
	public XY projection( GeoPosition _p ) {
		XY vv = new XY(v.getLatitude(), v.getLongitude());
		XY ww = new XY(w.getLatitude(), w.getLongitude());
		XY p = new XY(_p.getLatitude(), _p.getLongitude());

		// Return minimum distance between line segment vw and point p
		final double l2 = vv.distanceSq(ww); // i.e. |w-v|^2 - avoid a sqrt
		if (l2 == 0.0)
			return p; // v == w case
		// Consider the line extending the segment, parameterized as v + t (w -
		// v).
		// We find projection of point p onto the line.
		// It falls where t = [(p-v) . (w-v)] / |w-v|^2
		// final double t = dot(p - v, w - v) / l2;
		double t = ((p.x - vv.x) * (ww.x - vv.x) + (p.y - vv.y) * (ww.y - vv.y)) / l2;

		XY pp = new XY(vv.x + t * (ww.x - vv.x), vv.y + t * (ww.y - vv.y)); 
		return new XY(pp.y, pp.x) ;
	}
	
	static private double lat(GeoPosition p) {
		return p.getLatitude() * Math.PI / 180.0;
	}

	static private double lon(GeoPosition p) {
		return p.getLongitude() * Math.PI / 180.0;
	}
}
