package feeds.sys.backbone;

import java.io.* ;

import feeds.api.* ;
import feeds.sys.core.* ;

@SuppressWarnings("serial")
public class HelloReport implements Serializable  {
    
    /** Creates new HelloReport */
    public HelloReport( ID src, HelloRequest req) {
        this.src = src ;
        this.timeStamp0 = req.timeStamp ;
        this.timeStamp1 = Feeds.time() ;
    }
    
    public HelloReport( ID src, HelloReport rep) {
        this.src = src ;
        this.timeStamp1 = 0 ;
        this.timeStamp0 = rep.timeStamp1 ;
    }
    
    public HelloReport( ID src, int delay) {
        this.src = src ;
        this.rtt = delay ;
        this.timeStamp1 = -1 ;
        this.timeStamp0 = Feeds.time() ;
    }
    
    public ID src() {
        return src ;
    }
    
    public double rtt() {
        return rtt ;
    }
    
    public double samples() {
        return samples ;
    }

    public double timeStamp() {
        return timeStamp0 ;
    }
    
    public void setData( double rtt, double samples ) {
        this.rtt = rtt ;
        this.samples = samples ;
    }
    
    public String toString() {
        return String.format("HelloReport: %s  ( %3f ms)", src, 1000*rtt ) ;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
    	src.writeTo( out) ;
        out.writeDouble( timeStamp0 ) ;
        out.writeDouble( timeStamp1 ) ;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        src = new ID( in ) ;
        timeStamp0 = in.readDouble() ;
        timeStamp1 = in.readDouble() ;
        rtt = (Feeds.time() - timeStamp0) ;
        samples++ ;
    }
    
    ID src ;
    double timeStamp0 ;
    double timeStamp1 ;
    transient double rtt ;
    transient double samples = 0 ;
}