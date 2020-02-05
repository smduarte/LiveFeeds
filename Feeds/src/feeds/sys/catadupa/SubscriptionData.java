package feeds.sys.catadupa;

import java.io.*;
import java.util.*;

import feeds.api.*;
import feeds.sys.FeedsNode;
import feeds.sys.core.*;

public class SubscriptionData implements Serializable {

	final ID owner;
	final double timestamp;
	final Map<ID, Set<Criteria<?>>> pCriteria;
	final Map<ID, Set<Criteria<?>>> fCriteria;

	SubscriptionData(Map<ID, Set<Criteria<?>>> pC, Map<ID, Set<Criteria<?>>> fC) {
		pCriteria = pC;
		fCriteria = fC;
		owner = FeedsNode.id();
		timestamp = Feeds.time();
	}

	SubscriptionData() {
		this(new HashMap<ID, Set<Criteria<?>>>(), new HashMap<ID, Set<Criteria<?>>>());
	}

	private SubscriptionData(SubscriptionData other) {
		this.owner = other.owner;
		this.timestamp = other.timestamp;
		this.pCriteria = new HashMap<ID, Set<Criteria<?>>>();
		this.fCriteria = new HashMap<ID, Set<Criteria<?>>>();

		for (Map.Entry<ID, Set<Criteria<?>>> i : other.pCriteria.entrySet())
			pCriteria.put(i.getKey(), new HashSet<Criteria<?>>(i.getValue()));

		for (Map.Entry<ID, Set<Criteria<?>>> i : other.fCriteria.entrySet())
			fCriteria.put(i.getKey(), new HashSet<Criteria<?>>(i.getValue()));
	}

	public boolean newer(SubscriptionData other) {
		return timestamp >= other.timestamp;
	}

	@SuppressWarnings("unchecked")
	public boolean pAccepts(Object e, ID channel) {
		Set<Criteria<?>> cs = pCriteria.get(channel);
		if (cs != null)
			for (Criteria i : cs) {
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
	public boolean fAccepts(Object e, ID channel, ID dst) {
		if (dst.equals(owner)) {
			Set<Criteria<?>> cs = fCriteria.get(channel);
			if (cs != null)
				for (Criteria i : cs) {
					try {
						if (i.accepts(e))
							return true;
					} catch (Exception x) {
					}
				}
		}
		return false;
	}

	public SubscriptionData clone() {
		return new SubscriptionData(this);
	}

	public String toString() {
		return String.format("<%s : %f>", pCriteria, timestamp);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
