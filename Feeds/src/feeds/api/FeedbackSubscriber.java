package feeds.api;


/** The interface for receiving feedback events.
 * @author SŽrgio Duarte (smd@di.fct.unl.pt)
 * @version 1.0
 */


public interface FeedbackSubscriber<E, P> {
    
    /** Invoked when an feedback event matching the feedback subscription criteria is received.
     *
     * Due to internal sharing, any changes made the parameters passed by this method
     * will have side-effects.
     *
     * This method should return as soon as possible.
     * @param r Receipt of the notification.
     * @param e Envelope of the notification.
     * @param p Marshalled form of the notification.
     */
    public void notifyFeedback( final Receipt r, final E e, final Payload<P> p ) ;
}
