package sensing.core.pipeline;

class PProcessor extends Processor  {
	int period;
	boolean started
	
	public PProcessor(int period) {
		this.period = period;
		this.started = false;
	}
	
	public void init() {
		start();
	}
	
	protected start() {
		if(!started) {
			pipeline.schedule(period) {output()};
			started = true;
		}			
	}
	
	protected stop() {
		started = false;
	}
//		if(started) {
//			services.scheduler.unschedule(this);
//			started = false;
//		}
//	}
	
	public void dispose() {	
		stop()
	}
	
}
