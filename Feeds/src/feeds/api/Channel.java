package feeds.api;

/** Allows applications to interact with the associated channel.
 *
 * This interface mostly aggregates various, specific interfaces, which provide the actual functionalities.
 *
 * @author S�rgio Duarte (smd@di.fct.unl.pt)
 * @version 1.0
 */
public interface Channel<E, P, F, Q> extends Publication<E, P>, Feedback<F, Q> {
	
	/** Returns the binary identifier associated with this channel object.
	 * 
	 * @return the id of the channel.
    **/
	public Object id() ;    
   
	/** Returns the human readable string identifier associated with this channel object.
     *
     * @return the name of the channel.
     **/ 
    public String name() ;
    
    /** Releases the resources in use by this channel object and
     * prepares it for garbage collection.
     **/
    public void dispose() ;

    /** Sends the event back to the routing engine.
     *
     * @param r Receipt that identifies the event that should be re-routed.
     * @throws FeedsException If the event cannot be reRouted for some reason.
     */
    public void reRoute( Receipt r ) throws FeedsException ;
    
    public void setOutputRate( double Bps, boolean blocking ) ;
    
    public double getOutputRateDelay() ;
}

