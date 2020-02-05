package meeds.simsim.osm;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;

public class OsmNode {
	final Node node ;
	public final GeoPos pos ;
	
	public OsmNode( Node n ) {
		node = n ;
		pos = coords(n) ;
	}
	
	
	static GeoPos coords( Node n ) {
		GeoPos ll = coords.get( n.getId() ) ;
			if( ll == null ) {
				coords.put( n.getId(), ll = new GeoPos( n.getLatitude(), n.getLongitude() ) ) ;
			}
		return ll ;
	}
	
	static Map<Long, GeoPos> coords = new HashMap<Long,GeoPos>() ;
}
