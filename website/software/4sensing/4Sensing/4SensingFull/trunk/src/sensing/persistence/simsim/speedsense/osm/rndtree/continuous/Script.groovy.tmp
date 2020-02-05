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
	it.debugNode = true
}

//sim.eachNode{if(it.nodeId == 240) it.debugNode = true}
