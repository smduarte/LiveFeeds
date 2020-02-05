package sensing.core.pipeline;

import java.util.UUID;
import java.awt.geom.Rectangle2D;

public class Tuple {

	UUID mNodeId;
	UUID peerId;
	double time;
	double lat;
	double lon;
	Rectangle2D boundingBox;
	boolean isResult = false; // TODO: martelada
	
	def debugInfo;
	int level = -1;
	int gaLevel = 0;
	double winStart = 0; // Debug info: window span for aggregate data
	double winEnd = 0;
	
	
	public Tuple() {

	}
	
	public Tuple(Tuple t) {
		this.mNodeId = t.mNodeId;
		this.peerId = t.peerId;
		this.time = t.time;
		this.lat = t.lat;
		this.lon = t.lon;
		this.boundingBox = t.boundingBox;
		this.isResult = t.isResult;
		this.debugInfo = t.debugInfo;
		this.level = t.level;
		this.gaLevel = t.gaLevel;
		this.winStart = t.winStart; // Debug info: window span for aggregate data
		this.winEnd = t.winEnd;
	}

	public Tuple derive(Class tupleClass) {
		def derived = tupleClass.newInstance();
		copyProperties(this, derived);
		return derived;
	}
	
	public Tuple derive(Class tupleClass, propertyMap) {
		def derived = derive(tupleClass);
		propertyMap.each{prop -> derived.setProperty(prop.key, prop.value)}
		return derived;
	}
	
	private copyProperties(Tuple source, Tuple dest){
		// copy common properties
		source.metaClass.properties.each { prop ->
			def destProp;
			if(prop.name != "metaClass" && prop.name != "class" && (destProp = dest.metaClass.hasProperty(dest, prop.name)) && destProp.getSetter()) {
				destProp.setProperty(dest,source.getProperty(prop.name))
			}
		}
	}
	
	public String toString() {
		String properties = "${this.class.name}: ";
		def props = this.properties.collect{it.key}.sort()
		props.each { prop ->
		    if(prop != "metaClass" && prop != "class" && prop != "debugInfo") {
		    	properties += " ${prop}: ${getProperty(prop)}\t";
			}
		}
		return properties;
	}
		
}
