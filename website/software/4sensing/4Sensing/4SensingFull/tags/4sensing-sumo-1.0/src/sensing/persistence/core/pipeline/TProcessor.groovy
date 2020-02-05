package sensing.persistence.core.pipeline;

import groovy.lang.GroovyInterceptable;
import static sensing.persistence.core.logging.LoggingProvider.*;
import org.codehaus.groovy.runtime.InvokerHelper;

public abstract class TProcessor extends Processor implements GroovyInterceptable {

	protected double period;
	protected double lastOutput = -1; // last output timestamp
	protected scheduled = false;
	protected final boolean immediate;

	public TProcessor(double period) {
		this.period = period;
	}
	

	protected abstract boolean output();

	def invokeMethod(String name, args) {
		def mc = InvokerHelper.getMetaClass(this);
		if(name != "process") {
			return mc.invokeMethod(this, name, args);
		}
		def result =  mc.invokeMethod(this, name, args);
		if(!scheduled) {
			int due = (int)(period - (services.scheduler.currentTime() - lastOutput));
			if(due <= 0) { // output immediatly
				services.logging.log(DEBUG, this, "invokeMethod", "due now");
				due = (int)period
				result = immediateOutput(mc, args)
			}
			schedule(due);
		}
		return result;
	}
	
	protected immediateOutput(MetaClass mc, args) {
		//services.logging.log(DEBUG, this, "immediateOutput", "");
		mc.invokeMethod(this, "output", null);
		lastOutput = services.scheduler.currentTime();
	}
	
	public boolean canDispose() {
		return !scheduled;
	}

	public void dispose() {
//		services.scheduler.unschedule(this);
	}
	
	protected void schedule(int due) {
		services.logging.log(DEBUG, this, "scheduled", "scheduling, due in ${due}");
		services.scheduler.scheduleOnce(due) {
			scheduled = false
			services.logging.log(DEBUG, this, "schedule", "scheduled output");
			boolean reschedule = output();
			lastOutput = services.scheduler.currentTime();
			if(reschedule) {
				schedule((int)period);
			} 
		}
		scheduled = true;
	}
	
}
