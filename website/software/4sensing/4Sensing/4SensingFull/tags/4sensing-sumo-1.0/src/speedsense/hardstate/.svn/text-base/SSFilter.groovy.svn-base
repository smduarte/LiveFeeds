package speedsense.hardstate;
import sensing.persistence.core.pipeline.*;
import speedsense.*;

public class SSFilter extends Filter {
	public process(AggregateSpeed input) {
		return changeAbsolute('avgSpeed', 15) || changeAbsolute('count',1 )
	}

}
