package sensing.persistence.simsim.speedsense.sumo;

//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;

import java.util.ArrayList;

import groovy.lang.GroovyObject;
import sensing.persistence.simsim.PipelineSimJ;
import sensing.persistence.simsim.map.sumo.SUMOMapModel;
import simsim.core.Globals;
import simsim.core.Simulation;
//import simsim.core.Simulation;
import simsim.core.Task;

public class SUMOMNodeSegmentSpeed extends SUMOMobileNode {
	//BufferedWriter out;
	PipelineSimJ simJ;
	final protected int windowSize;
	protected double[] speedWindow; // per segment window
	int numReadings; // number of valid readings in speedWindow
	
	final protected int window2Size;
	protected double[] speedWindow2; // shorter window, independent of segment
	int numReadings2; //number of valid readings in speedWindow2
	
	int changeUploadLag;

	
	protected SUMOMapModel.Edge sampledEdge; // edge currently being sampled
	protected Task uploadTask;

	public SUMOMNodeSegmentSpeed(String vehicleId) {
		super(vehicleId);
		this.windowSize = simJ.<Integer>getConfig("SUMO_MNODE_WINDOWSIZE");;
		this.window2Size = 60; //TODO - setup
	}
	
	public void init() {
		super.init();
		if(homeBase != null) {
			initWindow();
			speedWindow2 = new double[window2Size/uP];
			numReadings2 = 0;	
			changeUploadLag = 3;
		}
	}
	
	public void update(double lat, double lon, double x, double y, double speed, String laneId, double pos) {
		super.update(lat, lon, x, y, speed, laneId, pos);
		if(homeBase != null) {
			if(sampledEdge != null && sampledEdge.id.equals(edgeId)) {
				boolean speedChange = addSpeed(speed);
				if(speedChange && changeUploadLag >= 3) {
					sendReading();
					changeUploadLag = 0;
				}
			
			} else { // changed edge - send immediately and reschedule
				if(uploadTask != null) {
					uploadTask.cancel();
				}
				sendReading();
//				if(edgeId.equals("26030733")) {
//					try {
//						out = new BufferedWriter(new FileWriter("v_" + id));
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				} else {
//					if(out != null) {
//						try {
//							out.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						out = null;
//					}
//				}
				sampledEdge = mapModel.getEdge(edgeId);
				initWindow();
				addSpeed(speed);
				scheduleUpload();
			}
			changeUploadLag++;
		}
	}
	
	protected void scheduleUpload() {
		uploadTask = new Task(sP) {
			public void run() {
				if(isActive()) {
					sendReading();
					scheduleUpload();
				}
			}
		};
	}
	
	
	protected void initWindow() {
		speedWindow = new double[windowSize/uP];
		numReadings = 0;
	}
	
	protected boolean addSpeed(double speed) {
		numReadings = (numReadings < speedWindow.length ? ++numReadings : numReadings);
		System.arraycopy(speedWindow, 0, speedWindow, 1, speedWindow.length-1);
		speedWindow[0] = speed;
		
		numReadings2 = (numReadings2 < speedWindow2.length ? ++numReadings2 : numReadings2);
		System.arraycopy(speedWindow2, 0, speedWindow2, 1, speedWindow2.length-1);
		speedWindow2[0] = speed;
//		if(out != null) {
//			try {
//				out.write(String.format("ADD\t%.2f\t%.2f\t%s\n", Simulation.currentTime(), speed, edgeId));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		if(sampledEdge.id.equals(selectedSegmentId)) {
			System.out.printf("%s] ADD at %s speed:%.2f\n", vehicleId, sampledEdge.id, speed);
		} 
		if(numReadings2 >= 6) {
			double totalSpeed1 = 0, totalSpeed2 = 0;
			for(int i=0; i<3; i++) {
				totalSpeed1+=speedWindow2[i];
			}
			for(int i=3; i<numReadings2; i++) {
				totalSpeed2+=speedWindow2[i];
			}
			double newSpeed = totalSpeed1/3;
			double oldSpeed = totalSpeed2/(numReadings2-3);
			boolean speedChange = Math.abs(oldSpeed-  newSpeed) >= 2.8; // +- 10kmh
			if(sampledEdge.id.equals(selectedSegmentId) && speedChange) {
				System.out.printf("%s] SPEEDCHANGE at %s old speed:%.2f new speed:%.2f\n", vehicleId, sampledEdge.id, oldSpeed, newSpeed);
			} 
			return speedChange;
		}
		return false;
	}
	
	protected void sendReading() {
		if(numReadings == 0 ) return;
		if(!sampledEdge.id.equals(selectedSegmentId)) return;
		
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
				if(!sampledEdge.id.equals(selectedSegmentId)) {
					System.out.printf("%s] SEND at %s speed:%.2f count: %d, total: %d\n", vehicleId, sampledEdge.id, avgSpeed,  numReadInAgg, numReadings);
				}
				tSpeed = 0;
				numReadInAgg = 0;
				timestamp = Simulation.currentTime()-(i+1)*uP;
			}
		}
		sensorInput(sWins);
//		if(out != null) {
//			try {
//				out.write(String.format("SEND\t%.2f\t%.2f\t%s\n", Simulation.currentTime(), avgSpeed, edgeId));
//				out.flush();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}

}
