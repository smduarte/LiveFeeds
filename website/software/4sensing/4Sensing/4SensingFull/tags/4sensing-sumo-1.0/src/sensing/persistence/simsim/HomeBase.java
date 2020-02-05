package sensing.persistence.simsim;

import java.util.List;
import java.util.UUID;

public interface HomeBase {
	public boolean sensorInput(Object reading);
	public List<Double> getBindingDestination(UUID mNodeId);

}
