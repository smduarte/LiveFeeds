package livefeeds.twister6;

import static livefeeds.twister6.config.Config.Config;
import static simsim.core.Simulation.rg;
import static simsim.logging.Log.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.utils.RandomList;

public class GlobalDB {

	public static final long MAX_KEY = (1L << Config.NODE_KEY_LENGTH) - 1L;

	final static LinkedList<CatadupaNode> recentNodes = new LinkedList<CatadupaNode>();
	
	public final static Set<CatadupaNode> deadNodes = new HashSet<CatadupaNode>() ;
	public final static RandomList<CatadupaNode> nodes = new RandomList<CatadupaNode>() ;
	
	final static TreeMap<Long, CatadupaNode> k2an = new TreeMap<Long, CatadupaNode>();

	public static final long SLICE_WIDTH = (MAX_KEY + 1L) / Config.NUMBER_OF_SLICES;

	public static int key2slice( long key ) {
		return (int)( key / SLICE_WIDTH) ;
	}

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

		// Make sure the db KnownNodes and DeadNodes masks/bitsets can slide...
		if( ! n.state.joined )
			for( CatadupaNode i : liveNodes() ) 
				i.state.db.accountDeadNode( n ) ;
	}
	
	public static void gc() {
		
		List<CatadupaNode> collected = new ArrayList<CatadupaNode>() ;

Li:		for( CatadupaNode i : deadNodes() ) {
			for( CatadupaNode j : liveNodes() ) {
				if( j.state.joined && ! j.state.db.deadNodes.get( i.offline_index ) )
					continue Li;
			}
			collected.add( i ) ;
		}
		
		allNodes().removeAll( collected) ;
		deadNodes().removeAll( collected ) ;

		double now = Simulation.currentTime() ;
		for( CatadupaNode i : deadNodes() )
			if( now - i.state.sessionEnd > 10 * Config.VIEW_CUTOFF ) 
				for( CatadupaNode j : liveNodes() )
					j.state.db.accountDeadNode(i) ;
				
		Log.fine(String.format("DB endpoint overhead: %.1f", 100.0 * k2an.size() / size() - 100 ));
	}
				
	public static int size() {
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
	
	@SuppressWarnings("unchecked")
	public static <T> T randomLiveNode() {
		return (T) nodes.randomElement() ;
	}
	
	public static void init() {
		new PeriodicTask(60.0 + rg.nextDouble() ) {
			public void run() {
				gc() ;
			}
		};
	}
}