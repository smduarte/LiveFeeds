package livefeeds.twister7;

import static livefeeds.twister7.config.Config.Config;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import livefeeds.twister7.msgs.CatadupaCastPayload;
import livefeeds.twister7.msgs.DbUploadEndpoints;
import livefeeds.twister7.msgs.DbUploadFilters;

import simsim.core.Simulation;
import simsim.utils.RandomList;

public class DB {

	private static final long KEY_RANGE = (1L << Config.NODE_KEY_LENGTH);
	private static final long MAX_KEY = KEY_RANGE - 1L ;
	
	final CatadupaNode owner;

	public View view;
	public StampView sview ;
	
	private int c_serial = -1, p_serial = -1, m_serial = -1;

	public SlidingBitSet knownNodes, deadNodes;

	public long neededFilterPieces ;
	public boolean loadedEndpoints, loadedFilters ;

	TreeSet<Integer> missingStamps = new TreeSet<Integer>();
	
	protected DB(CatadupaNode owner) {
		this.owner = owner;
		
		this.view = new View(owner.key);
		this.deadNodes = new SlidingBitSet( (int)( 2 * Config.AVERAGE_ARRIVAL_RATE * Config.VIEW_CUTOFF_WINDOW ) ); // slow and clean, to eval catadupa...
//		this.deadNodes = new SlidingBitSet( 4 * 64  ); // quick and dirty to eval turmoil...
		this.deadNodes.set(0) ;
		
		this.knownNodes = new SlidingBitSet();
		sview = Simulation.rg.nextDouble() < 0.99 ? null : new StampView( owner.key ) ; 
		
		
		this.loadedEndpoints = false ;
		this.neededFilterPieces = (1L << Config.DB_FILTER_DOWNLOAD_PIECES) - 1 ;
		this.loadedFilters = (neededFilterPieces == 0) ;
	}

	public Stamp nextStamp() {
		p_serial = c_serial;
		c_serial = view.maxSerial() + 1;
		Stamp res = new Stamp(owner.key, c_serial, p_serial);
		
		View.GV.add(res);
		return res ;
	}
	
	public boolean isEmpty() {
		return GlobalDB.size() < 2;
	}

	public void store( CatadupaCastPayload m ) {

		storeDeadNodes( m.failures ) ;
		
		if( m.stamp != null ) {
			m_serial = Math.max(m_serial, m.stamp.c_serial) + 1;

			assert loadedEndpoints ;
			
			view.add(m.stamp);
			
			storeFreshNodes( m.joins, m.rejoins ) ;
			
			if( sview != null )
				sview.add(m.stamp) ;
			
			missingStamps.remove( m.stamp.g_serial ) ;			
		}
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

	public long nextFilterPiece() {
		return Long.lowestOneBit( neededFilterPieces ) ;
	}
	
	void append(DbUploadEndpoints m) {
		view = m.view;

		if( sview != null )
			sview = view.toStampView() ;
		
		deadNodes = m.deadNodes;
		knownNodes = m.knownNodes;
		loadedEndpoints = true;
		
		if (owner.index == 0 ) {
			loadedFilters = true;
			neededFilterPieces = 0;
		}
	}

	void storeFreshNodes( Collection<Integer> joins, Collection<Integer> rejoins ) {
		for (int i : joins)
			knownNodes.set(i);

		for (int i : rejoins)
			knownNodes.set(i);
		
	}
	
	void storeDeadNodes( Collection<Integer> departures ) {
		for( int i : departures ) 
			deadNodes.set(i);
		
	}

	final void accountDeadNode( CatadupaNode n ) {
		if( loadedEndpoints ) {
			knownNodes.set( n.index) ;
			deadNodes.set( n.offline_index ) ;
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
		return sequencerFor(0, excludeSelf, key) ;
	}

	// -------------------------------------------------------------------------------
	// -------------------------------------------------------------------------------
	public CatadupaNode sequencerFor(int level, long key) {
		return sequencerFor(level, true, key);
	}

	private CatadupaNode sequencerFor(int level, boolean excludeSelf, long key) {
		long sliceWidth = KEY_RANGE / (1 << level) ;
		
		long levelOffset = ((level + 1) * GlobalDB.SLICE_RANDOM_OFFSET) & MAX_KEY ;
		
		long slice = ((key - levelOffset) & MAX_KEY) / sliceWidth ;
			
		long sliceLKey = (slice * sliceWidth + levelOffset) & MAX_KEY ;
		long sliceHKey = (sliceLKey + sliceWidth - 1L) & MAX_KEY ;

//		System.out.printf( "level:%s sliceWidth:%s offset: %s key: %s -> slice:%s-->[%s -> %s]\n", level, sliceWidth, levelOffset, key, slice, sliceLKey, sliceHKey );

		for (CatadupaNode candidate : nodes(sliceLKey, sliceHKey))
			if (excludeSelf && candidate == owner)
				continue;
			else
				return candidate;
		return owner;
	}
	
	private CatadupaNode sequencerFor2(int level, boolean excludeSelf, long key) {
		//sequencerFor2(level, excludeSelf, key) ;
		
		long sliceWidth = (GlobalDB.MAX_KEY + 1L) / (1 << level ) ;

		long sliceLKey = (key / sliceWidth ) * sliceWidth;
		long sliceHKey = sliceLKey + sliceWidth ;
		long offsetKey = sliceLKey + ((level + 1)* GlobalDB.SLICE_RANDOM_OFFSET) % sliceWidth ;
		for (CatadupaNode candidate : nodes(offsetKey, sliceHKey))
			if (excludeSelf && candidate == owner)
				continue;
			else
				return candidate;

		for (CatadupaNode candidate : nodes(sliceLKey, offsetKey))
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
				if (knownNodes.get(next.index) && ! deadNodes.get( next.offline_index))
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