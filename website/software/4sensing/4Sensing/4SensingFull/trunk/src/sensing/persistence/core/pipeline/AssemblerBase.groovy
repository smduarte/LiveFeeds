package sensing.persistence.core.pipeline;
import sensing.persistence.core.ServicesConfig;

class AssemblerBase {
	static final int eos		= 100;
	static final int change		= 110;
	static final int triggered	= 120;
	static final int periodic	= 130;

	protected final pipeline;

	public AssemblerBase(Pipeline target) {
		this.pipeline = target;
	}
	
	public ServicesConfig getServicesConfig() {
		return pipeline.services.config;
	}
	
	public Pipeline setMode(Pipeline.Mode mode) {
		pipeline.mode = mode;
		return pipeline;
	}
	
	/*
	 * Component
	 */	
	public Pipeline addComponent(Component c) {
		pipeline.addComponent(c);
	}
	
	/*
	 * Process
	 */	
	public  Pipeline process(Processor p) {
		pipeline.addComponent(p);	
	}
	
	public  Pipeline process(Closure processor) {
		Processor p = new Processor(processor);
		pipeline.addComponent(p);		
	}
	
	
	/*
	 * Aggregate
	 */	
	public   Pipeline aggregate(Class outputClass, Closure aggregator) {
		Aggregator ag = new Aggregator(outputClass, aggregator);
		pipeline.addComponent(ag);
	}
	
	/*
	 * Filter
	 */
	public  Pipeline filter(Closure filter) {
		Filter f = new Filter(filter);
		pipeline.addComponent(f);
	}
	
	/*
	 * Classify
	 */
	public  Pipeline classify(Closure classifier) {
		Processor c;
		if(servicesConfig.completeMode) {
			c = new CompleteClassifier(classifier);
		} else {
			c = new Classifier(classifier);
		}
		pipeline.addComponent(c);
	}
	
	/* 
	 * Set
	 */
	public  Pipeline set(parameters, key) {
		Component ls;
		if(parameters.mode == triggered) {
			ls = new TLatestSet(parameters.ttl, parameters.period);
		} else {
			ls = new LatestSet(parameters.mode == eos ? LatestSet.Mode.Eos : LatestSet.Mode.Change, parameters.ttl)
		}
		ls.setPartitionKey(key);
		pipeline.addComponent(ls);
		setMode(Pipeline.Mode.SET);
	}
	
	/* 
	 * TimeWindow
	 */
	public  Pipeline timeWindow(parameters) {
		Component w = parameters.mode == triggered ? new TTimeWindow(parameters.size, parameters.slide) : new PTimeWindow(parameters.size, parameters.slide)
		pipeline.addComponent(w);
		pipeline.setMode(Pipeline.Mode.SET);
	}
	
	/*
	 * Log
	 */
	public  Pipeline log(String name, int level) {
		Logger l = new Logger(name, level);
		pipeline.addComponent(l);
	}
	
	
	/*
	 * Throttle

	public  Pipeline throttle(int period, int mode) {
		Component t;
		switch(mode) {
			case PERIODIC: 
			t = new Throttle(period);
			break;
			case TRIGGERED: 
			t = new TThrottle(period);
			break;
		}
		addComponent(t);
	}
	 */	
	
	public void assemble(Closure pipelineDefinition) {
		pipelineDefinition.delegate = this;
		pipelineDefinition.resolveStrategy = Closure.DELEGATE_FIRST;
		pipelineDefinition.call();
	}

}
