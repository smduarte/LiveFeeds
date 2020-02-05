package simsim.graphs;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 */
public class Link<T extends Comparable<T>> implements Comparable<Link<T>> {
	public static int INFINITE = 100000;

	public Link(T v, T w, double cost) {
		this.v = v;
		this.w = w;
		this.cost = cost;
		if (v.equals(w))
			Thread.dumpStack();
	}

	@SuppressWarnings("unchecked")
	public boolean equals(final Object other) {
		return other != null && equals((Link<T>) other);
	}

	public boolean equals(final Link<T> other) {
		return (v.equals(other.v) && w.equals(other.w)) || (v.equals(other.w) && w.equals(other.v));
	}

	public int hashCode() {
		return v.hashCode() ^ w.hashCode();
	}

	public String toString() {
		return v + "<->" + w + ": " + cost;
	}

	public int compareTo(Link<T> other) {
		if (this.cost != other.cost) 
			return (this.cost < other.cost ? -1 : 1);
		else {
			int r = v.compareTo(other.v) ;
			return r == 0 ? (w.compareTo(other.w)) : r ;
		} 
	}

	public T v, w;
	public double cost;
}