package feeds.sys.pipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import feeds.api.Criteria;
import feeds.api.Subscription;
import feeds.sys.core.Container;
import feeds.sys.core.ContainerListener;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;
import feeds.sys.membership.containers.CriteriaDB;


public class ChannelSubscriptions<E> extends Container<ChannelSubscriptions<E>> implements Iterable<ChannelSubscription<E>>, ContainerListener<ChannelSubscriptions<E>> {
         
    public ChannelSubscriptions( ID ch, String name, boolean monitorable ) {
        super( name ) ;
        this.channel = ch ;
        if( monitorable ) 
        	monitor(this) ;

        this.isFeedbackContainer = name.startsWith("fsc") ;
        
        super.notifyUpdateNow() ;
    }
    
    public ID channel() {
    	return null ;
    }
    
    public int size() {
        return subscriptions.size() ;
    }
    
    public boolean isEmpty() {
        return subscriptions.isEmpty() ;
    }
    
    public void clear() {
        criteria.clear() ;
        subscriptions.clear() ;
        super.notifyUpdate() ;
    }
     
    synchronized public void remove( Subscription cs ) {        
        criteria.remove( cs ) ;
        subscriptions.remove( cs ) ;     
        super.notifyUpdate() ;
    }
    
    synchronized public void remove( Transport t ) {        
    	ChannelSubscription<?> cs = null ;
    	for( ChannelSubscription<?> i : subscriptions.values() )
    		if( i.transport == t ) {
    			cs = i ;
    			break ;
    		}
    	if( cs != null )
    		remove( cs ) ;
    }
   
    synchronized public ChannelSubscription<E> add( ChannelSubscription<E> cs ) {
    	subscriptions.put( cs, cs ) ;
        criteria.put( cs, cs.criteria ) ;        
        super.notifyUpdateNow() ; //Now used only for debugging...
        return cs ;
    }
    
    public Iterator<ChannelSubscription<E>> iterator() {
		return subscriptions.values().iterator() ;
	}

    synchronized public Set<Criteria<?>> criteria() {
    	return new HashSet<Criteria<?>>( criteria.values() );
    }

    synchronized public Collection<ChannelSubscription<E>> subscriptions() {
    	return new ArrayList<ChannelSubscription<E>>( subscriptions.values() );
    }
     
    
    public String toString() {
    	return String.format("%s Empty: %b : %s", super.toString(), isEmpty(), subscriptions ) ;
    }

	public void handleContainerUpdate(ChannelSubscriptions<E> c) {
		if( ! criteria.isEmpty() )
			if( isFeedbackContainer )
				cdb.fUpdate( channel, criteria() ) ;
			else
				cdb.pUpdate( channel, criteria() ) ;
	}	

	
	private ID channel ;
	private boolean isFeedbackContainer ;
	private CriteriaDB.Updater cdb = Container.byClass( CriteriaDB.class ) ;
    private Map<Subscription, Criteria<E>> criteria = new HashMap<Subscription, Criteria<E>>() ;
    private Map<Subscription, ChannelSubscription<E>> subscriptions = new HashMap<Subscription, ChannelSubscription<E>>() ;    
}
