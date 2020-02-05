package feeds.sys.tasks;

import feeds.sys.core.*;
import feeds.sys.util.Threading;
/**
 * 
 * This is the base class for creating tasks in the simulator.
 * 
 * Typically this class will be used to create anonymous classes by overriding run() to
 * place the code to be executed at given time in the future.
 *
 * By default, tasks execute only once. They can be re-scheduled (within run()) to execute
 * again at a later time.
 * 
 * Tasks can also be canceled to prevent them from executing.
 * 
 * Tasks are executed according to <b>simulation time</b>.
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class Task extends simsim.scheduler.Task {
	
	final public NodeContext context ;

    /**
     * Creates an anonymous Task to execute "immediately".
     * The task will execute once, but 
     * can be re-scheduled to execute again at a given later time.
     */
    public Task() {
    	this(0 ) ;
    }
    
    /**
     * Creates an anonymous Task.
     * By default it executes once, when it is due.
     * Can be re-scheduled to execute again at a given later time.
     * 
     * @param due Number of seconds to wait until the task executes. 
     */
    public Task( double due ) {
    	this( null, due ) ;
    }
    
    /**
     * Creates an anonymous Task.
     * By default it executes once, when it is due.
     * Can be re-scheduled to execute again at a given later time.
     *  
     * @param owner The owner of this task. 
     * @param due Number of seconds to wait until the task executes. 
     */
    public Task( simsim.scheduler.TaskOwner owner, double due ) {
    	super( owner, due ) ;
		context = NodeContext.context ;
    }
    
    public void reset() {
    	while(context == null ) //ugly hack, sorry...
    		Threading.sleep(1) ;
    	
    	super.reset() ;
    	context.makeCurrent() ;
    }
    
    public void block() {
    	super.block() ;
    	context.makeCurrent() ;
    }
}

