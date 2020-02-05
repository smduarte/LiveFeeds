package sensing.core.pipeline;

class SimpleProcessor extends Processor {
	private Closure processorClos
	
	public Processor(Closure clos) {
		this.processorClosure = clos
	}
	
}
