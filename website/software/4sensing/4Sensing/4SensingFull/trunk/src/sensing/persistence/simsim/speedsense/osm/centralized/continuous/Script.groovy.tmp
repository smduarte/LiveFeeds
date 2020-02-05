package sensing.persistence.simsim.speedsense.osm.centralized.continuous
;
import sensing.persistence.core.query.Query;
import simsim.gui.geom.*;
import java.awt.geom.*;


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

//sim.selectSegment("37119762_1_F");
//sim.onMouseClick = { sim.center(it); sim.zoom(0.5)};
sim.zoomReset();
sim.onMouseClick = { 
	//sim.center(it); sim.zoom(0.7);
	//println "Closest: ${it.fixedLatLon}";
		it.toggleDebugging()
};
//sim.stop();
if(sim.root) sim.root.debugNode = true;

sim.eachNode{it.debugNode = true}
//if(sim.root) sim.root.toggleDebugging()
//sim.nodeList[71].toggleDebugging();
