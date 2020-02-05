package simsim.scheduler;

import static simsim.scheduler.VT_Scheduler.Scheduler;

import java.util.concurrent.Semaphore;

@SuppressWarnings("serial")
public class Token extends Semaphore {

	Token() {
		super(0) ;
	}
	
	public void block() {
		Scheduler.threadManager.acquire(this);
	}

	public void unblock() {
		Scheduler.threadManager.release(this);
	}
}