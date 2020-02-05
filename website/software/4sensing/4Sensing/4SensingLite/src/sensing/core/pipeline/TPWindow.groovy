package sensing.core.pipeline;

import sensing.core.pipeline.*;
import static sensing.core.logging.LoggingProvider.*;

public class TPWindow extends TProcessor {

	def window; 
	int lastIdx = -1;
	int windowStart = -1;
	long lastTime;
	long totalTimeLapse = 0;
	long lastDisplay = -1;

	public TPWindow(int size, double period) {
		super(period);
		window = new Object[size];
	}


	public void process(input) {
		if(lastIdx == -1) {
			lastIdx = 0;
		} else {
			int timeLapse = input.time - lastTime;
			if(timeLapse <= 0) {
				timeLapse = -timeLapse;
				if(timeLapse > window.length) {
					println "out of sequence";
					return;
				} else if(lastIdx >= timeLapse) {
					add(input, lastIdx-timeLapse);
					//window[lastIdx-timeLapse] = input;
					return;
				} else {
					add(input, window.length-(timeLapse-lastIdx));
					//window[window.length-(timeLapse-lastIdx)] = input;
					return;
				}
			}
			//println "time forward: ${timeLapse}";
			timeLapse.times() {
				lastIdx = (lastIdx + 1) % window.length;
				window[lastIdx] = null;
			}
			totalTimeLapse += timeLapse;
		}
		add(input, lastIdx);
		//window[lastIdx] = input;
		lastTime = input.time;
		windowStart = lastIdx;
		return null;
	}
	
	protected void add(input, int idx) {
		if(window[idx] == null) {
			window[idx] = [];
		} 
		window[idx] << input;
	}
	
	protected boolean output() {
		if(lastIdx == -1) {
			return false;
		}
		if(windowStart == -1) { //?? TODO:
			windowStart = lastIdx;
		}
		int displayLapse = (services.scheduler.currentTime() - lastDisplay);
		services.logging.log(DEBUG, this, "window output", "slide: ${displayLapse}");
		//println("displayLapse: ${displayLapse} totalTimeLapse: ${totalTimeLapse} lastDisplay: ${lastDisplay} currentTime: ${pipeline.currentTime()}")
		if(lastDisplay > 0 && totalTimeLapse < displayLapse) {
			(displayLapse-totalTimeLapse).times {
				windowStart = (windowStart + 1) % window.length;
				window[windowStart] = null;			
			}
		}
		//print "[";
		int windowIdx = windowStart;
		boolean reschedule = false;
		window.length.times() {
			windowIdx = (windowIdx + 1) % window.length;
			if(window[windowIdx] != null) {
				//printf("%d ", window[windowIdx].speed);
				window[windowIdx].each{forward(it)};
				reschedule = true;
			} 
		}
		forward(EOS.instance);
		//println "]";
		//println window;
		totalTimeLapse = 0;
		lastDisplay = services.scheduler.currentTime();
		return reschedule;
	}
	
	public boolean canDispose() {
		//services.logging.log(DEBUG, this, "canDispose", "super: ${super.canDispose()} elapsedTime: ${services.scheduler.currentTime()-lastTime} window: ${window.size()}");
		return super.canDispose() && (services.scheduler.currentTime() - lastTime > window.size());
	}

}
