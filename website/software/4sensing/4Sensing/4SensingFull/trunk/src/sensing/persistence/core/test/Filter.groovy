package sensing.persistence.core.test;

import org.codehaus.groovy.runtime.InvokerHelper;
import groovy.lang.GroovyInterceptable;

class Filter implements GroovyInterceptable {
	def last;
	def input;


	Filter() {
	}

	boolean increase() {
		(!last || input > last)
	}
	
	def invokeMethod(String name, args) {
		def metaClass = InvokerHelper.getMetaClass(this);
		if(name == "process") {
			input = args[0];
			// if filter evaluates to true, propagate the input value
			if(metaClass.invokeMethod(this, name, args)) {
				last = input;
				return input;
			} 
		} else {
			return metaClass.invokeMethod(this, name, args);
		}
	}
}
