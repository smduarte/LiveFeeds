/*
 * Publication.java
 *
 * Created on 26 de Setembro de 2000, 17:30
 */

package feeds.api;

/** Interface for publishing events.
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version 1.0
 */
public interface Publication<E, P> {

    /** Publishes an event into this channel.
     * @param p Object corresponding to the main part of the event. The envelope is null.
     * @throws FeedsException if an error is detected, such as serious communication error
     * occurs.
     * @return a Receipt object with some system-collected information about the newly
     * published event, such as its encoded length in bytes or its source sequence number.
     */        
    public Receipt publish( P p ) throws FeedsException ;
    
    /** Publishes an event into this channel.
     * @param e Envelope of the event being published.
     * @param p Object corresponding to the main part of the event.
     * @throws FeedsException if an error is detected, such as serious communication error
     * occurs.
     * @return a Receipt object with some system-collected information about the newly
     * published event, such as its encoded length in bytes or its source sequence number.
     */    
    public Receipt publish( E e, P p ) throws FeedsException ;
    
    /** Adds a new subscriber to the subscribers list of this channel.
     * 
     * A criteria object will be used to select which of the incoming events
     * will be delivered to this particular subscriber.
     * @param c Criteria object that will filter incoming events.
     * @param l Object that will be notified of the incoming events that meet the
     * supplied criteria.
     * @returns Handle identifying the subscription, needed to cancel the subscription.
     * @throws FeedsException if the subscription parameters are invalid.
     */
    public Subscription subscribe( Criteria<E> c, Subscriber<E,P> l ) throws FeedsException ;
 
    
    /** Adds a new subscriber to the subscribers list of this channel.
     * 
     * The incoming events will be delivered to this particular subscriber of their envelope has an envelope equal to the one provided.
     * @param e Envelope object that will filter incoming events.
     * @param l Object that will be notified of the incoming events that meet the
     * supplied criteria.
     * @returns Handle identifying the subscription, needed to cancel the subscription.
     * @throws FeedsException if the subscription parameters are invalid.
     */
    public Subscription subscribe( E e, Subscriber<E,P> l ) throws FeedsException ;
 
    
    /** Adds a new subscriber to the subscribers list of this channel.
    *
    * No filtering is performed.
    * 
    * @param l Object that will be notified of the incoming events.
     * @returns Handle identifying the subscription, needed for unsubscriptions.
    * @throws FeedsException if the subscription parameters are invalid.
    */
   public Subscription subscribe( Subscriber<E,P> l ) throws FeedsException ;
}
