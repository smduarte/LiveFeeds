package sensing.persistence.core.pipeline;

public class TThrottle extends Component {
	protected int period;
	protected double lastSentTime;

	public TThrottle(int period) {
		this.period = period;
		lastSentTime = 0;
	}
	
	public void input(Object input) {
		double currTime = pipeline.currentTime();
		if(currTime - lastSentTime >= period) {
			next?.input(input);
			lastSentTime = currTime;
		}
	}


}
