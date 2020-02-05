package feeds.api;

import java.io.* ;
import java.util.* ;
/** Criteria objects govern the behavior of subscription operations.
 *
 * They are used internally to determine which events should be delivered to the event subscribers.
 * Criteria objects filter the incoming events based on their respective envelopes.
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * @version 2.0
 */
@SuppressWarnings("serial")
public class Criteria<E> implements Serializable {
    
    /** Tests the envelope parameter against the encoded criteria.
     * Returns true if the event meets the criteria and false otherwise.
     * @param e Envelope of the event to be tested against this criteria.
     * @return <B>true</B> if the event meets the criteria and <B>false</B> otherwise.
     */    
    public boolean accepts( E e ) {
        return true ;
    }
        
    /** Takes a Collection of Criteria objects and
     * computes a new one that approximates the disjunction of the
     * input. In most cases, the resulting object will be a coarser filter.
     * @param e
     * @return 
     */    
    public Criteria<E> simplify( Collection<Criteria<E>> c ) {
        return new Criteria<E>() ;
    }
    
    /** Compares this to the criteria parameter to determine which is the weaker.
     * A weaker criteria will accept a wider range of envelopes. An equal criteria will accept exactly 
     * the same range of envelopes. The stronger will be more restrictive of the two.
     * Two criteria are not comparable if the sets of envelopes that each accepts are disjoint.
     * @param c Criteria to compare to.
     * @return -1 if c is a stronger. 0 if c is equivalent. 1 if c is weaker. 2 if they are not comparable
     */
    public int compareTo( Criteria<E> c ) {    
        return 2 ;
    }
    
    public String toString() {
        return "<*>" ;
    }
}
