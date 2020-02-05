package feeds.sys.registry;

import static feeds.sys.registry.Location.HARDSTATE;
import static feeds.sys.registry.Location.SOFTSTATE;

import java.util.Iterator;
import java.util.Properties;

import feeds.api.FeedsException;
import feeds.sys.FeedsNode;
import feeds.sys.core.ID;

public class NodeRegistry {

	private VHashtable softstate, hardstate;

	public NodeRegistry() {
		this(false);
	}

	public NodeRegistry(String regDir) {
		this.softstate = new VHashtable();
		this.hardstate = new PHashtable(regDir);
	}

	public NodeRegistry(boolean emulatedHardstate) {
		this.softstate = new VHashtable();
		this.hardstate = emulatedHardstate ? new VHashtable() : softstate;
	}

	public RegistryItem getItem(String key) {
		RegistryItem ri = softstate.get(key);
		if (ri == null)
			ri = hardstate.get(key);
		return ri;
	}

	public void putItem(RegistryItem ri, Location where)
			throws IllegalArgumentException {
		if (where == SOFTSTATE) {
			softstate.put(ri);
			return;
		}
		if (where == HARDSTATE) {
			if (softstate.containsKey(ri.key()) && softstate != hardstate)
				throw new IllegalArgumentException(
						"Hardstate key shadowed by a softstate one already present. Not allowed.");
			else {
				if (hardstate != softstate)
					hardstate.put(ri);
				else
					FeedsNode.rrc().publish(ri.key, ri);
			}
			return;
		}
		throw new IllegalArgumentException("Invalid 'where' parameter");
	}

	public void put(String key, Object value) throws IllegalArgumentException {
		putItem(new RegistryItem(key, value), SOFTSTATE);
	}

	public void put(String key, Object value, Location where)
			throws IllegalArgumentException {
		putItem(new RegistryItem(key, value), where);
	}

	public void put(String key, Object value, int scope, int duration, ID owner)
			throws IllegalArgumentException {
		putItem(new RegistryItem(key, value, scope, duration, owner), HARDSTATE);
	}

	public Object get(String key) {
		RegistryItem ri = getItem(key);
		return ri == null ? null : ri.value;
	}

	public Object get(String key, boolean invalidateCache, int timeout)
			throws FeedsException {
		if (invalidateCache)
			remove(key, SOFTSTATE);
		return null; // TODO DistributedRegistry.get( key, timeout ) ;
	}

	public Object remove(String key) {
		RegistryItem ri = (RegistryItem) softstate.remove(key);
		if (ri != null)
			ri = (RegistryItem) hardstate.remove(key);
		return ri;
	}

	public Object remove(String key, Location where) {
		RegistryItem ri = null;
		if (where == SOFTSTATE)
			ri = (RegistryItem) softstate.remove(key);
		if (where == HARDSTATE)
			ri = (RegistryItem) hardstate.remove(key);
		return ri;
	}

	public void clear() {
		softstate.clear();
		hardstate.clear();
	}

	public Iterable<RegistryItem> values(final String preffix,
			final Location source) {
		return new Iterable<RegistryItem>() {
			public Iterator<RegistryItem> iterator() {
				if (source == SOFTSTATE)
					return softstate.values(preffix);
				if (source == HARDSTATE)
					return hardstate.values(preffix);
				throw new IllegalArgumentException("Invalid source parameter");
			}
		};
	}

	public NodeRegistry init() {
		try {
			Properties p = System.getProperties();
			for (Object i : p.keySet())
				put(i.toString(), p.get(i), SOFTSTATE);
		} catch (Exception x) {
		}
		return this;
	}

	public NodeRegistry create(String filename) {
		hardstate = new PHashtable(filename);

		return this;
	}
}
