package sensing.core.pipeline;

public interface PipelineScheduler {
	
	public void schedule (double period, Closure clos);
	public void scheduleOnce(double due, Closure clos);
	public double currentTime();
}
