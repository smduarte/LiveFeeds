package sensing.core.pipeline;

public class Processor extends Component {
	
	static emcCache = [:]
	
	public Processor() {}

	public Processor(Closure clos) {
		def emc;
		//TODO: important! emc caching assumes closure has no free variables
		if(emcCache[clos.class.name]) { 
			emc = emcCache[clos.class.name];
		} else {
			emc = new ExpandoMetaClass(this.class, false);
			emc.process = clos;
			emc.initialize();
			emcCache[clos.class.name] = emc;
		}
		this.metaClass = emc;	
//		this.metaClass.process = clos;
	}

//	public void input(input) {
//		if(this.metaClass.respondsTo(this, "process", input)) {
//			def out = process(input)
//			pipeline.incrProcessCount();
//			if(out) {
//				forward(out)
//			}
//		} else {
//			//println "${this} forwarding ${input} to ${next}";
//			forward(input);
//		}
//	}
	
	public void input(Tuple input) {
		//println "PROCESSOR ${this.class.name} GOT $input"
		pipeline.incrProcessCount();
		inputImpl(input)
	}
	
	public void input(EOS eos) {
		inputImpl(eos)
	}
	
	protected void inputImpl(input) {
		if(this.metaClass.respondsTo(this, "process", input)) {
			def out = process(input)
			if(out) {
				forward(out)
			} 
		} else {
			forward(input)
		}
	}

}
