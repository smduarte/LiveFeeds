package sensing.persistence.core.pipeline;

class Assembler extends AssemblerBase {
	
	public Assembler(Pipeline target) {
		super(target)
	}
	
	/*
	 * GroupBy
	 */
	
	public Pipeline groupBy(key, Closure partitionPDefinition) {
		GroupedComponent gp = new GroupedComponent(partitionPDefinition);
		gp.groupBy(key);
		pipeline.addComponent(gp);
	}

}
