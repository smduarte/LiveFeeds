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
sim.display.state = false;
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
if(sim.root) sim.root.debugNode = false;

sim.eachNode{it.debugNode = false}
//if(sim.root) sim.root.toggleDebugging()
//sim.nodeList[71].toggleDebugging();
