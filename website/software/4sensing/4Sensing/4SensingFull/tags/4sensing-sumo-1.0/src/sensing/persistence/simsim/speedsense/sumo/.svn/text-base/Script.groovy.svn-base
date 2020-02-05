package sensing.persistence.simsim.speedsense.sumo;

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

//sim.zoomReset();
sim.onMouseClick = { closest, pos ->
		sim.center(pos); 
		sim.zoom(0.5);

//	def seg = sim.getClosestSegment(pos)
//	if(seg) { 
//		sim.selectSegment(seg)
//		sim.setup.selectSegment(seg)
//		println "SEG>$seg"
//	}
}


// transito lento
//def selected = "26030733"

// fluxo livre com sinal
//def selected = "5170429#4"


//sim.selectSegment("-47007039#6")
//sim.setup.selectSegment("-47007039#6")

// Seg com erro elevado
//sim.selectSegment("23562784")
//sim.setup.selectSegment("23562784")

// Teste
//def selected = "47426157#0"
def selected = "6187381#3"
sim.selectSegment(selected)
sim.setup.selectSegment(selected)


sim.zoom(sim.mapModel.getEdge(sim.selectedSegments[0]).bbox);
sim.zoom(0.5);

//sim.onRunQuery = {q -> sim.zoom(q.aoi)
//	
//}

println "SELECTED: ${sim.selectedSegments[0]}: ${sim.mapModel.getEdge(sim.selectedSegments[0]).length}"
//sim.eachNode{it.debugNode = false}
