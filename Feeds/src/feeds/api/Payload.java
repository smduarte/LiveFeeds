package feeds.api;

/** Holds an event payload in marshalled form until needed. This wrapper is intended, primarily, to avoid performing
 * an expensive unmarshalling operation in those cases that particular events are not needed.
 *
 * @author SŽrgio Duarte (smd@di.fct.unl.pt)
 * @version 2.0
 */
public interface Payload<P> {
        
    /** Returns a reference to the decoded event. The event payload data is shared, so modification
     * will have side-effects. Unmarshalling occurs the first time this method is called.
     * @return A reference to the decoded event.
     * @throws FeedsException if unmarshalling of the payload event fails for some reason, such as the event class is unknown.
     */
    public P data() throws FeedsException ;
}
