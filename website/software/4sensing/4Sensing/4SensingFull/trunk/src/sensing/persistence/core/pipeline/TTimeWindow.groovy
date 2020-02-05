package sensing.persistence.core.pipeline;
import sensing.persistence.core.pipeline.Tuple;
import static sensing.persistence.core.logging.LoggingProvider.*;

class TTimeWindow extends TProcessor {
	SimpleWindow timeWindow;
	
	public TTimeWindow(int size, int period) {
		super(period);
		timeWindow = new SimpleWindow(size);
	}
	
	public void init() {
		super.init()
		timeWindow.logging = services.logging
	}
	
	public void reset() {
		timeWindow.reset();	
	}
	
	
	public Tuple process(Tuple input) {
		timeWindow.input(services.scheduler.currentTime(), input);
	}
	
	public boolean output() {
		//int slide = lastOutput > 0 ? (int)(services.scheduler.currentTime() - lastOutput) : period;
		def windowContent = timeWindow.output(services.scheduler.currentTime());
		//services.logging.log(DEBUG, this, "window output", "slide: ${slide} ntuples: ${windowContent.size()}");

		windowContent.each{forward(it)};
		forward(EOS.instance);
		return (windowContent.size() > 0);
	}
	
	public getWindowContent() {
		def content = timeWindow.getContent()
		if(next) {
			return content + next.getWindowContent();
		} else {
			return content;
		}
	}

}
