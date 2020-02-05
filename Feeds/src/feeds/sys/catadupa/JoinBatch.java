package feeds.sys.catadupa;

import java.io.*;
import java.util.*;

/**
 *
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 */
public class JoinBatch implements Serializable {
    	
	public gView view ;
	public Stamp stamp ;
	public Collection<Node> nodes ;

    public JoinBatch( Stamp stamp, gView view, Collection<Node> ns  ) {
        this.stamp = stamp ;
        this.view = view.clone() ;
        this.nodes = new ArrayList<Node>() ;
        for( Node i : ns )
        	this.nodes.add( i.clone() ) ;
    }
     
    private JoinBatch( JoinBatch other ) {
    	this( other.stamp, other.view, other.nodes) ;
    }
    
    public JoinBatch clone() {
    	return new JoinBatch( this ) ;
    }
        
    public String toString() {
    	return String.format("|%s %s %s|", stamp, view, nodes) ;
    }
    
    public boolean containsNewer( Node n ) {
    	for( Node i : nodes )
    		if( i.key.equals( n.key ) && i.data.newer( n.data ) ) 
    			return true ;

    	return false ;
    }
    
	private static final long serialVersionUID = 1L;
}
