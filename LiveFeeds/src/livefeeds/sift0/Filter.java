package livefeeds.sift0;

import java.io.Serializable;
abstract public class Filter implements Serializable {
	
	private static final long serialVersionUID = 1L;
		
	abstract public boolean accepts( Event e ) ;

}
