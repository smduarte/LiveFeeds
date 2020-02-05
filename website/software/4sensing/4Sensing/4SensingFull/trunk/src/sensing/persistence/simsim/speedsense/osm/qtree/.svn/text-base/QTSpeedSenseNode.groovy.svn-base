package sensing.persistence.simsim.speedsense.osm.qtree;
import static sensing.persistence.simsim.PipelineSimulation.display;
import sensing.persistence.simsim.speedsense.SpeedSenseNode;
import sensing.persistence.simsim.speedsense.map.*;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.core.vtable.VTableDefinition;

import simsim.gui.canvas.*;
import simsim.gui.geom.*;

public class QTSpeedSenseNode extends SpeedSenseNode {

	static Pen saPen = new Pen(RGB.BLUE, 2, 5);
	static Pen treePen = new Pen(RGB.RED);
	static Pen leafTreePen = new Pen(RGB.RED, 1, 5);
	static Pen quadPen = new Pen(RGB.ORANGE, 4, 10);
	
	
	public void displayOn(Canvas c) {
		super.displayOn(c);	
		if(display.tree) {
			println "display.tree"
			def queryInstances = services.query.getQueryInstances();
			if(queryInstances) {
				println "has query instances"
				Map qi = queryInstances[0];
	
				List pens = [treePen, leafTreePen ];
				[qi.contexts.global, qi.contexts[VTableDefinition.DATASRC]].eachWithIndex{qCtx,i->
					if(qCtx && qCtx.parent) {
						c.sDraw(pens[i], new Line(pos.x, pos.y, qCtx.parent.pos.x, qCtx.parent.pos.y));
					}
				}
	
				if(qi.contexts.global) {
					c.sDraw(treePen, "${qi.contexts.global.level}", pos.x+10, pos.y+10);	
				}
	//			// if is root
				if(qi.contexts.global && !qi.contexts.global.parent) {
					//canvas.sDraw(queryPen, qi.global.query.aoi);
					c.sDraw(saPen, OSMSpeedSenseSim.mapView.latLonBoundsToScreen(qi.contexts.global.query.searchArea));
					qi.contexts.global.quadIntercept.each{
						if(!it.isEmpty()) {
							c.sDraw(quadPen, OSMSpeedSenseSim.mapView.latLonBoundsToScreen(it));	
						} 
					}
				}
					
			}
		}
	}
	
}
