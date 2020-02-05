package meeds.sys.homing.containers;


import meeds.sys.homing.*;

public interface Location {

	public Position pos() ;
		
	interface Updater {
	
	    public void set( Position pos ) ;

	}
}
