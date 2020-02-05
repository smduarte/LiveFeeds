package sensing.persistence.core.test;
import sensing.persistence.core.pipeline.*;

public class IntProcessor extends Component {

	int process(int a) { 
		println "${name} processing int: ${a}";
		return a + 100;
	}
}
