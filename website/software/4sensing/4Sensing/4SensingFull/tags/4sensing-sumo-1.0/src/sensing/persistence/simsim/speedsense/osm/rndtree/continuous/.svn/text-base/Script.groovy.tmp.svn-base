package sensing.persistence.simsim.speedsense.osm.rndtree.continuous;
import sensing.persistence.core.query.Query;
import simsim.gui.geom.*;
import java.awt.geom.*;


//sim.stopActiveQuery();
//sim.reloadPipelines();

//Query q = new Query("sensing.persistence.simsim.speedsense.cell.pipeline.continuous.TrafficStatusQuad").area(minX: 200, minY:200, maxX: 500, maxY: 500);	

//sim.setupQuery(q);
//sim.nodes.each{node->node.toggleDebugging()};
//sim.randomQuery("sensing.persistence.simsim.speedsense.map.pipeline.continuous.TrafficStatus");


sim.display.messages = false;
sim.display.nodes = false;
sim.display.mobile = false;
sim.display.map = false;
sim.display.gps = false;
sim.display.query = false;
sim.display.queryResult = false;
sim.display.grid = false;
sim.display.tree = false;
sim.display.segmentExtent = false;

sim.onMouseClick = { closest, pos ->
	sim.center(pos); 
	sim.zoom(2);
	//println "Closest: ${it.fixedLatLon}";
	//it.toggleDebugging()
};
sim.zoomReset();
//sim.stop();
//sim.randomNode().toggleDebugging();

sim.eachNode{
	it.debugNode = false
}

//sim.eachNode{if(it.nodeId == 240) it.debugNode = false}
