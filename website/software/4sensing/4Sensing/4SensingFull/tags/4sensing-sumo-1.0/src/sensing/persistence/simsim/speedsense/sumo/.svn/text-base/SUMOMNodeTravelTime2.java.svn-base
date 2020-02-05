package sensing.persistence.simsim.speedsense.sumo;

//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;

import java.awt.Point;

import groovy.lang.GroovyObject;
import sensing.persistence.simsim.map.sumo.SUMOMapModel;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Rectangle;

public class SUMOMNodeTravelTime2 extends SUMOMobileNode {

	enum State {NORMAL, DELAYED, BLOCKED};
	
	final protected int windowSize;
	protected double[] speedWindow; // per segment window
	protected double[] posWindow;
	protected int numReadings; // number of valid readings in speedWindow

	protected State state;
	protected int changeUploadLag;
	protected boolean periodicUpload;
	protected double lastPos;
	protected double edgeEntryTs;
	protected boolean isRouteStart;	
	protected SUMOMapModel.Edge sampledEdge; // edge currently being sampled
	protected String sampledLaneId;

	public SUMOMNodeTravelTime2(String vehicleId, int updatePeriod) {
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
				if(vehicleId.equals("791731")) System.out.println(vehicleId + "] ROUTE START: " + edgeId);
			} else if(!sampledEdge.id.equals(edgeId)) { // crossed edge
				sendReading(true, sampledEdge.length);
				edgeEntry();
				isRouteStart = false;
				addReading();
				if(vehicleId.equals("791731"))  System.out.println(vehicleId + "] CROSSED EDGE: " + edgeId);
			} else { // same edge
				addReading();
				if(periodicUpload == true) { //TODO 12
					if(vehicleId.equals("791731")) System.out.println(vehicleId + "] PERIODIC UPLOAD Lag:" +  changeUploadLag);
					if(changeUploadLag > 0 && changeUploadLag % 12 == 0) {
						if(vehicleId.equals("791731"))  System.out.println(vehicleId + "] PERIODIC UPLOAD - SENDING");
						sendReading(false, pos);
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
		posWindow = new double[windowSize/uP];
		numReadings = 0;
	}
	
	protected void addReading() {
		numReadings = (numReadings < speedWindow.length ? ++numReadings : numReadings);
		System.arraycopy(speedWindow, 0, speedWindow, 1, speedWindow.length-1);
		speedWindow[0] = speed;
		System.arraycopy(posWindow, 0, posWindow, 1, posWindow.length-1);
		posWindow[0] = pos;
		//double expectedTT = (Simulation.currentTime() - edgeEntryTs) + (sampledEdge.length - pos) / avgSpeed;
		double elapsedTT = Simulation.currentTime() - edgeEntryTs;
		double minTT = sampledEdge.length / sampledEdge.maxSpeed;
		
		double tDistance = getTraveledDistance();
		
		switch(state) {
		case NORMAL: 
			if (elapsedTT  > minTT + 60) {
				state = State.DELAYED;
				startPeriodicUpload();
			} break;
		case DELAYED:
			if(tDistance == 0) {
				state = State.BLOCKED;
			} break;
		case BLOCKED:
			if(tDistance > 0) {
				state = State.DELAYED;
			} break;
		}
		
		
//		if(state == State.DELAYED || state == State.BLOCKED) {
//			if(changeUploadLag >= 12 && pos == lastPos) {
//				state = State.BLOCKED;
//			} else {
//				state = State.DELAYED;
//			}
//		} else if (elapsedTT  > minTT + 60) {
//			state = State.DELAYED;
//			startPeriodicUpload();
//		} else {
//			state = State.NORMAL;
//			endPeriodicUpload();
//		} 
		
		if(vehicleId.equals("791731"))  {
			System.out.printf("%s] ADD at %s (%.2f) speed:%.2f\n", vehicleId, sampledEdge.id, pos, speed);
		} 
	}
	
	protected double getAvgSpeed() {
		return getAvgSpeed(windowSize);
	}
	
	protected double getAvgSpeed(int period) {
		int avgNumReadings = Math.min((int)period/uP, numReadings);
		double avgSpeed = 0;
		for(int i=0; i<avgNumReadings; i++) {
			avgSpeed += speedWindow[i];
		}
		avgSpeed /= avgNumReadings;
		return avgSpeed;		
	}
	
	protected double getTraveledDistance() {
		double startPos = posWindow[numReadings-1];
		double endPos = posWindow[0];
		return endPos-startPos;
	}
	
	protected void startPeriodicUpload() {
		if(vehicleId.equals("791731"))  System.out.println(vehicleId + "] START PERIODIC UPLOAD was:" + periodicUpload);
		if(periodicUpload == false) {
			periodicUpload = true;
			changeUploadLag = 0;
			lastPos = pos;
		}
	}
	
	protected void endPeriodicUpload() {
		if(vehicleId.equals("791731"))  System.out.println(vehicleId + "] END PERIODIC UPLOAD was:" + periodicUpload);
		if(periodicUpload == true) {
			periodicUpload = false;
			changeUploadLag = 0;
			lastPos = 0;
		}
	}
	
	protected void sendReading(boolean edgeExit, double newPos) {
		if(vehicleId.equals("791731"))  System.out.println(vehicleId + "] SEND: " +  sampledEdge.id);
		//if(!sampledEdge.id.equals(selectedSegmentId)) return;

		GroovyObject tt = simJ.newTuple("speedsense.TravelTime");
		tt.setProperty("segmentId", sampledEdge.id);
		tt.setProperty("laneId", sampledLaneId);
		tt.setProperty("boundingBox", sampledEdge.bbox);
		tt.setProperty("pos", newPos);
		double travelTime = 0;
		double elapsedTime = Simulation.currentTime() - edgeEntryTs;
		double avgSpeed = 0;//getAvgSpeed();
		if(edgeExit) {
			travelTime =  elapsedTime;
			avgSpeed = sampledEdge.length/travelTime;
			tt.setProperty("isRealTT", 1);
		} else if(state == State.DELAYED || state == State.BLOCKED)  {
				//double tDistance = getTraveledDistance();
				//avgSpeed = tDistance / (numReadings * uP);
			
				//avgSpeed = pos / elapsedTime;
				avgSpeed = getAvgSpeed();
				travelTime = elapsedTime + (sampledEdge.length - pos) / avgSpeed;
		}
		tt.setProperty("isBlocked", (state == State.BLOCKED));
		tt.setProperty("travelTime", travelTime);
		tt.setProperty("elapsedTime", elapsedTime);
		tt.setProperty("avgSpeed", avgSpeed);
		if(vehicleId.equals("791731"))  System.out.println(vehicleId + "] SEND: " + travelTime + (edgeExit ? " REAL " : " ESTIMATE ") + (state == State.BLOCKED ? "BLOCKED" : "MOVING" + " SPEED: " + avgSpeed));
		boolean accepted = sensorInput(tt);
		if(vehicleId.equals("791731")) System.out.println(vehicleId + "] SEND result: " + accepted);
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
