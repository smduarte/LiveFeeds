package feeds.sys.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import feeds.api.FeedsException;
import feeds.sys.FeedsRegistry;
import feeds.sys.tasks.PeriodicTask;
import feeds.sys.tasks.Task;

public class Container<T> {
	static private final String REGISTRY_BASE = "/Containers/";

	private String name = "<?>";
	private Collection<ContainerListener<T>> listeners;

	protected Container() {
		this(0) ;
	}

	protected Container( String name) {
		this(0, name ) ;
	}

	protected Container( double autoRefreshPeriod ) {
		listeners = new ArrayList<ContainerListener<T>>();
		setAutoRefresh( autoRefreshPeriod);
	}

	protected Container( double autoRefreshPeriod, String name) {
		this.name = name;
		FeedsRegistry.put(REGISTRY_BASE + this.name, this);
		listeners = new ArrayList<ContainerListener<T>>();
		setAutoRefresh( autoRefreshPeriod);
	}
	
	private void setAutoRefresh( double period ) {
		if( period > 0 )
			new PeriodicTask(period) {
				public void run() {
					notifyUpdateNow() ;
				}
			};
	}
	
	@SuppressWarnings("unchecked")
	public static <Q> Q byName( String name) {
		try {
			String[] pcn = parseClassName(name) ;
			String key = REGISTRY_BASE + pcn[1] ;
			Q c = FeedsRegistry.get( key);
			if (c == null) {
				c = (Q) Class.forName( implPath(pcn) ).newInstance();
				FeedsRegistry.put( key, c ) ;
			}
			return c;
		} catch (Exception x) {
			x.printStackTrace() ;
			System.exit(0) ;
			throw new FeedsException("Unknown container:" + name);			
		}
	}
	
	public static <Q> Q byClass( Class<?> c) {
		return byName( c.getName() ) ;
	}
	
	public void monitor(ContainerListener<T> listener) {
		if( ! listeners.contains( listener) )
				listeners.add( listener ) ;
		this.notifyUpdate();
	}
	
	synchronized public static <Q> void monitor( Class<?> container, ContainerListener<Q> listener ) {
		Container<Q> c = byName( container.getName() ) ;
		c.monitor(listener) ;
	}

	public void notifyUpdate() {
		notifier.reSchedule(0.1) ;
	}

	@SuppressWarnings("unchecked")
	public void notifyUpdateNow() {
		for (ContainerListener<T> i : new ArrayList<ContainerListener<T>>( listeners ))
			i.handleContainerUpdate( (T)this);		
	}

	public String toString() {
		return String.format("Container: %s [#%d] = %s", this.getClass().getSimpleName(), listeners.size(), listenerClasses()) ;
	}
	
	private List<Class<?>> listenerClasses() {
		ArrayList<Class<?>> res = new ArrayList<Class<?>>() ;
		for( ContainerListener<?> i : listeners )
			res.add( i.getClass() ) ;
		return res ;
	}
	
	static private String implPath( String[] name ) {
		return name[0] + ".impl." + name[1] + "_Impl";
	}
		
	static String[] parseClassName( String name ) {
		int i = name.lastIndexOf('.') ;
		String pkg = i < 0 ? "" : name.substring(0, i) ;
		String snm = i < 0 ? name : name.substring(i+1) ;
		return new String[] {pkg, snm} ;
	}
	
	private Task notifier = new Task( Double.MAX_VALUE ) {
		public void run() {
			try {
				notifyUpdateNow();
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}; 
}
