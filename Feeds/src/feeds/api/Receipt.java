package feeds.api;


/** pReceipt objects are returned by event operations and have the purpose
 * of aggregating information that is produced or gathered at the system-level, such as event source identifiers or sequence numbers.
 *
 *
 * @author SŽrgio Duarte (smd@di.fct.unl.pt)
 * @version 1.0
 */
public interface Receipt {
    
    /** Returns a reference to the envelope of the event that produced this Receipt object.
     * @return a reference to the envelope of the event that produced this Receipt object.
     */    
    public <E> E envelope() ;

    /** Returns a reference to the event payload associated with this Receipt object.
     * @return a reference to the event payload associated this Receipt object.
     */
    public <P> Payload<P> payload() ;
    
    /** Returns a reference to the binary name of the event channel that produced this Receipt object.
     * @return a reference to the binary name of the event channel that produced this Receipt object.
     */
    public Object channel() ;
    
    /** Determines if the event was produced locally within a server.
     * @return <B>true</B> if the event was produced locally within a server, <B>false</B> otherwise.
     */
    public boolean isLocal() ;
    
    /** Returns a reference to an object that identifies the target of the event that produced this Receipt object.
     * @return a reference to an object that identifies the target of the event that produced this Receipt object.
     */
    public Object target() ;
    
    /** Returns a reference to an object that identifies the source of the event that produced this Receipt object.
     * @return a reference to an object that identifies the source of the event that produced this Receipt object.
     */
    public Object source() ;
    
    /** Returns a reference to the handle object that identifies the subscription that produced the notification of this Receipt object.
     * @return a reference to the handle object that identifies the subscription that produced the notification of this Receipt object. <B>null</B> is returned
     * if the receipt object was returned by a <B>publish</B> or <B>feedback</B> operation.
     */
    public Subscription subscription() ;
    
    /** Returns the length in bytes of the encoded event including system-level headers.
     * @return the length in bytes of the encoded event including system-level headers.
     */
    public int packetSize() throws FeedsException ;  
}
