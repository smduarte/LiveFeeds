package sensing.persistence.core.pipeline;

public class TWindow extends Component {
	def window; 
	int lastIdx = -1;
	int windowStart = -1;
	long lastTime;
	long totalTimeLapse = 0;
	long lastDisplay = -1;

	public TWindow(int size) {
		super();
		window = new Object[size];
	}


	public synchronized void input(input) {
		//println "input ${input}";

		if(lastIdx == -1) {
			lastIdx = 0;
		} else {
			int timeLapse = input.time - lastTime;
			if(timeLapse <= 0) {
				//println "out of sequence";
				return;
			}
			//println "time forward: ${timeLapse}";
			timeLapse.times() {
				lastIdx = (lastIdx + 1) % window.length;
				window[lastIdx] = null;
			}
			totalTimeLapse += timeLapse;
		}
		window[lastIdx] = input;
		lastTime = input.time;
		windowStart = lastIdx;	
		output();
	}
	
	public synchronized void output() {
//		if(pipeline.context.isRoot) {
//			println "output";
//		}
		if(lastIdx == -1) {
			return;
		}
		if(windowStart == -1) {
			windowStart = lastIdx;
		}
		int displayLapse = (pipeline.currentTime() - lastDisplay);
		//println("displayLapse: ${displayLapse} totalTimeLapse: ${totalTimeLapse} lastDisplay: ${lastDisplay} currentTime: ${pipeline.currentTime()}")
		if(lastDisplay > 0 && totalTimeLapse < displayLapse) {
			(displayLapse-totalTimeLapse).times {
				windowStart = (windowStart + 1) % window.length;
				window[windowStart] = null;			
			}
		}
		//print "[";
		int windowIdx = windowStart;
		window.length.times() {
			windowIdx = (windowIdx + 1) % window.length;
			if(window[windowIdx] != null) {
				//printf("%d ", window[windowIdx].speed);
				forward(window[windowIdx]);
			} 
		}
		forward(EOS.instance);
		//println "]";
		//println window;
		totalTimeLapse = 0;
		lastDisplay = pipeline.currentTime();
	}

	public void dispose() {
		println "DISPOSE called";		
		pipeline.uschedule(this);
	}
	
}
