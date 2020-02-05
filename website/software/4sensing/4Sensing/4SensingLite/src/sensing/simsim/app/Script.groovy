package sensing.simsim.app;

import sensing.core.query.Query;
import simsim.gui.geom.*;
import java.awt.geom.*;


//sim.stopActiveQuery();
//sim.reloadPipelines();
//Query q = new Query("sensing.persistence.simsim.speedsense.cell.pipeline.continuous.TrafficStatusQuad").area(minX: 200, minY:200, maxX: 500, maxY: 500);	
//sim.setupQuery(q);
//sim.nodes.each{node->node.toggleDebugging()};

sim.display.messages = false;
sim.display.nodes = false;
sim.display.mobile = true;
sim.display.map = true;
sim.display.gps = false;
sim.display.query = false;
sim.display.queryResult = true;
sim.display.grid = false;
sim.display.tree = false;
sim.display.segmentExtent = true;
sim.display.state = true;
sim.display.stateFilter = ["global"];
sim.display.workLoad = false;
sim.display.queryError = false; //smd


/*
display.grid = 0;
*/


//sim.selectSegment("37119762_1_F");
//sim.onMouseClick = { sim.center(it); sim.zoom(0.5)};
sim.zoomReset();
sim.onMouseClick = { 
	//sim.center(it); sim.zoom(0.7);
	//println "Closest: ${it.fixedLatLon}";
	//	it.toggleDebugging()
};
//sim.stop();
if(sim.root) sim.root.debugNode = true;

sim.eachNode{it.debugNode = true}
//if(sim.root) sim.root.toggleDebugging()
//sim.nodeList[71].toggleDebugging();
