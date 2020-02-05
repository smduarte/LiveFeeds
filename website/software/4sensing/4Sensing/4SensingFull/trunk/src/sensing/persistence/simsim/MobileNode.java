package sensing.persistence.simsim;

import groovy.lang.GroovyObject;

import java.util.List;
import java.util.UUID;

import simsim.core.Simulation;
import simsim.gui.canvas.*;
import simsim.gui.geom.XY;

public abstract class MobileNode {
	protected final UUID id;
	protected HomeBase homeBase;

	public MobileNode() {
		id = UUID.randomUUID();
	}
	
	public void init() {}

	public void setHomeBase(HomeBase homeBase) {
		this.homeBase = homeBase;
	}
	
	public boolean is4SensingNode() {
		return homeBase != null;
	}
	

	protected boolean sensorInput(GroovyObject reading, double time) {
		reading.setProperty("mNodeId", id);
		reading.setProperty("time", time);
		return homeBase.sensorInput(reading);
	}
	
	protected boolean sensorInput(GroovyObject reading) {
		return sensorInput(reading, Simulation.currentTime());
	}
	
	protected List<Double> getBindingDestination() {
		return homeBase.getBindingDestination(id);
	}
	
	public abstract XY getPos();
	
	public void updatePosition() {}
	
	public void displayOn(Canvas c) {}
	

	public void displayDetailOn(Canvas c) {}

	public void invalidateDisplay() {}

}