package feeds.sys.binding;

import java.io.* ;

import feeds.sys.* ;
import feeds.sys.core.* ;

@SuppressWarnings("serial")
public class BindingReply implements Serializable, Comparable<BindingReply> {
    
    public BindingReply( double timeStamp, float keepAlivePeriod, String transferURLs ) {
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
    
    public double keepAlivePeriod() {
        return keepAlivePeriod ;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {    	
        src.writeTo( out ) ;
        out.writeDouble( timeStamp ) ;
        out.writeUTF( transferURLs ) ;
        out.writeFloat( keepAlivePeriod ) ;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        src = new ID( in ) ;
        timeStamp = in.readDouble() ;
        transferURLs = in.readUTF() ;
        keepAlivePeriod = in.readFloat() ;
        rtt = (float)( FeedsNode.time() - timeStamp + 0.001 ) ;
    }
    
    public String toString() {
        return src + "<" + rtt + "/" + transferURLs + ">";
    }
    
    
    public boolean equals( BindingReply other ) {
    	return other == this || src.equals( other.src ) ;
    }
    
    public boolean equals( Object other ) {
        return other == this || equals( (BindingReply) other ) ;
    }

    public int hashCode() {
        return src.hashCode() ;
    }
    
    public int compareTo( BindingReply other ) {
        return this.rtt == other.rtt ? src.compareTo( other.src ) : (this.rtt < other.rtt ? -1 : 1 ) ;
    }
    
    float rtt ;
    double timeStamp ;
    String transferURLs ;
    float keepAlivePeriod ;
    ID src = FeedsNode.id() ;
}