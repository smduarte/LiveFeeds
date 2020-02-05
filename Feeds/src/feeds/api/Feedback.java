/*
 * FeedbackPublication.java
 *
 * Created on 27 de Setembro de 2000, 3:01
 */
 
package feeds.api;

/** The interface for sending feedback events. These strictly unicast events targetted
 * at the source of a previously received event.
 * @author S�rgio Duarte (smd@di.fct.unl.pt)
 * @version 1.0
 */
public interface Feedback<E, P> {
  
    /** Sends a feedback event back to the source of a previously received event.
     * @param r Receipt of the event that triggered this feedback operation and
     * that identifies the receiver of event being sent.
     * @param e Envelope of the feedback event.
     * @param s Object that makes up the main part of the feedback event.
     * @throws FeedsException if an error is detected, such as serious communication error
     * occurs or serialization fails.
     * @return a Receipt object with some system-collected information about the newly
     * published event, such as its encoded length in bytes or its source sequence number.
     */    
  public Receipt feedback( Receipt r, E envelope, P data ) throws FeedsException ;

  public Receipt feedback( Receipt r, P data ) throws FeedsException ;

  /** Adds a new subscriber to the feedback subscribers list of this channel.
  *
  * The subscription is identified by an handback object, needed
  * for later removal of the subscription.
  *
  * A criteria object will be used to select which of the incoming events
  * will be delivered to this particular subscriber.
  * @param c Criteria object that will filter incoming events.
  * @param handback Object that will identify this subscription internally.
  * @param l Object that will be notified of the incoming events that meet the
  * criteria.
  * @throws FeedsException if the subscription parameters are invalid.
  */
 public Subscription subscribeFeedback( Criteria<E> c, FeedbackSubscriber<E, P> l ) throws FeedsException ;

 
 public Subscription subscribeFeedback( E e, FeedbackSubscriber<E, P> l ) throws FeedsException ;

 
 public Subscription subscribeFeedback( FeedbackSubscriber<E, P> l ) throws FeedsException ;

}
