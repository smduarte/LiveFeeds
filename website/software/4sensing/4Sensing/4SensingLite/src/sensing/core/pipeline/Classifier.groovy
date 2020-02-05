package sensing.core.pipeline;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.InvokerHelper;
import static sensing.core.logging.LoggingProvider.*

public class Classifier extends Processor {

	public Classifier(Closure clos) {
		super(clos);
	}

//	def invokeMethod(String name, args) {
//		def metaClass = InvokerHelper.getMetaClass(this);
//		if(name == "process") {
//			def input = args[0]
//			def result = metaClass.invokeMethod(this, name, args);
//			if(result) {
//				result.isResult = true;
//				forward(result);
//			}
//			if(!services.query.isTupleBounded(querycontext, input)) {
//				forward(input);
//			} 
//		} else {
//			return metaClass.invokeMethod(this, name, args);
//		}
//	}
//	
//	public void input(EOS eos) {
//		forward(eos)
//	}
//	
	
	protected void inputImpl(input) {
		boolean responds = this.metaClass.respondsTo(this, "process", input);
		if(responds) {
			def out = process(input)
			if(out) {
				out.isResult = true; //TODO: fix
				forward out;
			}
		}
		if(!responds || !services.query.isTupleBounded(querycontext, input)) {
			forward(input);
		}
	}
}
