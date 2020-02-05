package sensing.simsim.app.osm;

import java.awt.Point
import java.awt.geom.*
import java.awt.geom.Rectangle2D

import sensing.core.network.*
import sensing.core.query.Query
import sensing.simsim.*
import sensing.simsim.speedsense.map.*
import sensing.simsim.speedsense.map.setup.*
import sensing.simsim.sys.MobileNode
import sensing.simsim.sys.Sim4Sensing
import sensing.simsim.sys.map.MapModel
import sensing.simsim.sys.map.osm.OSMMapModel
import simsim.core.*
import simsim.gui.canvas.Canvas
import simsim.gui.canvas.Pen
import simsim.gui.geom.*


public abstract class OSMSpeedSenseSim extends Sim4Sensing implements Displayable {

	static TrafficSpeedModel speedSenseModel;


	protected OSMSpeedSenseSim() {
		super();
		speedSenseModel = new TrafficSpeedModel();

	}

	protected void init() {
		super.init();
		
		setup.TOTAL_MNODES.times {
			if(it%100 == 0) println "initializing mobile node [${it}]"
			MobileNode mnode = new OSMMobileNode();
			registerMobileNode(mnode);
			mnode.init();
			mNodeDB.put(mnode.id.toString(), mnode);
		}
	}
	
	protected MapModel createMapModel() {
		MapModel m = new OSMMapModel();
		try {
			
			System.out.println("Loading map...");
			m.load ("map6.osm");
			System.out.println("Done...");			
		} catch( Exception x ) {
			x.printStackTrace() ;
			System.exit(-1) ;
		}
		return m;
	}

	protected void update() {
		super.update();
		eachNode{ node -> 
			node.mNodes*.updatePosition()
		}
//			mNodeDB.updatePosition();
		speedSenseModel.update();		
	}


}
