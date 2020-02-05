package sensing.persistence.core.pipeline;

public class Window {

	def window; 
	int slide;
	int lastIdx = -1;
	int windowStart = -1;
	long lastTime;
	long totalTimeLapse = 0;
	long lastDisplay = -1;

	public Window(int size) {
		window = new Object[size];
	}
	
	public void input(input) {
		if(lastIdx == -1) {
			lastIdx = 0;
		} else {
			int timeLapse = input.time - lastTime;
//			if(timeLapse <= 0) {
//				timeLapse = -timeLapse;
//				if(timeLapse > window.length) {
//					println "out of sequence ${input.time}";
//					return;
//				} else if(lastIdx >= timeLapse) {
//					add(input, lastIdx-timeLapse);
//					//window[lastIdx-timeLapse] = input;
//					return;
//				} else {
//					add(input, window.length-(timeLapse-lastIdx));
//					//window[window.length-(timeLapse-lastIdx)] = input;
//					return;
//				}
//			}
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
	
	protected output(int slide) {
		if(lastIdx == -1) {
			return [];
		}
		assert(windowStart > -1)
//		if(windowStart == -1) { //?? TODO:
//			windowStart = lastIdx;
//		}
		//println("displayLapse: ${displayLapse} totalTimeLapse: ${totalTimeLapse} lastDisplay: ${lastDisplay} currentTime: ${pipeline.currentTime()}")
		if(totalTimeLapse < slide) {
			(slide-totalTimeLapse).times {
				windowStart = (windowStart + 1) % window.length;
				window[windowStart] = null;			
			}
		}
		//print "[";
		int windowIdx = windowStart;
		def windowContent = [];
		window.length.times() {
			windowIdx = (windowIdx + 1) % window.length;
			if(window[windowIdx] != null) {
				//printf("%d ", window[windowIdx].speed);
				window[windowIdx].each{windowContent << it};
			} 
		}
		//forward(EOS.instance);
		//println "]";
		//println window;
		totalTimeLapse = 0;
		return windowContent;
	}
	
}
