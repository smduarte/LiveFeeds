package feeds.sys.templates.centradupa.anycast;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import feeds.api.Criteria;
import feeds.api.Feeds;
import feeds.sys.FeedsNode;
import feeds.sys.core.ID;

public class SubscriptionData implements Serializable {

	final ID owner;
	final double timestamp;
	final Set<Criteria<?>> pCriteria;
	final Set<Criteria<?>> fCriteria;


	public SubscriptionData(Set<Criteria<?>> pC, Set<Criteria<?>> fC) {
		pCriteria = pC;
		fCriteria = fC;
		owner = FeedsNode.id();
		timestamp = Feeds.time();
	}

	public SubscriptionData() {
		this(new HashSet<Criteria<?>>(), new HashSet<Criteria<?>>());
	}

	private SubscriptionData(SubscriptionData other) {
		this.owner = other.owner;
		this.timestamp = Feeds.time() ;
		this.pCriteria = new HashSet<Criteria<?>>();
		this.fCriteria = new HashSet<Criteria<?>>();

		pCriteria.addAll( other.pCriteria ) ;
		fCriteria.addAll( other.fCriteria ) ;
	}

	public boolean newer(SubscriptionData other) {
		return timestamp >= other.timestamp;
	}

	@SuppressWarnings("unchecked")
	public boolean pAccepts(Object e) {
			for (Criteria i : pCriteria) {
				try {
					if (i.accepts(e))
						return true;
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean fAccepts(Object e, ID dst) {
		if (dst.equals(owner)) {
				for (Criteria i : fCriteria) {
					try {
						if (i.accepts(e))
							return true;
					} catch (Exception x) {
					}
				}
		}
		return false;
	}

	public double age() {
		return Feeds.time() - timestamp ;
	}
	
	public boolean isEmpty() {
		return (pCriteria == null || pCriteria.isEmpty()) && ( fCriteria == null || fCriteria.isEmpty()) ;
	}
	
	public SubscriptionData clone() {
		return new SubscriptionData(this);
	}

	public String toString() {
		return String.format("<%s | %s>", pCriteria, fCriteria);
	}

	public int hashCode() {
		return owner.hashCode() ;
	}
	
	public boolean equals( SubscriptionData other ) {
		return owner.equals(other.owner ) ;
	}
	
	public boolean equals( Object other ) {
		return other != null && equals( (SubscriptionData) other ) ;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
