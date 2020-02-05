package meeds.sys.proxying;

import java.io.*;

import meeds.sys.homing.*;

public class ProxyInfo implements Serializable {
	
	public String url ;
	public Position pos ;
	public double radius ;
	
	public ProxyInfo( Position pos, double radius, String url) {
		this.url = url ; this.pos = pos ; this.radius = radius ;
	}

	public String toString() {
		return url ;
	}
	
	public double distanceSq( Position other ) {
		return pos.xy.distanceSq(other.xy) ;
	}
	
	private static final long serialVersionUID = 1L;
}