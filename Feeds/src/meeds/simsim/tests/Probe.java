package meeds.simsim.tests;

import feeds.api.Feeds;
import feeds.sys.FeedsNode;
import feeds.sys.core.ID;

public class Probe implements java.io.Serializable {

	final public int seqN ;
	final public ID src = FeedsNode.id() ;
	final public double time = Feeds.time();

	public Probe( int s ) {
		seqN = s ;	
	}

	public String toString() {
		return Integer.toString(seqN) ;
	}
	
	private static final long serialVersionUID = 1L;
}
