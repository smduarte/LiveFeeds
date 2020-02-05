package feeds.api;

public interface Subscriber<E,P> {
  
    /** Invoked when an event matching the subscription criteria is received.
     *
     * Due to internal sharing, any changes made the parameters passed by this method
     * will have side-effects.
     *
     * This method should return as soon as possible.
     * @param r Receipt of the notification.
     * @param e Envelope of the notification.
     * @param mev Marshalled form of the notification payload data.
     * 
     * @author SŽrgio Duarte (smd@di.fct.unl.pt)
     * * @version 1.0
     */    
  public void notify( final Receipt r, final E e, final Payload<P> p ) ;
}
