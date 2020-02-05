package sensing.core.pipeline;

import org.codehaus.groovy.runtime.InvokerHelper;
import static sensing.core.logging.LoggingProvider.*

public class CompleteClassifier extends Processor implements GroovyInterceptable {

	public CompleteClassifier(Closure clos) {
		super(clos);
	}

	protected void inputImpl(input) {
		boolean responds = this.metaClass.respondsTo(this, "process", input);
		if(responds && services.query.isTupleBounded(querycontext, input)) {
			def out = process(input)
			if(out) {
				out.isResult = true; //TODO: fix
				forward out;
			}
		} else {
			forward(input);
		}
	}
}
