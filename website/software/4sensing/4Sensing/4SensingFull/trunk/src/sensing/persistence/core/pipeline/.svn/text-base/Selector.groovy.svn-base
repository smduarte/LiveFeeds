package sensing.persistence.core.pipeline;

public class Selector {
	Component pipelineHead;

	Selector(Closure clos) {
		def emc = new ExpandoMetaClass(Selector, false);
		emc.start = clos;
		emc.initialize();
		this.metaClass = emc;		
	}
	
	void insert(Object o) {
		pipelineHead.input(o);
	}

	public void start() {};
}
