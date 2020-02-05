package sensing.persistence.core.pipeline;
import static sensing.persistence.core.logging.LoggingProvider.*;

class LatestSet extends Component {
	public enum Mode {Change, Eos};
	
	Mode mode;
	int ttl
	protected Closure partitionKey
	def tupleSet = [:]
	
	public LatestSet(Mode mode,  int ttl) {
		this.mode = mode
		this.ttl = ttl
	}
	
	public setPartitionKey(Closure clos) {
		partitionKey = clos
	}
	
	public setPartitionKey(List properties) {
		this.partitionKey = {properties.inject(new StringBuffer()){resultkey,property -> resultkey << it."$property"}.toString()}	
	}
	
	
	public void input(Tuple input) {
		try {
			services.logging.log(DEBUG, this, "input","got $input");
			String key = partitionKey(input)
			tupleSet[key] = [input, services.scheduler.currentTime()];
			if(mode == Mode.Change) output()
		} catch(MissingPropertyException e) { 
			println "missing property in ${input}"
			e.printStackTrace();
			forward(input)
		}	
	}
	
	public void input(EOS eos) {
		services.logging.log(DEBUG, this, "input","got EOS");
		if(mode == Mode.Eos) output()
	}
	

	public boolean canDispose() {
		cleanupState()
		return tupleSet.size() == 0
	}
	
	public void reset() {
		tupleSet = [:];
	}
	
	protected void output() {
		cleanupState()
		tupleSet.each {key, val -> forward(val[0])}
		if(tupleSet.size() > 0) forward(EOS.instance)
	}
	
	protected void cleanupState() {
		if(ttl == 0) return // hard state
		double currTime = services.scheduler.currentTime();
		tupleSet = tupleSet.findAll{key, val -> currTime-val[1] <= ttl};	
	}
	
}
