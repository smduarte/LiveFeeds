package simsim.utils;

import java.util.Iterator;

public class AppendIterator<T> implements Iterator<T> , Iterable<T>{

	protected Iterator<T> curr, first, second;

	public AppendIterator(Iterator<T> a, Iterator<T> b) {
		curr = first = a;
		second = b;
	}

	public AppendIterator(Iterable<T> a, Iterable<T> b) {
		curr = first = a.iterator();
		second = b.iterator();
	}
	
	@Override
	public boolean hasNext() {
		if (curr.hasNext())
			return true;
		else
			return (curr = second).hasNext();
	}

	@Override
	public T next() {
		return curr.next();
	}

	@Override
	public void remove() {
		curr.remove();
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}
}