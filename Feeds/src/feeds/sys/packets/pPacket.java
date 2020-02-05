package feeds.sys.packets;

import java.io.* ;

import feeds.api.* ;
import feeds.sys.core.* ;

public final class pPacket<E,P> extends cPacket implements Cloneable, Receipt, Payload<P> {
    

	@SuppressWarnings("unchecked")
	public pPacket(ID channel, ID src, Object envelope, Object data) {
        super( channel, P_PACKET ) ;
        this.src = src ;
        this.payload = new PayloadPacket( envelope, data ) ;
    }
    
    @SuppressWarnings("unchecked")
	protected pPacket( Packet p ) throws IOException {
        super( p ) ;        
        this.src = dec.readID() ;
        this.payload = new PayloadPacket( this ) ;
    }
    
    protected void encode() throws IOException {
        super.encode() ;
        src.writeTo( enc ) ;
        this.payload.writeTo( enc ) ;
        enc.flush();
    }
        
    public Object target() {
        return target ;
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
    
    public P data() throws FeedsException {
        return payload.data() ;
    }
    
    @SuppressWarnings("unchecked")
	public E envelope() throws FeedsException {
        return payload.envelope() ;
    }

    public void setTarget( Object to ) {
        target = to ;
    }
    
    public void setHandle( Subscription h ) {
        handle = h ;
    }
     
    @SuppressWarnings("unchecked")
	final public void route( Router r ) throws Exception {
        r.pRoute( this ) ;
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
    /*****************************************************************/
    /* encoded data fields                                           */
    public ID src ;
    public Object target ;
    public PayloadPacket<E, P> payload ;
    
    /*****************************************************************/
    /* for implementing Receipt                                     */
    private Subscription handle ;
    
	private static final long serialVersionUID = 1L;
}
