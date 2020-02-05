package feeds.sys.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import feeds.api.Channel;
import feeds.api.Directory;
import feeds.api.Feeds;
import feeds.sys.binding.BindingReply;
import feeds.sys.binding.BindingRequest;
import feeds.sys.catadupa.SubscriptionData;
import feeds.sys.pipeline.PipelineManager;
import feeds.sys.registry.NodeRegistry;
import feeds.sys.registry.RegistryItem;
import feeds.sys.tasks.TaskScheduler;
import feeds.sys.transports.Transports;
import feeds.sys.util.FeedsPrintStream;
import feeds.sys.util.Threading;

public abstract class NodeContext implements simsim.scheduler.TaskOwner {
    
	protected NodeContext( ID id ) {
		this.id = id ;
		context = this ;
	}
	
    public NodeType type() {
    	return type ;
    }
    
	public ID id() {
		return id ;
	}
	
	public void exit() {
		System.exit(0) ;
	}
	    
	public double time() {
		return (System.nanoTime() - T0) * 1e-9;
	}
	    
	public void sleep( double s ) {
		Threading.sleep( (int) (s * 1000) ) ;
	}
	    	    	
	public Thread newThread( boolean daemon, Runnable r ) {
		return Threading.newThread(r, daemon) ;
	}
	
	public String ipAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress() ;
		} catch (UnknownHostException e) {			
			e.printStackTrace();
			return "127.0.0.1" ;
		}
	}
	
    /**
     * Registers a task previously issued by this node.
     *      
    */
     public void registerTask( simsim.scheduler.Task t ) {
    	nodeTasks.add( t ) ;
     }
   
    
    /**
     * Cancels all of the "named" tasks issued by this node.
     */
    public void cancelAllTasks() {
    	for( simsim.scheduler.Task i : nodeTasks ) {
    		i.cancel() ;
    	}
    	nodeTasks.clear();
    }

    public void makeCurrent() {
    	context = this ;
    }
    
	abstract public void init() ;
	
	public boolean isCnode ;
    public boolean isSnode ;
    public boolean isPnode ;
    public boolean isServer ;

    public Channel<String, Void, Void, RegistryItem> rqc;
    public Channel<String, RegistryItem, Void, Void> rrc;
    
    public Channel<Character, SubscriptionData, Void, Void> mc;
    public Channel<Void, BindingRequest, Void, BindingReply> bc;
    
    protected TaskScheduler scheduler ;

	final public ID id ;
	public NodeType type ;
	public Transports tf ;
	public Directory dir ;
	public NodeRegistry reg ;
	public PipelineManager plm ;
	private double T0 = System.nanoTime() ;
	private Set<simsim.scheduler.Task> nodeTasks = new HashSet<simsim.scheduler.Task>() ;

    public static NodeContext context ;
    public static Random rnd = new Random();

    static {
	    Feeds.out = new FeedsPrintStream(System.out) ;
	    Feeds.err = new FeedsPrintStream(System.err) ;
	}

}
