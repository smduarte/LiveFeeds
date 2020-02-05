package sensing.persistence.core.pipeline;

import groovy.lang.Closure;
import static sensing.persistence.core.logging.LoggingProvider.*;

public class GroupedComponent extends Component {
	protected static final CLEANUP_PERIOD = 60;
	protected Closure pipelineDescriptor;
	protected Closure groupByKey;
	def protected properties;
	protected Map<String,Pipeline> groupMap;
	protected lastInputTime = -1;
	protected groupTerm;
	
	class GroupTerm extends Component {
		
		public void input(EOS eos) {}

		public void streamInit() {}
		
		public boolean streamCanDispose(){
			return true;
		}
		
		public void streamDispose() {}
		
		public void streamReset() {}
	}

	public GroupedComponent(Closure clos) {
		pipelineDescriptor = clos;
	}

	public GroupedComponent groupBy(final Closure clos) {
		groupByKey = clos;
		groupMap = [:];
		return this;
	}
	
	public GroupedComponent groupBy(final List properties) {
		this.properties = properties
		groupBy({properties.inject(new StringBuilder()){resultkey,property -> resultkey << it.getProperty(property)}});
	}
	

	public void input(Tuple input) {
		assert groupMap != null;
		String key;
		try {
			key = groupByKey(input);
		} catch(MissingPropertyException e) {
			//println "group - wrong type: ${input.class.name}"
			next?.input(input);
			lastInputTime = services.scheduler.currentTime();
			return;
		}
		def pipe = groupMap[key];
		if(!pipe) {
			//println "got ${input} creating new group pipeline";
			pipe = new Pipeline();
			pipe.context = pipeline.context;
			AssemblerBase pa = new AssemblerBase(pipe);
			Closure descriptor = pipelineDescriptor.clone();
			pa.assemble(descriptor);
			if(!groupTerm) {
				groupTerm = new GroupTerm();
				groupTerm.next = next;
			}
			pipe.addComponent(groupTerm);
			pipe.parent = pipeline;
			pipe.invariant = properties;
			groupMap[key] = pipe;
			pipe.init();
		}
		//println "got ${input} forwarding to group pipeline";
		pipe.input(input);
		lastInputTime = services.scheduler.currentTime();
	}
	
	public void cleanup() {
		//int currTime = services.scheduler.currentTime() 
		//if(currTime - lastInputTime > CLEANUP_PERIOD) {
			int before = groupMap.size();
			def toRemove = [];
			groupMap.each{entry ->
				def p = entry.value;
				if(p?.canDispose()) {
					p.dispose();
					toRemove << entry.key;
					//groupMap[entry.key]=null;
				}
			}
			toRemove.each{groupMap.remove(it)};
//			if(before!=groupMap.size())  {
//				services.logging.log(DEBUG, this, "cleanup", "${before}-${groupMap.size()} diff: ${before-groupMap.size()}");
//			};
		//}		
	}

	public void input(EOS eos) {
		groupMap.values().each { it.input(eos)};
		forward(eos);
	}

	public void init() {
		pipeline.schedule(CLEANUP_PERIOD) {
			cleanup();
		}
	}
	
	public void dispose() {
		groupMap.each{ entry ->
			entry.value.dispose();
		}		
	}
	
	public void reset() {
		groupMap.each{ entry ->
			entry.value.reset();
		}
	}
	
	
	/*
	 * Monitoring
	 */
	

	public getState() {
		def state = groupMap.keySet();
		if(next) {
			return state + next.getState();
		} else {
			return state;
		}
	}
	
}
