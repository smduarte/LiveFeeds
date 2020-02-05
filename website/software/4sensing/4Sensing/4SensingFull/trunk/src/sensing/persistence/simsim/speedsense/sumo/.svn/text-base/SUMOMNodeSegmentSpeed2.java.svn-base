package sensing.persistence.simsim.speedsense.sumo;

//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;

import java.awt.Point;
import java.util.ArrayList;

import groovy.lang.GroovyObject;
import sensing.persistence.simsim.map.sumo.SUMOMapModel;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Rectangle;

public class SUMOMNodeSegmentSpeed2 extends SUMOMobileNode {

	enum State {NORMAL, DELAYED};
	
	final protected int windowSize;
	protected double[] speedWindow; // per segment window
	protected int numReadings; // number of valid readings in speedWindow

	protected State state;
	protected int changeUploadLag;
	protected boolean periodicUpload;
	protected double lastPos;
	protected double edgeEntryTs;
	protected boolean isRouteStart;	
	protected SUMOMapModel.Edge sampledEdge; // edge currently being sampled
	protected String sampledLaneId;

	public SUMOMNodeSegmentSpeed2(String vehicleId) {
		super(vehicleId);
		this.windowSize = simJ.<Integer>getConfig("SUMO_MNODE_WINDOWSIZE");
	}
	
	public void init() {
		super.init();
		if(homeBase != null) {
			sampledEdge = null;
			sampledLaneId = null;
			edgeEntryTs = 0;
			state = State.NORMAL;
			changeUploadLag = 0;
			periodicUpload = false;
			lastPos = 0;
			initWindow();
		}
	}
	
	public void update(double lat, double lon, double x, double y, double speed, String laneId, double pos) {
		super.update(lat, lon, x, y, speed, laneId, pos);	
		if(homeBase != null) {
			if(sampledEdge == null || !isActive()) { // route start or teleported
				edgeEntry();
				isRouteStart = true;
				addReading();
				//if(vehicleId.equals("791731")) System.out.println(vehicleId + "] ROUTE START: " + edgeId);
			} else if(!sampledEdge.id.equals(edgeId)) { // crossed edge
				sendReading();
				edgeEntry();
				isRouteStart = false;
				addReading();
				//if(vehicleId.equals("791731"))  System.out.println(vehicleId + "] CROSSED EDGE: " + edgeId);
			} else { // same edge
				addReading();
				if(periodicUpload == true) { //TODO 12
					//if(vehicleId.equals("791731")) System.out.println(vehicleId + "] PERIODIC UPLOAD Lag:" +  changeUploadLag);
					if(changeUploadLag > 0 && changeUploadLag % 12 == 0) {
						//if(vehicleId.equals("791731"))  System.out.println(vehicleId + "] PERIODIC UPLOAD - SENDING");
						sendReading();
						lastPos = pos;
					} 
					changeUploadLag++;
				}
			}
		}
		
	}
	
	protected void edgeEntry() {
		sampledEdge = mapModel.getEdge(edgeId);
		sampledLaneId = laneId;
		edgeEntryTs = Simulation.currentTime();
		state = State.NORMAL;
		endPeriodicUpload();
		initWindow();
	}
	
	
	protected void initWindow() {
		speedWindow = new double[windowSize/uP];
		numReadings = 0;
	}
	
	protected void addReading() {
		numReadings = (numReadings < speedWindow.length ? ++numReadings : numReadings);
		System.arraycopy(speedWindow, 0, speedWindow, 1, speedWindow.length-1);
		speedWindow[0] = speed;

		double elapsedTT = Simulation.currentTime() - edgeEntryTs;
		double minTT = sampledEdge.length / sampledEdge.maxSpeed;
		
		//double tDistance = getTraveledDistance();
		
		if(state == State.NORMAL && elapsedTT  > minTT + 60) {
			state = State.DELAYED;
			startPeriodicUpload();
		}
		
//		if(vehicleId.equals("791731"))  {
//			System.out.printf("%s] ADD at %s (%.2f) speed:%.2f\n", vehicleId, sampledEdge.id, pos, speed);
//		} 
	}
	
	
	
	protected void startPeriodicUpload() {
		//if(vehicleId.equals("791731"))  System.out.println(vehicleId + "] START PERIODIC UPLOAD was:" + periodicUpload);
		if(periodicUpload == false) {
			periodicUpload = true;
			changeUploadLag = 0;
			lastPos = pos;
		}
	}
	
	protected void endPeriodicUpload() {
		//if(vehicleId.equals("791731"))  System.out.println(vehicleId + "] END PERIODIC UPLOAD was:" + periodicUpload);
		if(periodicUpload == true) {
			periodicUpload = false;
			changeUploadLag = 0;
			lastPos = 0;
		}
	}
	
	protected void sendReading() {
		if(numReadings == 0 ) return;
		
		int aggExtent = 60;
		int maxReadInAgg = aggExtent/uP; //60/uP
		GroovyObject sWins = simJ.newTuple("speedsense.SpeedWindows");
		sWins.setProperty("segmentId", sampledEdge.id);
		sWins.setProperty("boundingBox", sampledEdge.bbox);
		ArrayList<GroovyObject> reads = new ArrayList<GroovyObject>(); 
		sWins.setProperty("sWindows", reads);
		
		double tSpeed = 0;
		int numReadInAgg = 0;
		double timestamp = Simulation.currentTime();
		for(int i=0; i<numReadings; i++) {
			tSpeed += speedWindow[i];
			numReadInAgg++;
			if(numReadInAgg == maxReadInAgg || i == numReadings-1) {
				double avgSpeed = tSpeed/numReadInAgg;
				GroovyObject sWin = simJ.newTuple("speedsense.MappedSpeed");
				sWin.setProperty("time", timestamp);
				sWin.setProperty("segmentId", sampledEdge.id); 
				sWin.setProperty("boundingBox", sampledEdge.bbox);
				sWin.setProperty("speed", avgSpeed);
				sWin.setProperty("sumSpeed", tSpeed);
				sWin.setProperty("count", numReadInAgg);
				reads.add(sWin);
//				if(!sampledEdge.id.equals(selectedSegmentId)) {
//					System.out.printf("%s] SEND at %s speed:%.2f count: %d, total: %d\n", vehicleId, sampledEdge.id, avgSpeed,  numReadInAgg, numReadings);
//				}
				tSpeed = 0;
				numReadInAgg = 0;
				timestamp = Simulation.currentTime()-(i+1)*uP;
			}
		}
		sensorInput(sWins);
	}

	public void displayOn(Canvas c) {
		Point p = mapView.getPoint(pPos);

		RGB color = !isActive() ? RGB.GRAY :
			(state == State.NORMAL ? RGB.GREEN : 
				(state == State.DELAYED ? RGB.ORANGE : RGB.RED));
		if(homeBase != null) { // is 4Sensing node
			c.sFill(color, new Circle(p.x, p.y, 15));
		} else {
			c.sFill(color, new Rectangle(p.x, p.y, 6.0, 6.0));
		}
	}
}
