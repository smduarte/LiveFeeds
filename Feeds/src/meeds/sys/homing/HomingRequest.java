package meeds.sys.homing;

import java.io.* ;

import feeds.api.*;
import feeds.sys.* ;
import feeds.sys.core.*;

public final class HomingRequest implements Serializable {
    
	public HomingRequest( Position pos, String urls) {
		this.pos = pos ;
        this.urls = urls ;
        this.src = FeedsNode.id() ;
    }
    
    public String urls() {
        return urls ;
    }

    public double timeStamp() {
        return timeStamp ;
    }
    
    public String toString() {
        return String.format("HomingRequest[%s %.1f]", urls, 1000*(Feeds.time() - timeStamp) ); 
    }

    ID src ;
    String urls ;
    Position pos ;
    double timeStamp = FeedsNode.time() ;
    
	private static final long serialVersionUID = 1L;
}