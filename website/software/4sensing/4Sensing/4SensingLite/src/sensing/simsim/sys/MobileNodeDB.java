package sensing.simsim.sys;

import java.util.HashMap;

import simsim.gui.canvas.Canvas;

public class MobileNodeDB<T extends MobileNode> {
	protected HashMap<String,T> mNodeDB = new HashMap<String,T>();

	public synchronized void put(String id, T m) {
		mNodeDB.put(id, m);
	}
	
	public T get(String id) {
		return mNodeDB.get(id);
	}
	
	public int size() {
		return mNodeDB.size();
	}
	
//	public void updatePosition() {
//		for(MobileNode m : mNodeDB) {
//			m.updatePosition();
//		}
//	}
	
	public void displayOn(Canvas c) {
		for(MobileNode m : mNodeDB.values()) {
			m.displayOn(c);
		}
	}
	
	
	//invalidateDisplay is called by even loop thread, has to be sync to avoid concurrent modification exception
	public synchronized void invalidateDisplay() {
		for(MobileNode m : mNodeDB.values()) {
			m.invalidateDisplay();
		}	
	}

}
