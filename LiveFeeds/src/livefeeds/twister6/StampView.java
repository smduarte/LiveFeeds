package livefeeds.twister6;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import livefeeds.twister6.config.Config;

public class StampView implements Serializable {

	long owner;
	public int cutoff = -1, maxSerial = -1;
	final Map<Long, SlidingStampSet> data;

	public StampView(long key) {
		owner = key;
		data = new LinkedHashMap<Long, SlidingStampSet>();
	}
	
	private StampView(StampView other) {
		owner = other.owner;
		cutoff = other.cutoff;
		data = new LinkedHashMap<Long, SlidingStampSet>();
		for (Map.Entry<Long, SlidingStampSet> i : other.data.entrySet()) {
			data.put(i.getKey(), new SlidingStampSet(i.getValue()));
		}
	}

	public StampView clone() {
		return new StampView(this);
	}

	public void add(Stamp s) {
		if( s.c_serial > maxSerial )
			maxSerial = s.c_serial ;
		
		if (s.c_serial > cutoff)
			getSet(s.key).add(s);
	}

	private SlidingStampSet getSet(Long key) {
		SlidingStampSet res = data.get(key);
		if (res == null) {
			res = new SlidingStampSet(cutoff);
			data.put(key, res);
		}
		return res;
	}

	public boolean contains(Stamp s) {
		if (cutoff >= s.c_serial)
			return true;
		SlidingStampSet x = data.get(s.key);
		return x != null && x.contains(s, cutoff);
	}

	public boolean contains(StampView other) {
		for (Map.Entry<Long, SlidingStampSet> i : other.data.entrySet()) {
			SlidingStampSet tSet = data.get(i.getKey());
			if (tSet == null || !tSet.containsAll(i.getValue(), cutoff))
				return false;
		}
		return true;
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (Map.Entry<Long, SlidingStampSet> i : data.entrySet()) {
			sb.append(String.format("<%10d = %s>", i.getKey(), i.getValue()));
		}
		sb.append('}');
		return cutoff + " >>>> " + sb.toString();
	}

	public int size() {
		return data.size();
	}

	//		
	public int length() {
		trim( Config.Config.VIEW_CUTOFF ) ;
		
		int res = 0;
		for (SlidingStampSet i : data.values())
			res += i.length();
		return 1 + res;
	}

	public StampView trim(int cut) {
		cut = maxSerial - cut;
		if (cut > cutoff)
			cutoff = cut;
		
		for (Iterator<SlidingStampSet> i = data.values().iterator(); i.hasNext();) {
			if (i.next().trim(cutoff).isEmpty())
				i.remove();
		}
		
		return this;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

class SlidingStampSet extends TreeSet<Stamp> {

	int smallest = -1;

	SlidingStampSet(int cutoff) {
	}

	SlidingStampSet(SlidingStampSet other) {
		super(other);
	}

	public SlidingStampSet clone() {
		return new SlidingStampSet(this);
	}

	public boolean add(Stamp s) {
		return super.add(s);
	}

	SlidingStampSet trim(int cutoff) {
		for (Iterator<Stamp> i = iterator(); i.hasNext();)
			if (i.next().c_serial <= cutoff)
				i.remove();
		
		return this;
	}


	int length() {
		int res = 0;

		Stamp[] sa = super.toArray(new Stamp[size()]);

		int i = 0;
		for (; i < sa.length - 1 && sa[i + 1].p_serial == sa[i].c_serial; i++)
			;

		for (; i < sa.length; i++)
			res++;

		return 16 + 4 + +1 + res;
	}

	public boolean contains(Stamp s, int cutoff) {
		return s.c_serial <= cutoff || super.contains(s);
	}

	public boolean contains(Object other) {
		Thread.dumpStack();
		return false;
	}

	public boolean containsAll(Collection<?> c, int cutoff) {
		for (Object i : c)
			if (((Stamp) i).c_serial > cutoff && !super.contains(i))
				return false;

		return true;
	}

	public String toString() {
		return super.toString();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
