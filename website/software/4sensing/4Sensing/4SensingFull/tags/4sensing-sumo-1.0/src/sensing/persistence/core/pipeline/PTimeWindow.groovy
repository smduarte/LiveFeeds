package sensing.persistence.core.pipeline;
import sensing.persistence.core.pipeline.Tuple;
import static sensing.persistence.core.logging.LoggingProvider.*;

class PTimeWindow  extends PProcessor {
	SimpleWindow timeWindow;
	
	public PTimeWindow(int size, int period) {
		super(period);
		timeWindow = new SimpleWindow(size, period);
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
	
	public void process(EOS input) {}
	
	public boolean output() {
		//services.logging.log(DEBUG, this, "PTimeWindow output", "${querycontext.context}");
		timeWindow.output(services.scheduler.currentTime(), {forward(it)});
		forward(EOS.instance);
		return true;
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
