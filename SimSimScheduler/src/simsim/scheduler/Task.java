package simsim.scheduler;

import static simsim.scheduler.VT_Scheduler.Scheduler;

/**
 * 
 * This is the class for creating aperiodic tasks in the simulator.
 * 
 * Typically this class will be used to create anonymous classes by overriding run() to
 * place the code to be executed at given time in the future.
 *
 * By default, tasks execute only once. They can be re-scheduled (often within run()) to execute
 * again at a later time.
 * 
 * Tasks can be canceled to prevent them from executing.
 * 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class Task implements Comparable<Task> {
    
    public double due ;
    private TaskOwner owner ;
    protected boolean isQueued = false ;
    protected boolean isCancelled = false ; 
    protected boolean wasReScheduled = false ;

    protected int seqN = g_seqN++;
    private static int g_seqN = 0;

    int queuePosition ;
    /**
     * Creates an new Task.
     * By default it executes once, when it is due.
     * Can be re-scheduled to execute again at a given later time.
     * 
     * @param owner - Owner of the task, a node for certain in this case.
     * @param due Number of seconds to wait until the task executes. 
     */
    public Task( TaskOwner owner, double due) {
    	this.owner = owner ;    	
    	Scheduler.schedule( this, due) ;
    	if( owner != null ) owner.registerTask(this ) ;
    }

    /* (non-Javadoc)
     * 
     * This method should overriden in all concrete subtypes of this base class.
     * @see java.lang.Runnable#run()
     */
    public void run() {
    	System.err.println("Unexpected execution of Task.run() method.") ;
    	System.err.println("Override public void run() in parent class...") ;
    }
    
    /**
     * Tells the time when the task is to due to execute.
     * @return The time when the task is due to execute.
     */
    public double due() {
    	return due ;
    }
    
    /**
     * Cancels the tasks, preventing it from being executed again.
     */
    public void cancel() {
    	isCancelled = true ;
    	wasReScheduled = false ;
    }
    
    /**
     * Asks the task to be scheduled again to execute after the given delay. The period is maintained.
     * @param t The new deadline for next execution of this task.
     */
    public void reSchedule( double t ) {
    	Scheduler.reSchedule( this, t ) ;
    	wasReScheduled = true ;
    }
    
    /**
     * Tells if the task is scheduled for execution.
     * @return true if the task is scheduled for execution or false otherwise.
     */
    public boolean isScheduled() {
    	return this.isQueued ;
    }
     
    /**
     * Tells if the task was reScheduled for execution at a different time...
     * @return true if the task was reScheduled or false otherwise.
     */
    public boolean wasReScheduled() {
    	return this.wasReScheduled ;
    }
    
	protected void reSchedule() {
    }
	
    protected void reset() {
    	//isQueued = false ;
    	wasReScheduled = false ;
    }
    
    public int hashCode() {
    	return seqN ;
    }
    
	public boolean equals( Object o ) {
    	Task other = (Task) o ;
    	return other != null && seqN == other.seqN ;
    }
    
    public int compareTo( Task other) {
    	assert other != null ;
    	if( this.due == other.due ) return (this.seqN - other.seqN) ;
    	else return this.due < other.due ? -1 : 1 ;
    }
  
    protected void release() {
    	if( token != null)
    		token.unblock() ;
    }
    
    public void block() {
    	token = Scheduler.newToken() ;
    	token.block() ;
    }
    
    public String toString() {
        return String.format("%d / %f / %s / %s [%s, %s]", seqN, due, (owner == null ? "" : "" + owner.toString()), getClass(), isQueued, wasReScheduled) ;
    }
    
    private Token token = null ;
}

