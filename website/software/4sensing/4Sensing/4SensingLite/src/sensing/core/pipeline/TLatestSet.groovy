package sensing.core.pipeline;

class TLatestSet extends TProcessor {

	int ttl
	protected Closure partitionKey
	def tupleSet = [:]
	
	public TLatestSet(int ttl, int period) {
		super(period);
		this.ttl = ttl
	}
	
	public setPartitionKey(Closure clos) {
		partitionKey = clos
	}
	
	public setPartitionKey(List properties) {
		this.partitionKey = {properties.inject(new StringBuffer()){resultkey,property -> resultkey << it."$property"}.toString()}
	}
	
	
	public Tuple process(Tuple input) {
		try {
			String key = partitionKey(input)
			tupleSet[key] = [input, services.scheduler.currentTime()];
		} catch(MissingPropertyException e) { 
			println "missing property in ${input}"
			e.printStackTrace();
			return input;
		}	
		return null;
	}
	
	

	public boolean canDispose() {
		cleanupState()
		return tupleSet.size() == 0
	}
	
	protected boolean output() {
		tupleSet.each {key, val ->
			def(tuple, ts) = val
			forward(tuple)
		}
		forward(EOS.instance)
		cleanupState()
		return (tupleSet.size > 0);
	}
	
	protected void cleanupState() {
		if(ttl == 0) return // hard state
		double currTime = services.scheduler.currentTime()
		def remove = []
		tupleSet.each {key, val ->
			def(tuple, ts) = val
			if(currTime-ts >= ttl) {
				remove << key
			} 
		}
		remove.each{tupleSet.remove(it)}		
	}
}
