package meeds.simsim.osm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;


public class OsmWay {
	public final Way way;
	public final List<OsmNode> nodes;

	public final double length;

	public final String type;
	public final double maxSpeed;

	public final String name;
	
	public OsmWay(Way w, List<OsmNode> nl) {
		way = w;
		nodes = nl;
		name = name();
		type = type();
		length = length();
		maxSpeed = maxSpeed();
		
	}

	private double maxSpeed() {
		for (Tag i : way.getTags())
			if (i.getKey().equals("maxspeed")) 
				try {
					return Double.parseDouble(i.getValue());
				} catch( Exception x ){}
		Double val = defaultMaxSpeed.get(type) ;
		return val == null ? 100.0 : val ;
	}

	private String type() {
		for (Tag i : way.getTags())
			if (i.getKey().equals("highway"))
				return i.getKey() + "." + i.getValue();
		return "???";
	}

	private String name() {
		for (Tag i : way.getTags())
			if (i.getKey().equals("name"))
				return i.getKey() + "." + i.getValue();
		return "way-" + way.getId();
	}
	
	private double length() {
		double l = 0.0;
		OsmNode n0 = nodes.get(0);
		for (OsmNode i : nodes) {
			l += distance(n0.pos, i.pos);
			n0 = i;
		}
		return l;
	}

	private double distance(GeoPosition a, GeoPosition b) {
		final double R = 6371 * 1000;
		double x = (lon(b) - lon(a)) * Math.cos(0.5 * (lat(a) + lat(b)));
		double y = (lat(b) - lat(a));
		return (int) (Math.sqrt(x * x + y * y) * R + 0.5);
	}

	private double lat(GeoPosition p) {
		return p.getLatitude() * Math.PI / 180.0;
	}

	private double lon(GeoPosition p) {
		return p.getLongitude() * Math.PI / 180.0;
	}

	static Map<String,Double> defaultMaxSpeed = new HashMap<String,Double>() ;
	static {
		defaultMaxSpeed.put("highway.trunk", 120.0) ;
		defaultMaxSpeed.put("highway.motorway", 120.0) ;
		defaultMaxSpeed.put("highway.motorway_link", 120.0) ;
		defaultMaxSpeed.put("highway.primary", 50.0) ;
		defaultMaxSpeed.put("highway.primary_link", 50.0) ;
		defaultMaxSpeed.put("highway.secondary", 50.0) ;
		defaultMaxSpeed.put("highway.secondary_link", 50.0) ;
		defaultMaxSpeed.put("highway.tertiary", 50.0) ;
		defaultMaxSpeed.put("highway.tertiary_link", 50.0) ;
		defaultMaxSpeed.put("highway.residential", 30.0) ;
		defaultMaxSpeed.put("highway.living_street", 30.0) ;
		defaultMaxSpeed.put("highway.unknown", -30.0) ;
		defaultMaxSpeed.put("highway.unclassified", 50.0) ;
	}
}
