package sensing.core;

import groovy.lang.Closure;
import sensing.core.query.Query;

class QueryInterface extends Service {
	
	QueryInterface(ServiceManager services) {
		super(services)
	}
	
	public void runQuery(Query q, Closure listener) {
		services.query.runQuery(q, listener);
	}
	
	public void closeQuery(Query q) {
		services.query.closeQuery(q);
	}
	
}
