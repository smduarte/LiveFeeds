package livefeeds.sift0;

import static livefeeds.sift0.stats.Statistics.Statistics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simsim.core.Simulation;

import umontreal.iro.lecuyer.stat.Tally;

abstract public class Event implements Serializable {
	private static final long serialVersionUID = 1L;
	private double _timeStamp = -1 ;

	public final long src ;
	
	public double targets = 0 ;
	public double visited = 0;
	public double candidates = 0 ;
	public double popularity = 0 ;
	
	public double inView = 0, offView = 0 ;
	
//	public SortedSet<Integer> targets = new TreeSet<Integer>() ;
//	public Set<Integer> visited = new TreeSet<Integer>() ;	

//	public List<Integer> inView = new ArrayList<Integer>() ;	
//	public List<Integer> offView = new ArrayList<Integer>() ;	
	
	public Map<CatadupaNode, Long> ops = new HashMap<CatadupaNode, Long>() ;
	
	public Tally evals = new Tally("Tests") ;
	public Tally sends = new Tally("Sends") ;

	public Event( long srcKey ) {
		this.src = srcKey ;
	}
	
	private static int g_serial = 0 ;
	public final int serial = g_serial++;
		
	public void notify( TurmoilNode node ) {

		visited++ ;
		
//		boolean duplicate = duplicate( node.index ) ;
//
//		if (duplicate)
//			Log.severe("\nDuplicate Delivery..." + serial + "/" + node.key + "\n");
//
//		visited.add( node.index ) ;		
	}
	
	public void notify( TurmoilNode node, List<TurmoilNode> path ) {
		notify( node ) ;
	}

	public double elapsed() {
		return Simulation.currentTime() - _timeStamp ;
	}
	
	public void resetStamp() {
		_timeStamp = Simulation.currentTime() ;
	}
	
	Event init() {
		resetStamp() ;

		for( CatadupaNode i : GlobalDB.liveNodes() )
			if( i.state.joined ) {
				candidates++ ;				
				if( i.accepts( this ) ) {
					targets++ ;
//					targets.add( i.index ) ;
					i.state.stats.filter.accepted_events++ ;
				} else
					i.state.stats.filter.rejected_events++ ;
									
			}
		
		popularity = targets / candidates ;		
		return this ;
	}
	
	public boolean duplicate( int index ) {
		return false ;
	}
	
	public void evaluatedBy( CatadupaNode node, CatadupaNode nodeFilter ) {
		Long v = ops.get( node ) ;		
		ops.put( node, v == null ? 1L : v + 1L ) ;
		if( node.key != src ) {
			node.state.stats.filter.filters_evaluated ++ ;
		}
	}
	
	public void forwardedBy( CatadupaNode node ) {
		Long v = ops.get( node ) ;		
		ops.put( node, v == null ? 1L << 32 : v + 1L << 32) ;
		if( node.key != src ) {
			node.state.stats.filter.events_forwarded ++ ;
		}
	}
	
	public void account() {
		
		
		System.out.println(this.serial );
		System.out.println( popularity + "/" + visited );


//		double t = 0, f = 0 ;
		for( Long i : ops.values() ) {
			long l = i & 0xFFFFFFFFL, h = i >> 32 ;
			if( l > 0 ) 
				evals.add( l ) ;
			if( h > 0 )
				sends.add( h ) ;
		}

		if( evals.numberObs() > 1 ) {
			Statistics.evStats.inView_events += inView ;
			Statistics.evStats.offView_events += offView ;
			
			System.out.printf( "OffView/InView: %.0f%%\n", 100.0 * offView / inView  );

			double avgEvals = evals.numberObs() > 1 ? evals.average() : evals.sum() ;
			
			Statistics.evStats.popularity.tally( 100 * popularity, 0) ;
			if( candidates > 0 )
				Statistics.evStats.pop_cpuLoad.tally( 100 * popularity, 100 * avgEvals / candidates ) ;
		} 

		System.out.printf("Popularity:%.1f%% Evaluations<%.0f/%.1f/%.0f> Size:%.0f\n", 100 * popularity, evals.min(), evals.average(), evals.max(), candidates );
		System.out.printf("Popularity:%.1f%%       Sends<%.0f/%.1f/%.0f>\n", 100 * popularity, sends.min(), sends.average(), sends.max() );


//		for( Map.Entry<CatadupaNode, Integer> i : forwards.entrySet() ) {
//			CatadupaNode j = i.getKey() ;	
//			if( j.key != src ) {
//				j.state.stats.filter.events_forwarded += i.getValue() ;
//			}
//		}
//		
//		for( Map.Entry<CatadupaNode, Integer> i : tests.entrySet() ) {
//			CatadupaNode j = i.getKey() ;	
//			if( j.key != src ) {
//				j.state.stats.filter.filters_evaluated += i.getValue() ;
//			}
//		}
		
//		List<Integer> TL = new ArrayList<Integer>() ;
//		Set<Integer> TS = new TreeSet<Integer>() ;
//		for( List<Integer> i : testsList.values() ) {
//			TL.addAll( i ) ;
//			TS.addAll( i ) ;
//		}
		
//		System.out.println( "Targets:      " + targets );
//		System.out.println( "Sorted Visits:" + new TreeSet<Integer>(visited) );
//		System.out.println( "Ordered Visits:" + visited );
//		System.out.println( "TESTS:" + tests );
//		System.out.println( "TESTS LIST:" + testsList );

//		for( Integer i : visited ) {
//			List<Integer> x = testsList.get( i ) ;
//			if( x != null )
//				System.out.println( i + "->" + x + ":" + x.size() + "-" + new TreeSet<Integer>(x).size() ) ;
//		}
//		System.out.printf( "TotalEvals / Nodes %f\n", ET / candidates ) ;
		
		
//		if( visited.size() < targets.size() )
//			
//		System.exit(0) ;

	}
}
