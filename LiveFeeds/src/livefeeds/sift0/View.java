package livefeeds.sift0;

import static livefeeds.sift0.config.Config.Config;
import static simsim.logging.Log.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import livefeeds.sift0.msgs.CatadupaCastPayload;

import simsim.core.PeriodicTask;
import simsim.utils.Pair;

import umontreal.iro.lecuyer.stat.Tally;

public class View implements Serializable {

	long owner;
	
	int max_cSerial;
	public int cutoff;
	public SlidingBitSet stamps ;
		
	public static View GV = new View(-1L);

	public View(long key) {
		owner = key;
		max_cSerial = cutoff = -1 ;
		stamps = new SlidingBitSet() ;
	}

	public View(long key, int co ) {
		owner = key;
		cutoff = co;
		max_cSerial = cutoff = -1 ;
		stamps = new SlidingBitSet() ;
	}
	
	private View(View other) {
		owner = other.owner;
		cutoff = other.cutoff;
		stamps = other.stamps.clone() ;
		max_cSerial = other.max_cSerial ;
	}
	
	public View clone() {
		return new View(this);
	}
	
	public boolean add(Stamp s) {
		if( s.c_serial > max_cSerial )
			max_cSerial = s.c_serial ;		
		return stamps.set( s.g_serial ) ;
	}
	
	public Pair<List<Integer>, List<CatadupaCastPayload>> differences( View other, int max ) {
		Pair<List<Integer>, List<Integer>> diffs = this.stamps.mutualDifference(other.stamps) ;

		ArrayList<CatadupaCastPayload> have = new ArrayList<CatadupaCastPayload>();
		for( Integer i : diffs.first  ) {
			if( have.size() < max ) {
				CatadupaCastPayload x = ArrivalsDB.get(i) ;
				if( x != null && x.stamp.c_serial > other.cutoff)
					have.add(x) ;
			}
			else 
				break ;
		}		
		return new Pair<List<Integer>, List<CatadupaCastPayload>>( diffs.second, have) ;
	}

	
	public String toString() {
		return String.format("%5d >>>>> %s", cutoff, stamps.toString());
	}

	public int size() {
		return 0 ;
	}

	public int length() {
		return meanLength ;
	}

	public boolean contains( Stamp stamp ) {
		return stamps.get( stamp.g_serial ) ;
	}
	
	public boolean contains( View other ) {
		return stamps.contains(other.stamps ) ;
	}
	
	public View trim( int cut ) {
		cut = max_cSerial - cut ;
		if( cut > cutoff && cut < stamps.base)
			cutoff = cut ;
		
		return this;
	}
		
	public int maxSerial() {
		return max_cSerial ;
	}
		
	public int holes(View other) {
		return other.stamps.difference( this.stamps).size() ;
	}
	
	public StampView toStampView() {
		StampView res = new StampView(owner) ;
		for( int i = stamps.base ; i <= stamps.top ; i++ ) {
			if( stamps.get(i) ) {
				CatadupaCastPayload x = ArrivalsDB.get(i) ;
				if( x == null )
					System.err.printf("---------------------------->Base: %s i: %s  Top:%s --->%s\n", stamps.base, i, stamps.top, ArrivalsDB.arrivals.keySet() );
				if( x.stamp.c_serial > cutoff )
					res.add(x.stamp) ;
			}
		}
		return res.trim( this.cutoff ) ;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	static int meanLength = 64 ;
	
	public static void init() {
		new PeriodicTask(120) {
			public void run() {
				View.GV.trim(Config.VIEW_CUTOFF);
				Tally m = new Tally() ;
				for( CatadupaNode i : GlobalDB.liveNodes() )
					if( i.state.joined && i.state.db.sview != null )
						m.add( i.state.db.sview.trim( Config.VIEW_CUTOFF ).length() ) ;
				
				if( m.numberObs() > 2 ) {
					meanLength = (int)(0.9 * meanLength + 0.1 * m.average()) ;		
					Log.fine(String.format("#samples: %d, average view length: %.0f [%d] bytes", m.numberObs(), m.average(), meanLength ));
				}
			}
		};
	}
}