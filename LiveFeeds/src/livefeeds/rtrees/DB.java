package livefeeds.rtrees;

import static livefeeds.rtrees.config.Config.Config;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import livefeeds.rtrees.msgs.DbUploadEndpoints;
import livefeeds.rtrees.msgs.DbUploadFilters;
import livefeeds.rtrees.msgs.NewArrivals;

import simsim.core.Simulation;
import simsim.utils.RandomList;

public class DB {

	private static final long KEY_RANGE = (1L << Config.NODE_KEY_LENGTH);

	final CatadupaNode owner;

	public View view;
	public StampView sview ;
	
	public SlidingBitSet knownNodes ; //, deadNodes;

	public int neededFilterPieces ;
	public boolean loadedEndpoints, loadedFilters ;

	TreeSet<Integer> missingStamps = new TreeSet<Integer>();
	
	protected DB(CatadupaNode owner) {
		this.owner = owner;
		
		this.view = new View(owner.key);
//		this.deadNodes = new SlidingBitSet( (int)(Config.AVERAGE_ARRIVAL_RATE * Config.VIEW_CUTOFF) );
//		this.deadNodes.set(0) ;
		
		this.knownNodes = new SlidingBitSet();
		sview = Simulation.rg.nextDouble() < 0.99 ? null : new StampView( owner.key ) ; 
		
		
		this.loadedEndpoints = false ;
		this.neededFilterPieces = (1 << Config.DB_FILTER_DOWNLOAD_PIECES) - 1 ;
		this.loadedFilters = neededFilterPieces == 0 ;
	}

	public boolean isEmpty() {
		return GlobalDB.size() < 2;
	}

	public void store( NewArrivals m ) {

		assert loadedEndpoints ;
		
		view.add(m.stamp);
		store(m.joins, m.rejoins, m.failures);		
		if( sview != null )
			sview.add(m.stamp) ;
		
		missingStamps.remove( m.stamp.g_serial ) ;
	}
	
	public void trimView( int cutoff) {
		view.trim( cutoff ) ;
		if( sview != null)
			sview.trim( cutoff ) ;
	}
	
	public void append(DbUploadFilters m) {
		neededFilterPieces &= ~m.piece ;		
		loadedFilters = (neededFilterPieces == 0);		
	}

	public int nextFilterPiece() {
		return Integer.lowestOneBit( neededFilterPieces ) ;
	}
	
	void append(DbUploadEndpoints m) {
		view = m.view;

		if( sview != null )
			sview = view.toStampView() ;
		
//		deadNodes = m.deadNodes;
		knownNodes = m.knownNodes;
		loadedEndpoints = true;
		
		if (owner.index == 0 ) {
			loadedFilters = true;
			neededFilterPieces = 0;
		}
	}

	void store(Collection<Integer> joins, Collection<Integer> rejoins) {
		for (int i : joins)
			knownNodes.set(i);

		for (int i : rejoins)
			knownNodes.set(i);
	}

	void store(Collection<Integer> joins, Collection<Integer> rejoins, Collection<Integer> failed) {
		for (int i : joins)
			knownNodes.set(i);

		for (int i : rejoins)
			knownNodes.set(i);
		
//		for( int i : failed) 
//			deadNodes.set(i);
	}

	final void accountDeadNode( CatadupaNode n ) {
		if( loadedEndpoints ) {
			knownNodes.set( n.index) ;
//			deadNodes.set( n.offline_index ) ;
		}
	}
		
	CatadupaNode randomSeedNode(long ignoreKey) {
		int N = GlobalDB.nodes.size();
		int randomIndex = Simulation.rg.nextInt(N);
		for (CatadupaNode i : GlobalDB.nodes.subList(randomIndex, N))
			if (i.key != ignoreKey && i.state.joined)
				return i;

		for (CatadupaNode i : GlobalDB.nodes.subList(0, randomIndex))
			if (i.key != ignoreKey && i.state.joined)
				return i;

		return owner;
	}

