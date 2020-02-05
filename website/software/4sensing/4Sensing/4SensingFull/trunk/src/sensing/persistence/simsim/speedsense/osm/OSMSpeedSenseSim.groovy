package sensing.persistence.simsim.speedsense.osm;
import sensing.persistence.simsim.map.MapModel;
import sensing.persistence.simsim.map.osm.OSMMapModel;
import sensing.persistence.simsim.speedsense.osm.OSMMobileNode;
import sensing.persistence.simsim.MobileNode;
import sensing.persistence.simsim.speedsense.SpeedSenseNode;
import sensing.persistence.simsim.speedsense.SpeedSenseSim;
import sensing.persistence.simsim.speedsense.map.*;
import sensing.persistence.core.ServicesConfig;
import sensing.persistence.core.network.*;
import sensing.persistence.core.query.Query;
import sensing.persistence.core.monitoring.MonitoringService;
import sensing.persistence.core.sensors.GPSReading;
import sensing.persistence.simsim.*;
import sensing.persistence.simsim.speedsense.map.setup.*;
import simsim.core.*;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.*;
import simsim.gui.InputHandler;
import speedsense.Hotspot;

import java.awt.Point;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Image;
import javax.imageio.ImageIO;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.coor.CachedLatLon;


public abstract class OSMSpeedSenseSim extends SpeedSenseSim implements Displayable {

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
		m.load ("map6.osm");
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
