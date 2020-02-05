package feeds.api;

/**
 * @author SŽrgio Duarte (smd@di.fct.unl.pt)
 *
 */
@SuppressWarnings("serial")
public class FeedsException extends RuntimeException {
    
    /** Constructs a DeedsException with no specified diagnostic message.
     */
    public FeedsException() {
        super() ;
    }
    
    /** Constructs a DeedsException with the specified diagnostic message
     * @param diagnostic diagnostic message associated with the exception.
     */
    public FeedsException(String diagnostic) {
        super(diagnostic) ;
    }
}