	CatadupaNode randomNode() {
		int N = GlobalDB.nodes.size();
		int randomIndex = Simulation.rg.nextInt(N);
		for (CatadupaNode i : GlobalDB.nodes.subList(randomIndex, N))
			if (i.key != owner.key && i.state.joined && knownNodes.contains(i.index))
				return i;

		for (CatadupaNode i : GlobalDB.nodes.subList(0, randomIndex))
			if (i.key != owner.key && i.state.joined && knownNodes.contains(i.index))
				return i;

		return null;
	}

	CatadupaNode randomSucessor( int max ) {
		int skip = 1 + Simulation.rg.nextInt( max ) ;		
		for( CatadupaNode i : nodes( (owner.key + 1L) & GlobalDB.MAX_KEY, (owner.key - 1L) & GlobalDB.MAX_KEY))
			if( skip-- < 0 ) 
				return i ;
		
		return null ;
	}
	
	RandomList<CatadupaNode> randomSucessors( int max ) {
		RandomList<CatadupaNode> res = new RandomList<CatadupaNode>() ;
		
		for( CatadupaNode i : nodes( (owner.key + 1L) & GlobalDB.MAX_KEY, (owner.key - 1L) & GlobalDB.MAX_KEY))
			if( max-- > 0 ) 
				res.add(i) ;
			else 
				return res ;
		
		return res ;
	}
	// -------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------
	public CatadupaNode sequencerFor(long key) {
		return sequencerFor(true, key);
	}

	private CatadupaNode sequencerFor(boolean excludeSelf, long key) {
		long sequencerKey = GlobalDB.key2slice(key) * GlobalDB.SLICE_WIDTH;
		for (CatadupaNode candidate : nodes(sequencerKey, (sequencerKey -1L) & GlobalDB.MAX_KEY ))
			if (excludeSelf && candidate == owner)
				continue;
			else
				return candidate;

		return owner;
	}

	// -------------------------------------------------------------------------------
	public Iterable<CatadupaNode> nodes(long L, long H) {
		try {
			Iterator<CatadupaNode> it;
			if (L <= H)
				it = GlobalDB.k2an.subMap(L, H + 1L).values().iterator();
			else {
				Iterator<CatadupaNode> first = GlobalDB.k2an.subMap(L, KEY_RANGE).values().iterator();
				Iterator<CatadupaNode> second = GlobalDB.k2an.subMap(0L, H + 1L).values().iterator();
				it = new AppendIterator<CatadupaNode>(first, second);
			}
			return maskingCollection(it);
		} catch (Exception x) {
			System.out.println(L + "/" + H);
			throw new RuntimeException(x.getMessage());
		}
	}

	public Iterable<CatadupaNode> nodes(long fromKey) {
		Iterator<CatadupaNode> first = GlobalDB.k2an.subMap(fromKey, KEY_RANGE).values().iterator();
		Iterator<CatadupaNode> second = GlobalDB.k2an.subMap(0L, fromKey + 1L).values().iterator();
		return maskingCollection(new AppendIterator<CatadupaNode>(first, second));
	}

	protected MaskingCollectionIterator maskingCollection(Iterator<CatadupaNode> it) {
		return new MaskingCollectionIterator(it);
	}

	class MaskingCollectionIterator implements Iterator<CatadupaNode>, Iterable<CatadupaNode> {

		CatadupaNode next = null;
		Iterator<CatadupaNode> it;

		MaskingCollectionIterator(Iterator<CatadupaNode> it) {
			this.it = it;
		}

		public boolean hasNext() {
			while (it.hasNext()) {
				next = it.next();
				if (knownNodes.get(next.index) ) // && ! deadNodes.get( next.offline_index)); 
					return true;
			}
			return false;
		}

		public CatadupaNode next() {
			assert knownNodes.get(next.index) == true;
			return next;
		}

		public void remove() {
			throw new RuntimeException("Not implemented...");
		}

		public Iterator<CatadupaNode> iterator() {
			return this;
		}
	}
}

class AppendIterator<T> implements Iterator<T> {

	Iterator<T> curr, first, second;

	AppendIterator(Iterator<T> a, Iterator<T> b) {
		curr = first = a;
		second = b;
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
}