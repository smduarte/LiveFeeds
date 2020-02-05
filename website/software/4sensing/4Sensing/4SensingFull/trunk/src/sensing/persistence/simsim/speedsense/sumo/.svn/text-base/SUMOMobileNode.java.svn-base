package sensing.persistence.simsim.speedsense.sumo;

import java.awt.Point;

import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import sensing.persistence.simsim.MobileNode;
import simsim.core.Globals;
import simsim.core.Simulation;

import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Rectangle;
import simsim.gui.geom.XY;

import sensing.persistence.simsim.PipelineSimJ;
import sensing.persistence.simsim.map.MapView;
import sensing.persistence.simsim.map.sumo.SUMOMapModel;

public class SUMOMobileNode extends MobileNode {
	protected PipelineSimJ simJ;
	protected SUMOMapModel mapModel;
	protected MapView mapView;
	protected LatLon 	gPos;
	protected EastNorth pPos;
	protected double pos; // position on segment - meters
	protected double speed;
	protected String laneId;
	protected String edgeId;
	protected String selectedSegmentId;
	protected double lastUpdateTs = -1;
	protected final String vehicleId;
	protected final int sP;
	protected final int uP;

	public SUMOMobileNode(String vehicleId) {
		simJ = Globals.get("4sensingJ");
		mapModel = Globals.get("4sensing_MapModel");
		mapView = Globals.get("4sensing_MapView");
		this.vehicleId = vehicleId;
		sP = simJ.<Integer>getConfig("SAMPLING_PERIOD");
		uP = simJ.<Integer>getConfig("SUMO_VPROBE_SAMPLING_PERIOD");
	}
	
	public void init() {
		selectedSegmentId = simJ.getConfig("selectedSegmentId");
	}
	
	
	public int getSamplingPeriod() {
		return sP;
	}
	
	public void update(double lat, double lon, double x, double y, double speed, String laneId, double pos) {
		setLatLon(lat,lon);
		setEastNorth(x,y);
		setLane(laneId);
		this.speed = speed;
		this.pos = pos;
		lastUpdateTs = Simulation.currentTime();
	}
	
	
	protected void setLatLon(double lat, double lon) {
		if(gPos == null) {
			gPos = new LatLon(lat,lon);
		} else {
			gPos.setLocation(lon, lat);
		}
		
	}
	
	
	protected void setEastNorth(double east, double north) {
		if(pPos == null) {
			pPos = new EastNorth(east, north);
		} else {
			pPos.setLocation(east, north);
		}	
	}
	
	protected void setLane(String laneId) {
		this.laneId = laneId;
		this.edgeId = laneId.substring(0, laneId.indexOf("_"));
	}
	
	public LatLon getLatLon() {
		return gPos;
	}
	
	public EastNorth getEastNorth() {
		return pPos;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	
	public String getLaneId() {
		return laneId;
	}
	
	public String getEdgeId() {
		return edgeId;
	}
	
	
	public XY getPos() {
		Point p = mapView.getPoint(pPos);
		return new XY(p.x, p.y);
	}
	
	public boolean isActive() {
		return (lastUpdateTs == -1 || (Simulation.currentTime() - lastUpdateTs <= uP));
	}
	
	public void displayOn(Canvas c) {
		Point p = mapView.getPoint(pPos);

		RGB color = !isActive() ? RGB.GRAY :
			(speed > 0 ? RGB.GREEN : RGB.RED);
			//(speed > 0.5 * mapModel.getMaxSpeed(edgeId) ? RGB.GREEN : RGB.RED);
		if(homeBase != null) { // is 4Sensing node
			c.sFill(color, new Circle(p.x, p.y, 15));
		} else {
			c.sFill(color, new Rectangle(p.x, p.y, 6.0, 6.0));
		}
	}
	
	
}
