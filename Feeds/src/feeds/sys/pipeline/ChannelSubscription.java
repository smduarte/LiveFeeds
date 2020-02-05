package feeds.sys.pipeline;

import java.io.Serializable;

import feeds.api.Criteria;
import feeds.api.Subscription;
import feeds.sys.FeedsNode;
import feeds.sys.core.ID;
import feeds.sys.core.Transport;

@SuppressWarnings("serial")
public class ChannelSubscription<E> implements Subscription, Serializable {

	final ID channel ;
    final ID subscriber ;
    final Criteria<E> criteria ;
    final boolean ignoreDestination ;
    final boolean isFeedbackSubscription ;
    
    public ChannelSubscription( ID channel, ID subscriber, Criteria<E> criteria, Transport transport, boolean isFeedbackSubscription ) {
    	this( channel, subscriber, criteria, transport, isFeedbackSubscription, FeedsNode.isServer() ) ;
    }
  
    public ChannelSubscription( ID channel, ID subscriber, Criteria<E> criteria, Transport transport, boolean isFeedbackSubscription, boolean ignoreDestination ) {
    	this.channel = channel ;
        this.transport = transport ;
        this.subscriber = subscriber ;
        this.ignoreDestination = ignoreDestination ;
        this.isFeedbackSubscription = isFeedbackSubscription ;
        this.criteria = (criteria == null ? new Criteria<E>() : criteria) ;
    }
    
    public Transport transport() {
    	return transport ;
    }
        
    public Criteria<E> criteria() {
    	return criteria ;
    }
    
    public Criteria<E> getCriteria() {
    	return criteria;
    }
    
    void setOwner( ChannelSubscriptions<E> container ) {
    	this.container = container ;
    }
    
	public void cancel() {
		container.remove( this ) ;
	}

    public String toString() {
        return criteria + "/" + subscriber + "/" + ignoreDestination ;
    }

    public int hashCode() {
    	return subscriber.hashCode() ;
    }
    
    public boolean equals( ChannelSubscription<E> other ) {
    	return subscriber.equals( other.subscriber ) && criteria.equals( other.criteria ) ;
    }
    
    @SuppressWarnings("unchecked")
	public boolean equals( Object other ) {
    	return equals( (ChannelSubscription<E>) other ) ;
    }
    
    final Transport transport ;
	transient private ChannelSubscriptions<E> container ;   
}
