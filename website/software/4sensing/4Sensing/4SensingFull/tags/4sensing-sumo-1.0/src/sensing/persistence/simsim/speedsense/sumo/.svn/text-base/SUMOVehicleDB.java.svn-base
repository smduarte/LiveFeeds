package sensing.persistence.simsim.speedsense.sumo;

import java.util.HashMap;

import simsim.gui.canvas.Canvas;

public class SUMOVehicleDB {
	static protected HashMap<String, SUMOVehicle> mNodeDB = new HashMap<String, SUMOVehicle>();

	static public synchronized void put(String id, SUMOVehicle m) {
		mNodeDB.put(id, m);
	}
	
	static public SUMOVehicle get(String id) {
		return mNodeDB.get(id);
	}
	
	public static int size() {
		return mNodeDB.size();
	}
	
//	public void updatePosition() {
//		for(SUMOVehicle m : mNodeDB) {
//			m.updatePosition();
//		}
//	}
	
	public static void displayOn(Canvas c) {
		for(SUMOVehicle m : mNodeDB.values()) {
			m.displayOn(c);
		}
	}
	
	
	//invalidateDisplay is called by even loop thread, has to be sync to avoid concurrent modification exception
//	public synchronized static void invalidateDisplay() {
//		for(SUMOVehicle m : mNodeDB.values()) {
//			m.invalidateDisplay();
//		}	
//	}

}
