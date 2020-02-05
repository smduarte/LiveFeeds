package sensing.persistence.simsim.speedsense.osm.qtree.continuous;
import sensing.persistence.core.query.Query;
import sensing.persistence.core.query.quadtree.QTQueryService;
import simsim.gui.geom.*;
import java.awt.geom.*;
import org.openstreetmap.josm.data.coor.LatLon;

//sim.stopActiveQuery();
//sim.reloadPipelines();
//Query q = new Query("sensing.persistence.simsim.speedsense.cell.pipeline.continuous.TrafficStatusQuad").area(minX: 200, minY:200, maxX: 500, maxY: 500);	
//sim.setupQuery(q);
//sim.nodes.each{node->node.toggleDebugging()};

sim.display.messages = true;
sim.display.nodes = true;
sim.display.mobile = true;
sim.display.map = true;	
sim.display.gps = true;
sim.display.query = true;
sim.display.queryResult = true;
sim.display.grid = true;
sim.display.tree = true;
sim.display.segmentExtent = true;
sim.display.state = true;
sim.display.stateFilter = ["global"];

//sim.selectSegment("22278076_0_F");	
//sim.onMouseClick = { sim.center(it); sim.zoom(0.5)};
sim.zoomReset();
sim.onMouseClick = { closest, pos ->
	sim.center(pos); 
	sim.zoom(2);
	//println "Closest: ${it.fixedLatLon}";
	//it.toggleDebugging()
};
//sim.stop();

sim.eachNode{it.debugNode = true}
if(sim.root) {
	print "TREE STATS: "
	QTQueryService.getBranchStat().each{ entry ->
		print "${entry.key}: ${entry.value} ";
	}
	println "";
}
//def minDist = sim.nodeList[0].fixedLatLon.greatCircleDistance(sim.nodeList[1].fixedLatLon);
//def maxDist = 0;
//def totalDist = 0;
//def numDist = 0;
//
//sim.eachNode{n1->
//  println "node ${n1.nodeId}";
//	  def n2;
//	  def dist = 999999;
//	  sim.eachNode{
//		if(it != n1) {
//		  def tmpDist = it.fixedLatLon.greatCircleDistance(n1.fixedLatLon);
//		  if(tmpDist < dist) {
//		    n2 = it;
//		    dist = tmpDist; 
//		  }	
//		}
//	  }
//	  minDist = dist < minDist ? dist : minDist;
//	  maxDist = dist > maxDist ? dist : maxDist;
//	  totalDist += dist;
//          numDist++;
//}
//println "MIN DIST: ${minDist}";
//println "MAX DIST: ${maxDist}";
//println "AVG DIST: ${totalDist/numDist}";

//def height = sim.minLatLon.greatCircleDistance(new LatLon(sim.maxLatLon.lat(), sim.minLatLon.lon()))
//def width = sim.minLatLon.greatCircleDistance(new LatLon(sim.minLatLon.lat(), sim.maxLatLon.lon()))
//
//println "width: ${width} height: ${height}"
//
//println sim.setup.activeSegments.size()
