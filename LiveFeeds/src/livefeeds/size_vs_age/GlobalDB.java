package livefeeds.size_vs_age;

import static livefeeds.rtrees.config.Config.Config;
import static simsim.core.Simulation.rg;

import java.util.Collection;
import java.util.TreeMap;

import simsim.utils.RandomList;

public class GlobalDB {

	public static final long MAX_KEY = (1L << Config.NODE_KEY_LENGTH) - 1L;

	public final static RandomList<Node> nodes = new RandomList<Node>() ;
	final static TreeMap<Long, Node> k2on = new TreeMap<Long, Node>();

	public static final long SLICE_WIDTH = (MAX_KEY + 1L) / Config.NUMBER_OF_SLICES;

	public static int key2slice( long key ) {
		return (int)( key / SLICE_WIDTH) ;
	}

	private static int g_index = 0 ;
	
	public static void store(Node nn) {
		if (k2on.size() > MAX_KEY )
			throw new RuntimeException("NODE_KEY_LENGTH too small...");
		for (;;) {
			long key = (rg.nextLong() >>> 1) & MAX_KEY;
			if (!k2on.containsKey(key) ) {
				k2on.put(key, nn);
				nn.key = key ;
				
				nn.index = g_index++ ;
				nn.__storage_index = nodes.size() ;
				nodes.add( nn ) ;
				break;
			}
		}
	}

	
	public static void dispose(Node n) {
		k2on.remove( n.key ) ;
		
		Node replacement = nodes.remove( nodes.size() - 1) ;
		if( replacement != n ) {
			replacement.__storage_index = n.__storage_index ;
			nodes.set( replacement.__storage_index, replacement) ;	
		}
	}
				
	public static int size() {
		return k2on.size();
	}

	static Collection<Node> nodes() {
		return k2on.values() ;
	}		
}
