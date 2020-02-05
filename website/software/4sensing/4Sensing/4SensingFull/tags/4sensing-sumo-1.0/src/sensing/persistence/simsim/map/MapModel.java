package sensing.persistence.simsim.map;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.projection.Projection;


public abstract class MapModel {
	
	protected Bounds bounds;
	protected Projection proj;
	
	public abstract  MapView newMapView(double width, double height);
	
	public MapView newMapView(double width, double height, MapRenderer mapRenderer) {
		MapView v = new MapView(this, proj, width, height, mapRenderer);
		return v;		
	}

	public abstract boolean load(String fileName);
	
	public Bounds getBounds() {
		return bounds;
	}
	
	public Projection getProjection() {
		return proj;
	}
	
	public boolean isInLand(double lat, double lon) {
		return true;		
	}
	
	public abstract double getMaxSpeed(String segmentId);
	public abstract double getAvgSpeed(String segmentId);

	public abstract Collection<String> getClosestSegment(EastNorth en);
	public abstract Rectangle2D getSegmentExtent(String segmentId);
	
	public abstract  ProjectionBounds getProjectionBounds();

	/* 	Destination LatLon given origin, distance and bearing. 
	 * 	0 bearing = heading north
	 *  based on http://www.movable-type.co.uk/scripts/latlong.html
	 */
	private static final int R = 6371000;
	
	public static LatLon destinationPoint(LatLon origin, double bearing, double d) {
		double lat1 = origin.lat()*Math.PI/180;
		double lon1 = origin.lon()*Math.PI/180;
		double brng = bearing*Math.PI/180;
		double lat2 = Math.asin( Math.sin(lat1)*Math.cos(d/R) + Math.cos(lat1)*Math.sin(d/R)*Math.cos(brng));
		double lon2 = lon1 + Math.atan2(Math.sin(brng)*Math.sin(d/R)*Math.cos(lat1), Math.cos(d/R)-Math.sin(lat1)*Math.sin(lat2));
		lon2 = (lon2+Math.PI)%(2*Math.PI) - Math.PI;
		if(lat2 == Double.NaN || lon2 == Double.NaN) return null;
		return new LatLon(lat2*180/Math.PI, lon2*180/Math.PI);
	}
	
	public double[] metersToDegrees(double distance) {
		double[] degrees = new double[2];
		LatLon origin = bounds.getMin();
		LatLon destNorth = destinationPoint(origin, 0, distance);
		LatLon destEast = destinationPoint(origin, 90, distance);
		degrees[0] = destNorth.lat() - origin.lat();
		degrees[1] = destEast.lon() - origin.lon();
		return degrees;
	} 
	
}
