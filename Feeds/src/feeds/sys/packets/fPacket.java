package feeds.sys.packets;

import java.io.* ;

import feeds.api.* ;
import feeds.sys.core.* ;

public final class fPacket<E, P> extends cPacket implements Cloneable, Receipt, Payload<P> {
    
    @SuppressWarnings("unchecked")
	public fPacket(ID channel, ID src, ID dst, Object envelope, Object event) {
        super( channel, F_PACKET ) ;
        this.src = src ;
        this.dst = dst ;
        this.payload = new PayloadPacket( envelope, event ) ;
    }
    
    @SuppressWarnings("unchecked")
	protected fPacket( Packet p ) throws IOException {
        super( p ) ;        
        this.src = dec.readID() ;
        this.dst = dec.readID() ;
        this.payload = new PayloadPacket( this ) ;
    }
    
    protected void encode() throws IOException {
        super.encode() ;
        enc.writeID( src ) ;
        enc.writeID( dst ) ;
        this.payload.writeTo( enc ) ;
        enc.flush() ;
    }
      
    public Object target() {
        return dst ;
    }
    
    public Object source() {
        return src ;
    }
    
    @SuppressWarnings("unchecked")
	public Payload<P> payload() {
        return payload ;
    }
    
    public Subscription subscription() {
        return handle ;
    }
    
    public void setHandle( Subscription h ) {
        handle = h ;
    }
    
    public P data() throws FeedsException {
        return payload.data() ;
    }
    
    @SuppressWarnings("unchecked")
	public E envelope() throws FeedsException {
        return payload.envelope() ;
    }
  
    public String toString() {
        try {
        	Object e = envelope(), p = data() ;
        	return String.format("@%s + <%s,%s> - [%s, %s]", src, e == null ? "?" : e.getClass(), p == null ? "?" : p.getClass(), e, p ) ;
        } catch( Exception x ) {
        	x.printStackTrace() ;
        }
        return "<?>" ;
    }
    
    @SuppressWarnings("unchecked")
	final public void route( Router r ) throws Exception {
        r.fRoute( this ) ;
    }

    /*****************************************************************/
    /* encoded data fields                                           */
    public ID src ;
    public ID dst ;    
    public PayloadPacket<E, P> payload ;
    
    /*****************************************************************/
    /* for implementing Receipt                                     */
    private Subscription handle ;
    private static final long serialVersionUID = 1L;
}
