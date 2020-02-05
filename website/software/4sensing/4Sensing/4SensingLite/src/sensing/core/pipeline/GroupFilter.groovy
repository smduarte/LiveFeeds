package sensing.core.pipeline;

import groovy.lang.Closure;

public class GroupFilter extends GroupProcessor {
	def last;
	
	public GroupFilter(Closure clos) {
		this.metaClass.process = clos.curry({last});
	}
	
}
