/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package simsim.scheduler;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An unbounded priority {@linkplain Queue queue} based on a priority heap. The
 * elements of the priority queue are ordered according to their
 * {@linkplain Comparable natural ordering}, or by a {@link Comparator} provided
 * at queue construction time, depending on which constructor is used. A
 * priority queue does not permit {@code null} elements. A priority queue
 * relying on natural ordering also does not permit insertion of non-comparable
 * objects (doing so may result in {@code ClassCastException}).
 * 
 * <p>
 * The <em>head</em> of this queue is the <em>least</em> element with respect to
 * the specified ordering. If multiple elements are tied for least value, the
 * head is one of those elements -- ties are broken arbitrarily. The queue
 * retrieval operations {@code poll}, {@code remove}, {@code peek}, and
 * {@code element} access the element at the head of the queue.
 * 
 * <p>
 * A priority queue is unbounded, but has an internal <i>capacity</i> governing
 * the size of an array used to store the elements on the queue. It is always at
 * least as large as the queue size. As elements are added to a priority queue,
 * its capacity grows automatically. The details of the growth policy are not
 * specified.
 * 
 * <p>
 * This class and its iterator implement all of the <em>optional</em> methods of
 * the {@link Collection} and {@link Iterator} interfaces. The Iterator provided
 * in method {@link #iterator()} is <em>not</em> guaranteed to traverse the
 * elements of the priority queue in any particular order. If you need ordered
 * traversal, consider using {@code Arrays.sort(pq.toArray())}.
 * 
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> Multiple
 * threads should not access a {@code PriorityQueue} instance concurrently if
 * any of the threads modifies the queue. Instead, use the thread-safe
 * {@link java.util.concurrent.PriorityBlockingQueue} class.
 * 
 * <p>
 * Implementation note: this implementation provides O(log(n)) time for the
 * enqueing and dequeing methods ({@code offer}, {@code poll}, {@code remove()}
 * and {@code add}); linear time for the {@code remove(Object)} and
 * {@code contains(Object)} methods; and constant time for the retrieval methods
 * ({@code peek}, {@code element}, and {@code size}).
 * 
 * <p>
 * This class is a member of the <a href="{@docRoot}
 * /../technotes/guides/collections/index.html"> Java Collections Framework</a>.
 * 
 * @since 1.5
 * @version %I%, %G%
 * @author Josh Bloch, Doug Lea
 * @param <E>
 *            the type of elements held in this collection
 */
public class CustomPriorityQueue<E extends Task> extends AbstractQueue<E> {

	private static final int DEFAULT_INITIAL_CAPACITY = 5000;

	/**
	 * Priority queue represented as a balanced binary heap: the two children of
	 * queue[n] are queue[2*n+1] and queue[2*(n+1)]. The priority queue is
	 * ordered by comparator, or by the elements' natural ordering, if
	 * comparator is null: For each node n in the heap and each descendant d of
	 * n, n <= d. The element with the lowest value is in queue[0], assuming the
	 * queue is nonempty.
	 */
	private Task[] queue;

	/**
	 * The number of elements in the priority queue.
	 */
	private int size = 0;

	/**
	 * Creates a {@code PriorityQueue} with the default initial capacity (11)
	 * that orders its elements according to their {@linkplain Comparable
	 * natural ordering}.
	 */
	public CustomPriorityQueue() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * Creates a {@code PriorityQueue} with the specified initial capacity that
	 * orders its elements according to their {@linkplain Comparable natural
	 * ordering}.
	 * 
	 * @param initialCapacity
	 *            the initial capacity for this priority queue
	 * @throws IllegalArgumentException
	 *             if {@code initialCapacity} is less than 1
	 */
	public CustomPriorityQueue(int initialCapacity) {
		if (initialCapacity < 1)
			throw new IllegalArgumentException();
		this.queue = new Task[initialCapacity];
	}

	/**
	 * Increases the capacity of the array.
	 * 
	 * @param minCapacity
	 *            the desired minimum capacity
	 */
	private void grow(int minCapacity) {
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		int oldCapacity = queue.length;
		// Double size if small; else grow by 50%
		int newCapacity = 3 * oldCapacity / 2 ;
		if (newCapacity < 0) // overflow
			newCapacity = Integer.MAX_VALUE;
		if (newCapacity < minCapacity)
			newCapacity = minCapacity;
		queue = Arrays.copyOf(queue, newCapacity);
	}

	/**
	 * Inserts the specified element into this priority queue.
	 * 
	 * @return {@code true} (as specified by {@link Collection#add})
	 * @throws ClassCastException
	 *             if the specified element cannot be compared with elements
	 *             currently in this priority queue according to the priority
	 *             queue's ordering
	 * @throws NullPointerException
	 *             if the specified element is null
	 */
	public boolean offer(E e) {
		return add(e);
	}

	/**
	 * Inserts the specified element into this priority queue.
	 * 
	 * @return {@code true} (as specified by {@link Queue#offer})
	 * @throws ClassCastException
	 *             if the specified element cannot be compared with elements
	 *             currently in this priority queue according to the priority
	 *             queue's ordering
	 * @throws NullPointerException
	 *             if the specified element is null
	 */
	public boolean add(E e) {
		if (e == null)
			throw new NullPointerException();
		int i = size;
		if (i >= queue.length)
			grow(i + 1);
		size = i + 1;
		if (i == 0)
			queue[e.queuePosition = 0] = e;
		else
			siftUp(i, e);
		
		e.isQueued = true ;
		return true;
	}

	
	
	@SuppressWarnings("unchecked")
	public E peek() {
		if (size == 0)
			return null;
		return (E) queue[0];
	}


	/**
	 * Removes a single instance of the specified element from this queue, if it
	 * is present. More formally, removes an element {@code e} such that
	 * {@code o.equals(e)}, if this queue contains one or more such elements.
	 * Returns {@code true} if and only if this queue contained the specified
	 * element (or equivalently, if this queue changed as a result of the call).
	 * 
	 * @param o
	 *            element to be removed from this queue, if present
	 * @return {@code true} if this queue changed as a result of the call
	 */
	public boolean remove(Object o) {
		return remove( (Task) o ) ;
	}

	public boolean remove(Task t) {
		if( t.queuePosition >= 0 ) {
			removeAt( t.queuePosition ) ;
			
			t.queuePosition = -1 ;
			t.isQueued = false ;
			
			return true ;
		}
		return false ;
	}
	
	
	public void changeKey( E t, double oldValue ) {
		if( t.due <= oldValue)
			siftUp(t.queuePosition, t) ;
		else
			siftDown(t.queuePosition, t );		
	}
	/**
	 * Returns {@code true} if this queue contains the specified element. More
	 * formally, returns {@code true} if and only if this queue contains at
	 * least one element {@code e} such that {@code o.equals(e)}.
	 * 
	 * @param o
	 *            object to be checked for containment in this queue
	 * @return {@code true} if this queue contains the specified element
	 */
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	private int indexOf(Object o) {
		if (o != null) {
			for (int i = 0; i < size; i++)
				if (o.equals(queue[i]))
					return i;
		}
		return -1;
	}

	/**
	 * Returns an array containing all of the elements in this queue. The
	 * elements are in no particular order.
	 * 
	 * <p>
	 * The returned array will be "safe" in that no references to it are
	 * maintained by this queue. (In other words, this method must allocate a
	 * new array). The caller is thus free to modify the returned array.
	 * 
	 * <p>
	 * This method acts as bridge between array-based and collection-based APIs.
	 * 
	 * @return an array containing all of the elements in this queue
	 */
	public Object[] toArray() {
		return Arrays.copyOf(queue, size);
	}

	/**
	 * Returns an array containing all of the elements in this queue; the
	 * runtime type of the returned array is that of the specified array. The
	 * returned array elements are in no particular order. If the queue fits in
	 * the specified array, it is returned therein. Otherwise, a new array is
	 * allocated with the runtime type of the specified array and the size of
	 * this queue.
	 * 
	 * <p>
	 * If the queue fits in the specified array with room to spare (i.e., the
	 * array has more elements than the queue), the element in the array
	 * immediately following the end of the collection is set to {@code null}.
	 * 
	 * <p>
	 * Like the {@link #toArray()} method, this method acts as bridge between
	 * array-based and collection-based APIs. Further, this method allows
	 * precise control over the runtime type of the output array, and may, under
	 * certain circumstances, be used to save allocation costs.
	 * 
	 * <p>
	 * Suppose <tt>x</tt> is a queue known to contain only strings. The
	 * following code can be used to dump the queue into a newly allocated array
	 * of <tt>String</tt>:
	 * 
	 * <pre>
	 * String[] y = x.toArray(new String[0]);
	 * </pre>
	 * 
	 * Note that <tt>toArray(new Object[0])</tt> is identical in function to
	 * <tt>toArray()</tt>.
	 * 
	 * @param a
	 *            the array into which the elements of the queue are to be
	 *            stored, if it is big enough; otherwise, a new array of the
	 *            same runtime type is allocated for this purpose.
	 * @return an array containing all of the elements in this queue
	 * @throws ArrayStoreException
	 *             if the runtime type of the specified array is not a supertype
	 *             of the runtime type of every element in this queue
	 * @throws NullPointerException
	 *             if the specified array is null
	 */
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			// Make a new array of a's runtime type, but my contents:
			return (T[]) Arrays.copyOf(queue, size, a.getClass());
		System.arraycopy(queue, 0, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	/**
	 * Returns an iterator over the elements in this queue. The iterator does
	 * not return the elements in any particular order.
	 * 
	 * @return an iterator over the elements in this queue
	 */
	public Iterator<E> iterator() {
		return new Itr();
	}

	private final class Itr implements Iterator<E> {

        private int currentIndex = -1;

        public boolean hasNext() {
            return currentIndex < size - 1;
        }

        @SuppressWarnings("unchecked")
		public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return (E)queue[++currentIndex];
        }

        public void remove() {
        }
    }
	public int size() {
		return size;
	}

	/**
	 * Removes all of the elements from this priority queue. The queue will be
	 * empty after this call returns.
	 */
	public void clear() {
		for (int i = 0; i < size; i++)
			queue[i] = null;		
		size = 0;
	}

	@SuppressWarnings("unchecked")
	public E poll() {
		if (size == 0)
			return null;
		int s = --size;
		Task result = queue[0];
		Task x = queue[s];
		queue[s] = null;
		if (s != 0)
			siftDown(0, x);
		
		result.isQueued = false ;
		result.queuePosition = -1 ;
		
		return (E)result;
	}

	/**
	 * Removes the ith element from queue.
	 * 
	 * Normally this method leaves the elements at up to i-1, inclusive,
	 * untouched. Under these circumstances, it returns null. Occasionally, in
	 * order to maintain the heap invariant, it must swap a later element of the
	 * list with one earlier than i. Under these circumstances, this method
	 * returns the element that was previously at the end of the list and is now
	 * at some position before i. This fact is used by iterator.remove so as to
	 * avoid missing traversing elements.
	 */
	private Task removeAt(int i) {
		assert i >= 0 && i < size;
		int s = --size;
		if (s == i) // removed last element
			queue[i] = null;
		else {
			Task moved = queue[s];
			queue[s] = null;
			siftDown(i, moved);
			if (queue[i] == moved) {
				siftUp(i, moved);
				if (queue[i] != moved)
					return moved;
			}
		}
		return null;
	}

	/**
	 * Inserts item x at position k, maintaining heap invariant by promoting x
	 * up the tree until it is greater than or equal to its parent, or is the
	 * root.
	 * 
	 * To simplify and speed up coercions and comparisons. the Comparable and
	 * Comparator versions are separated into different methods that are
	 * otherwise identical. (Similarly for siftDown.)
	 * 
	 * @param k
	 *            the position to fill
	 * @param x
	 *            the item to insert
	 */

	private void siftUp(int k, Task x) {
		Task key = x;
		while (k > 0) {
			int parent = (k - 1) >>> 1;
			Task e = queue[parent];
			if (key.compareTo(e) >= 0)
				break;
			queue[e.queuePosition = k] = e;
			k = parent;
		}
		queue[key.queuePosition = k] = key;
	}

	/**
	 * Inserts item x at position k, maintaining heap invariant by demoting x
	 * down the tree repeatedly until it is less than or equal to its children
	 * or is a leaf.
	 * 
	 * @param k
	 *            the position to fill
	 * @param x
	 *            the item to insert
	 */
	private void siftDown(int k, Task x) {
		Task key = x;
		int half = size >>> 1; // loop while a non-leaf
		while (k < half) {
			int child = (k << 1) + 1; // assume left child is least
			Task c = queue[child];
			int right = child + 1;
			if (right < size && c.compareTo(queue[right]) > 0)
				c = queue[child = right];
			if (key.compareTo(c) <= 0)
				break;
			queue[c.queuePosition = k] = c;
			k = child;
		}
		queue[key.queuePosition = k] = key;
	}

	/**
	 * Returns the comparator used to order the elements in this queue, or
	 * {@code null} if this queue is sorted according to the
	 * {@linkplain Comparable natural ordering} of its elements.
	 * 
	 * @return the comparator used to order this queue, or {@code null} if this
	 *         queue is sorted according to the natural ordering of its elements
	 */
	public Comparator<? super E> comparator() {
		return null;
	}
}
