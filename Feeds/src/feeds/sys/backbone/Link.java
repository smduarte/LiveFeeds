package feeds.sys.backbone;


/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class Link<T extends Comparable<T>> extends simsim.graphs.Link<T> {
    public Link( T v, T w, double cost) {
    	super( v, w, cost ) ;
        this.samples = 1 ;
    }
    
    public int samples ;
    
}