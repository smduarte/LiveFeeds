package simsim.core;

import simsim.gui.canvas.RGB;
import simsim.gui.canvas.Canvas;


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
	
	public RGB color ;

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
     * Creates an new Task.
     * By default it executes once, when it is due.
     * Can be re-scheduled to execute again at a given later time.
     * 
     * @param owner - Owner of the task, a node for certain in this case.
     * @param due Number of seconds to wait until the task executes. 
     */
    public Task( AbstractNode owner, double due ) {
    	this( owner, due, RGB.GRAY ) ;
    }

    /**
     * Creates an new Task.
     * By default it executes once, when it is due.
     * Can be re-scheduled to execute again at a given later time.
     * 
     * @param owner - Owner of the task, a node for certain in this case.
     * @param due Number of seconds to wait until the task executes. 
     * @param color The color used to display the task in the Scheduler 
     */
    public Task( AbstractNode owner, double due, RGB color ) {
    	super( owner, due ) ;
    	this.color = color ;
    }
          
    public void displayOn( Canvas canvas ) {}
    
    protected void reSchedule() {}
}

