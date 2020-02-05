package feeds.sys.templates.centradupa;


public class Join_Request extends CentradupaControlPacket {

	final public SubscriptionData sd ;
    
	public Join_Request( SubscriptionData sd ) {
		this.sd = sd ;
	}

    final public void cRoute( ControlPacketRouter<?,?,?,?> cpr ) throws Exception {        
        cpr.cRoute( this ) ;
    }   
    
	private static final long serialVersionUID = 1L;
}
