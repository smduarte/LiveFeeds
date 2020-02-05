package sensing.persistence.core.test;
import java.util.Random;



public class Window2 {
	
	def window  = new Value[10];
	int lastIdx = -1;
	int windowStart = -1;
	long lastTime;
	long totalTimeLapse = 0;
	long lastDisplay = -1;

	public synchronized void input(Value input) {
		println "input ${input.val}";
		if(lastIdx == -1) {
			lastIdx = 0;
		} else {
			int timeLapse = input.time - lastTime;
			if(timeLapse <= 0) {
				println "out of sequence";
				return;
			}
			println "time forward: ${timeLapse}";
			timeLapse.times() {
				lastIdx = (lastIdx + 1) % window.length;
				window[lastIdx] = null;
			}
			totalTimeLapse += timeLapse;
		}
		window[lastIdx] = input;
		lastTime = input.time;
		windowStart = lastIdx;		
	}
	
	public synchronized void display() {
		println "display";
		if(lastIdx == -1) {
			return;
		}
		if(windowStart == -1) {
			windowStart = lastIdx;
		}
		int displayLapse = (System.currentTimeMillis() - lastDisplay)/1000
		if(lastDisplay > 0 && totalTimeLapse < displayLapse) {
			(displayLapse-totalTimeLapse).times {
				windowStart = (windowStart + 1) % window.length;
				window[windowStart] = null;			
			}
		}
		print "[";
		int windowIdx = windowStart;
		window.length.times() {
			windowIdx = (windowIdx + 1) % window.length;
			if(window[windowIdx] != null) {
				print window[windowIdx].val + " ";
			} 
		}
		println "]";
		println window;
		totalTimeLapse = 0;
		lastDisplay = System.currentTimeMillis();
//		5.times() {
//			windowStart = (windowStart + 1) % window.length;
//			window[windowStart] = null;
//		}
//		windowStart++;

	}
	
	public static void main(String[] args) {
		int i = 30;
		int r = 0;
		Random rnd = new Random();
		int delay = 3;
		def myWin = new Window2();
		Thread.start{while(true) {myWin.display();sleep(5000 + rnd.nextInt(1000));}}
		Thread.start{while(i-- > 0) {myWin.input( new Value(val: ((r++ % 50)+1), time: System.currentTimeMillis()/1000 - rnd.nextInt(2))); sleep(1000)}};
	}
	
}
