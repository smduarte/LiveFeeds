package sensing.persistence.simsim.speedsense.sumo;

import java.awt.Point;

import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;

import sensing.persistence.simsim.PipelineSimJ;
import sensing.persistence.simsim.map.MapView;
import simsim.core.Globals;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Rectangle;
import simsim.gui.geom.XY;

public class SUMOVehicle {
	protected MapView mapView;
	private LatLon 	gPos;
	private EastNorth pPos;
	private double speed;
	private String laneId;
	private String edgeId;
	private double lastUpdateTs = -1;
	private final boolean isMobileNode;
	private final int sR;
	
	public SUMOVehicle(boolean isMobileNode, int samplingRate) {
		this.isMobileNode = isMobileNode;
		mapView = Globals.get("4sensing_MapView");
		sR = samplingRate;
	}
	
	public boolean isMobileNode() {
		return isMobileNode;
	}
	
	public int getSamplingRate() {
		return sR;
	}
	
	public void update(double lat, double lon, double x, double y, double speed, String laneId) {
		setLatLon(lat,lon);
		setEastNorth(x,y);
		setLane(laneId);
		this.speed = speed;
	}
	
	
	protected void setLatLon(double lat, double lon) {
		if(gPos == null) {
			gPos = new LatLon(lat,lon);
		} else {
			gPos.setLocation(lon, lat);
		}
		lastUpdateTs = Simulation.currentTime();
	}
	
	
	protected void setEastNorth(double east, double north) {
		if(pPos == null) {
			pPos = new EastNorth(east, north);
		} else {
			pPos.setLocation(east, north);
		}	
		lastUpdateTs = Simulation.currentTime();
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
		return (lastUpdateTs != -1 && (Simulation.currentTime() - lastUpdateTs <= sR));
	}
	
	public void displayOn(Canvas c) {
		Point p = mapView.getPoint(pPos);

		RGB color = !isActive() ? RGB.GRAY :
			(speed > 0 ? RGB.GREEN : RGB.RED);
			//(speed > 0.5 * mapModel.getMaxSpeed(edgeId) ? RGB.GREEN : RGB.RED);
		if(isMobileNode) {
			c.sFill(color, new Circle(p.x, p.y, 15));
		} else {
			c.sFill(color, new Rectangle(p.x, p.y, 6.0, 6.0));
		}
	}
 
}
