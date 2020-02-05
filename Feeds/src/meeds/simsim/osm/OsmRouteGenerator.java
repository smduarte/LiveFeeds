package meeds.simsim.osm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import rlc.Point;
import rlc.RLC;
import simsim.graphs.Dijkstra;
import simsim.graphs.Graph;
import simsim.graphs.ShortestPathsTree;
import simsim.utils.RandomList;

public class OsmRouteGenerator {

	RLC rlc;
	RandomList<OsmSegment> allSegments, wayPoints;

	GeoPos mapCenter = new GeoPos(38.738711, -9.139981);
	
	OsmRouteGenerator(RLC rlc, RandomList<OsmSegment> allSegments) {
		this.rlc = rlc;
		this.allSegments = allSegments;
		
		wayPoints = new RandomList<OsmSegment>();
		for (Point i : rlc.rangeSearch(mapCenter, 0.015, new long[1]))
			wayPoints.add((OsmSegment) i);
	}

	void updateRoute(final OsmRoute r) {

		if (r.path == null) {
			r.lastPos = allSegments.randomElement().v;
		} else
			r.lastPos = r.endPosition();
		
		List<OsmSegment> segments = new ArrayList<OsmSegment>();
		for (Point i : rlc.rangeSearch(r.lastPos, 0.01, new long[1]))
			segments.add((OsmSegment) i);

		
		Set<GeoPos> nodeSet = new HashSet<GeoPos>();
		for (OsmSegment i : segments) {
			nodeSet.add(i.v);
			nodeSet.add(i.w);
		}

		if (!nodeSet.contains(r.lastPos)) {
			r.path = null;
			return;
		}

		Graph<GeoPos> g = new Graph<GeoPos>(nodeSet, segments);
		ShortestPathsTree<GeoPos> spt = new ShortestPathsTree<GeoPos>(r.lastPos, g) ;
		
		r.path = spt.pathTo( spt.leafSet().randomElement() ) ;
	}
}
