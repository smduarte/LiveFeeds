package feeds.sys.catadupa.template;

import java.io.*;
import feeds.sys.core.*;

public class CatadupaPayload<E,P> implements Serializable {

	public final ID src ;
	public final P data ;
	public final E envelope ;
	
	CatadupaPayload(ID s, E e, P p ) {
		this.src = s ;
		this.data = p ;
		this.envelope = e ;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
