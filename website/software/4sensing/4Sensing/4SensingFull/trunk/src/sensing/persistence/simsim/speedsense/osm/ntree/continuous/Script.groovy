package sensing.persistence.simsim.speedsense.osm.ntree.continuous;
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
sim.display.queryError = true;
sim.display.grid = true;
sim.display.tree = true;
sim.display.segmentExtent = true;
sim.display.stateFilter = ['speedsense.TrafficSpeed.globalAggregation0','speedsense.TrafficHotspots.globalAggregation0'];
sim.display.grid=true;
sim.display.workLoad=true;

sim.onMouseClick = { closest, pos ->
	//closest.toggleDebugging();
	sim.center(pos); 
	sim.zoom(1.5)
	println "Closest: ${closest.fixedLatLon}"
};
//sim.nodeDetail = {it.services.query.getProcessedTuples()}

sim.eachNode{it.debugNode = true}
//sim.zoomReset();
//sim.stop();
