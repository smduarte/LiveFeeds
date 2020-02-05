package meeds.sys.homing.containers.impl;

import feeds.sys.core.* ;

import meeds.sys.homing.* ;
import meeds.sys.homing.containers.*;

public class Location_Impl extends Container<Location> implements Location, Location.Updater {
    
    
    public Location_Impl() {
    	super.notifyUpdate() ;
    }

	public Position pos() {
		return latest ;
	}

	public void set(Position pos) {
		latest = pos ;
		super.notifyUpdate() ;
	}

	
	Position latest = new Position() ;
}