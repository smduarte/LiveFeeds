package simsim.utils;

import static simsim.core.Simulation.rg;

import java.util.Iterator;
import java.util.TreeSet;


/**
 * 
 * An utility class that implements a list that can index elements at random positions.
 * It uses the Simulation random generator to produce the random indices.
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 * @param <T> The type of the elements of the list.
 */
@SuppressWarnings("serial")
public class RandomList2<T> extends RandomList<T>{
	
	public Iterator<T> iterator() {
		return new RandomIterator() ;
	}
	
	class RandomIterator implements Iterator<T> {
		
		int curr = 0 ;
		int remaining = size() ;
		
		public boolean hasNext() {
			return remaining > 0 ;
		}

		public T next() {
			int n = curr + rg.nextInt( remaining-- ) ;
			T res = get( n ) ;
			set(n, get(curr)) ;
			set(curr++, res) ;
			return res ;
		}

		@Override
		public void remove() {
			throw new RuntimeException("Not supported") ;
		}
		
	}
	
	public String toString() {
		return new TreeSet<T>(this).toString() ;
	}
}
