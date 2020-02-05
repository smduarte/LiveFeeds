package livefeeds.twister7;

import static livefeeds.twister7.config.Config.Config;
import static simsim.core.Simulation.rg;
import static simsim.logging.Log.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.utils.RandomList;

import umontreal.iro.lecuyer.stat.Tally;

public class GlobalDB {

	public static final long MAX_KEY = (1L << Config.NODE_KEY_LENGTH) - 1L;

	final static LinkedList<CatadupaNode> recentNodes = new LinkedList<CatadupaNode>();
	
	public final static Set<CatadupaNode> deadNodes = new HashSet<CatadupaNode>() ;
	public final static RandomList<CatadupaNode> nodes = new RandomList<CatadupaNode>() ;
	
	final static TreeMap<Long, CatadupaNode> k2an = new TreeMap<Long, CatadupaNode>();

	public static final long SLICE_RANDOM_OFFSET = new Random(100).nextLong() >>> 10 ;

	public static int g_index = 0, g_offline_index = 1 ;
	
	public static void store(CatadupaNode n) {
		if (k2an.size() > MAX_KEY )
			throw new RuntimeException("NODE_KEY_LENGTH too small...");
		
		for (;;) {
			long key = (rg.nextLong() >>> 1) & MAX_KEY;
			if (!k2an.containsKey(key)) {
				k2an.put(key, n) ;
				n.key = key ;
				
				n.index = g_index++ ;
				n.offline_index = -1 ;
				n.__storage_index = nodes.size() ;
				nodes.add( n ) ; // keep it here, so __storage_index gets the correct value...
				break;
			}
		}
	}
			
	public static void dispose(CatadupaNode n) {
		
		deadNodes.add( n ) ;
		
		CatadupaNode replacement = nodes.remove( nodes.size() - 1) ;
		if( replacement != n ) {
			replacement.__storage_index = n.__storage_index ;
			nodes.set( replacement.__storage_index, replacement) ;	
		}
		
		n.offline_index = g_offline_index++ ;

		// Make sure the DB KnownNodes and DeadNodes masks/bitsets can slide...
		if( ! n.state.joined )
			for( CatadupaNode i : liveNodes() ) 
				i.state.db.accountDeadNode( n ) ;
	}
	
	private static double dirtyGC = 0, cleanGC = 0 ;
	private static Tally gcStats = new Tally("GC stats ") ;
	
	public static void gc() { //Garbage collect dead nodes...
		
		double now = Simulation.currentTime() ;

		List<CatadupaNode> collected = new ArrayList<CatadupaNode>() ;

		double N = 0 ; double T = 0.05 * size() ; // GC if more than 95% of the nodes know the dead node...[speedup for evaluating turmoil]
i:		for( CatadupaNode i : deadNodes() ) {
			for( CatadupaNode j : liveNodes() ) {
				if( j.state.joined && ! j.state.db.deadNodes.get( i.offline_index ) &&  N++ > T )
					continue i;
			}
			collected.add( i ) ;
		}

		for( CatadupaNode i : collected ) {
			k2an.remove( i.key ) ;
			deadNodes.remove( i ) ;
		}

		cleanGC += collected.size() ;
		collected.clear() ;
		
		for( CatadupaNode i : deadNodes() )
			if( now - i.state.sessionEnd > Config.VIEW_CUTOFF_WINDOW ) {
				for( CatadupaNode j : liveNodes() )
					j.state.db.accountDeadNode(i) ;
				
				collected.add( i ) ;
			}
		
		dirtyGC += collected.size() ;
		
		if( dirtyGC + cleanGC > 0 )
			gcStats.add( dirtyGC / ( dirtyGC + cleanGC ) ) ;
		
		for( CatadupaNode i : collected ) {
			k2an.remove( i.key ) ;
			deadNodes.remove( i ) ;
		}
		Log.fine(String.format("DB GC clean/dirty:%.0f / %.0f ->%.1f%%", cleanGC, dirtyGC, 100 * gcStats.average() ));
		Log.fine(String.format("DB endpoint overhead: %.1f", 100.0 * k2an.size() / size() - 100 ));
	}
				
	public static int size() {
		return nodes.size();
	}

	public static int deadNodesSize() {
		return nodes.size();
	}

	public static int liveNodesSize() {
		return nodes.size();
	}

	public static Collection<CatadupaNode> liveNodes() {
		return nodes ;
	}		

	public static Collection<CatadupaNode> deadNodes() {
		return deadNodes ;
	}
	
	public static Collection<CatadupaNode> allNodes() {
		return k2an.values();
	}
	
	public static int joinedNodes() {
		int res = 0 ;
		for( CatadupaNode i : liveNodes() )
			if( i.state.joined )
				res++ ;
		return res ;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T randomLiveNode() {
		return (T) nodes.randomElement() ;
	}
	
	public static void init() {
		new PeriodicTask(30.0 + rg.nextDouble() ) {
			public void run() {
				gc() ;
			}
		};
	}
}