package livefeeds.sift0;

import java.util.Iterator;

import simsim.utils.AppendIterator;

import livefeeds.sift0.config.Config;
import livefeeds.sift0.msgs.SliceCastPayload;

import static livefeeds.sift0.config.Config.*;

public class SliceDB {

	final CatadupaNode owner;

	public View sliceView;
	
	final private int slice ;
	private int c_serial = -1, p_serial = -1, m_serial = -1;
	private static int[] g_serial = new int[ 1 << Config.Config.SLICE_AGGREGATION_DEPTH ] ;
	
	protected SliceDB(CatadupaNode owner) {
		this.owner = owner;
		this.sliceView = new View(owner.key);						
		this.slice = slice( Config.Config.SLICE_AGGREGATION_DEPTH ) ;
	}

	public Stamp nextStamp() {
		p_serial = c_serial;
		c_serial = sliceView.maxSerial() + 1;
		Stamp res = new Stamp(owner.key, c_serial, p_serial, g_serial[slice]++);
		return res ;
	}
	
	public Range sliceRange() {
		long sliceWidth = (GlobalDB.MAX_KEY + 1L) / (1 << Config.SLICE_AGGREGATION_DEPTH ) ;
		long L = (owner.key / sliceWidth ) * sliceWidth;
		return new Range( L, L + sliceWidth ) ;
	}
	
	public boolean isEmpty() {
		return GlobalDB.size() < 2;
	}

	public void store( SliceCastPayload m ) {
	}
	
	public Range range() {
		return new Range() ;
	}
	
	private int slice(int level) {
		long sliceWidth = (GlobalDB.MAX_KEY + 1L) / (1 << level ) ;
		return (int)(owner.key / sliceWidth) ;
	}

}