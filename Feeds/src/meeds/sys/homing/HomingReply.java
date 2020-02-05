package meeds.sys.homing;

import java.io.* ;


import feeds.sys.* ;
import feeds.sys.core.* ;
import meeds.sys.proxying.*;

public class HomingReply implements Serializable, Comparable<HomingReply> {
    
    public HomingReply( double timeStamp, float keepAlivePeriod, String transferURLs, ProxyInfo proxy ) {
    	this.proxy = proxy ;
        this.timeStamp = timeStamp ;
        this.transferURLs = transferURLs ;
        this.keepAlivePeriod = keepAlivePeriod ;
    }
    
    public ID src() {
        return src ;
    }
    
    public float rtt() {
        return rtt ;
    }
    
    public String urls() {
        return transferURLs ;
    }
    
    public ProxyInfo proxy() {
        return proxy ;
    }
    
    public double keepAlivePeriod() {
        return keepAlivePeriod ;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {    	
        src.writeTo( out ) ;
        out.writeDouble( timeStamp ) ;
        out.writeUTF( transferURLs ) ;
        out.writeFloat( keepAlivePeriod ) ;
        out.writeObject( proxy ) ;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        src = new ID( in ) ;
        timeStamp = in.readDouble() ;
        transferURLs = in.readUTF() ;
        keepAlivePeriod = in.readFloat() ;
        rtt = (float)( FeedsNode.time() - timeStamp + 0.001 ) ;
        proxy = (ProxyInfo) in.readObject() ;
    }
    
    public String toString() {
        return src + "<" + rtt + "/" + transferURLs + ">";
    }
    
    
    public boolean equals( HomingReply other ) {
    	return other == this || src.equals( other.src ) ;
    }
    
    public boolean equals( Object other ) {
        return other == this || equals( (HomingReply) other ) ;
    }

    public int hashCode() {
        return src.hashCode() ;
    }
    
    public int compareTo( HomingReply other ) {
        return this.rtt == other.rtt ? src.compareTo( other.src ) : (this.rtt < other.rtt ? -1 : 1 ) ;
    }
    
    float rtt ;
    ProxyInfo proxy ;
    double timeStamp ;
    float keepAlivePeriod ;
    ID src = FeedsNode.id() ;
    String transferURLs ;
    
	private static final long serialVersionUID = 1L;
}