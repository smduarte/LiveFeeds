package sensing.persistence.core.test;
import java.util.Random;

public class Window {
	def window  = new int[30];
	int lastIdx = -1;
	long then;

	private void timeForward() {
		long now = System.currentTimeMillis() / 1000;
		int timeLapse = now - then;
		timeLapse.times() {
			lastIdx = (lastIdx + 1) % window.length;
			window[lastIdx] = 0;
		}
		then = now;
	}

	public void input(int input) {
		if(lastIdx == -1) {
			lastIdx = 0;
			then = System.currentTimeMillis() / 1000;
			window[lastIdx] = input;
		} else {
			timeForward()
			window[lastIdx] = input;	
		}
	}
	
	public void display() {
		timeForward();
		int windowIdx = lastIdx;
		print "[";
		window.length.times() {
			windowIdx = (windowIdx + 1) % window.length;
			if(window[windowIdx] > 0) {print window[windowIdx] + " "};
		}
		println "]";
	}
	
	public static void main(String[] args) {
		int r = 0;
		def myWin = new Window();
		Thread.start{while(true) {myWin.display();sleep(5000);}}
		Thread.start{while(true) {myWin.input((r++ % 50)+1); sleep(1000)}};
	}
	
}
