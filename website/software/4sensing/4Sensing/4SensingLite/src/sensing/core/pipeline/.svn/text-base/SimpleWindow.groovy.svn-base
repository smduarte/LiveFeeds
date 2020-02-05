package sensing.persistence.core.pipeline;
import sensing.persistence.core.pipeline.Tuple;
import sensing.persistence.core.logging.*;
import static sensing.persistence.core.logging.LoggingProvider.*;

public class SimpleWindow {
	int size;
	int period;
	LoggingProvider logging;
	
	class Entry{
		double time;
		Tuple tuple;
		
		public Entry(double time, Tuple tuple) {
			this.time = time;
			this.tuple = tuple;
		}
	}
	
	def window = [];
	
	public SimpleWindow(int size, int period) {
		this.size = size;
		this.period = period;
	}
	
	public void input(double time, Tuple t) {
		window << new Entry(time,t)
	}
	
	public output(double time, Closure outproc) {
		double windowStart = time-size;
		def newWindow = [];
		window.each{ Entry e -> 
			if(e.time >= windowStart) {
				outproc(e.tuple);
				if(e.time >= windowStart+period) {
					newWindow << e;	
				}
			}
		}
		window = newWindow;
	}
	
	public void reset() {
		window = [];
	}
	
	public List getContent() {
		return window.collect{it.tuple}	
	}
}
