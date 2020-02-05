package sensing.core.scheduler;

import groovy.lang.Closure;

public interface SchedulerProvider {
	public void schedule (double period, Closure clos, boolean prioritary);
	public void schedule (double period, Closure clos);
	public void schedule (double period, Object taskContext, Object owner, Closure clos,  boolean prioritary);
	public void schedule (double period, Object taskContext, Object owner, Closure clos);
	public void scheduleOnce(double due, Closure clos);
	public void unschedule();
	public void unschedule( Object taskContext,Object owner);
	public double currentTime();
}
