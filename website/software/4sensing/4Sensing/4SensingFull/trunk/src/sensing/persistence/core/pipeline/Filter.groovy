package sensing.persistence.core.pipeline;

import org.codehaus.groovy.runtime.InvokerHelper;

public class Filter extends Processor implements GroovyInterceptable {
	def last;
	def input;
	double lastTime = -1;

	public Filter() {
		super();
	}
	public Filter(Closure clos) {
		super(clos);
	}
	
	def invokeMethod(String name, args) {
		def metaClass = InvokerHelper.getMetaClass(this);
		if(name == "process") {
			input = args[0]
			// if filter evaluates to true, propagate the input value
			if(metaClass.invokeMethod(this, name, args)) {
				last = input;
				lastTime = services.scheduler.currentTime();
				return input;
			} 
		} else {
			return metaClass.invokeMethod(this, name, args);
		}
	}


	public boolean changePercent(String prop, double percent) {
		return(!last || 
				input.getProperty(prop) > last.getProperty(prop) + last.getProperty(prop) * (percent/100) ||
				input.getProperty(prop) < last.getProperty(prop) - last.getProperty(prop) * (percent/100));	
	}

	public boolean changeAbsolute(String prop, double value) {
		//!= condition required for NaN values
		if(!last) return true
		def lastProp = last.getProperty(prop);
		def inputProp = input.getProperty(prop);
		if(lastProp instanceof Double && lastProp == Double.NaN && inputProp == Double.NaN) return false;
		return (input.getProperty(prop) != last.getProperty(prop)) && Math.abs(input.getProperty(prop) - last.getProperty(prop)) > value
	}
	
	public boolean elapsedTime(double lapse ) {
		if(!last) return true;
		return (services.scheduler.currentTime() - lastTime) >= lapse;
	}

	public void reset() {
		last = null;
		input = null;
	}
	
}
