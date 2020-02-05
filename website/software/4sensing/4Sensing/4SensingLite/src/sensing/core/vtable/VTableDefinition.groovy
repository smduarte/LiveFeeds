package sensing.core.vtable;

import static sensing.core.logging.LoggingProvider.*;
import sensing.core.pipeline.Pipeline;
import sensing.core.pipeline.Assembler;

public abstract class VTableDefinition extends Script {
	public static String DATASRC = "dataSource";
	public static String GLOBALAGR = "globalAggregation";
	protected String lastStage;  
	protected stages = [:];
	protected sensorInput = [];


	public void sensorInput(Class sensorClass) {
		println "adding input ${sensorClass}"
		sensorInput << sensorClass
	}

	public void tableInput(String tableName) {
		println "composing vtable ${tableName}";
		VTableDefinition source =  VTableManager.instance.getVTableDefinition(tableName);	
		this.stages = source.stages;
		this.lastStage = source.lastStage;
		this.sensorInput = source.sensorInput;
	}
	
	public VTableDefinition dataSource(Closure pipelineDefinition) {
		addStage(DATASRC, pipelineDefinition);
	}
	
	int gaId = 0;
	public VTableDefinition globalAggregation(Closure pipelineDefinition) {
		addStage(this.class.name + "." + GLOBALAGR + (gaId++), pipelineDefinition);
	}
	
	
	protected VTableDefinition addStage(String name, Closure pipelineDefinition) {
		println "adding stage ${name}";
		stages[name] = [builders: [pipelineDefinition], nextStage: null];
		if(lastStage) {
			stages[lastStage].nextStage = name;
		}
		lastStage = name;
		return this;
	}
	
	
	public getStage(String stage) {
		return stages[stage];
	}
	
	public getSensorInputs() {
		return sensorInput;
	}
		




}
