package sensing.persistence.core.test;
import sensing.persistence.core.pipeline.*;

public class StringProcessor extends Component{
	String process(String a) { 
		println "${name} processing string: ${a}";
		return a.toUpperCase();
	}
}
