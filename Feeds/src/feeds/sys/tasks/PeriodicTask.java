package feeds.sys.tasks;

import simsim.scheduler.TaskOwner;
import feeds.sys.core.*;
import feeds.sys.util.Threading;

public class PeriodicTask extends simsim.scheduler.PeriodicTask {
    
	final public NodeContext context ;

    /**
     * Creates an anonymous task that is automatically scheduled to run with a given frequency/period.
     * @param period The period of time between successive executions.
     */
    public PeriodicTask( double period ) {
        this(0, period) ;
    }
    
    /**
     * Creates an anonymous task that is automatically scheduled to run with a given frequency/period.
     * @param due The number of seconds before this task executes for the first time.
     * @param period The period of time between successive executions.
     */
    public PeriodicTask( double due, double period ) {
    	this( null, due, period) ;
    }    
    
    /**
     * Creates an anonymous task that is automatically scheduled to run with a given frequency/period.
     * @param due The number of seconds before this task executes for the first time.
     * @param period The period of time between successive executions.
     */
    public PeriodicTask( TaskOwner owner, double due, double period ) {
    	super( owner, due, period) ;
    	context = NodeContext.context ;
    }   
    
    protected void reset() {
    	while(context == null ) //ugly hack, sorry...
    		Threading.sleep(1);
    	super.reset() ;
    	context.makeCurrent() ;
    }
    
    public void block() {
    	super.block() ;
    	context.makeCurrent() ;
    }
}
