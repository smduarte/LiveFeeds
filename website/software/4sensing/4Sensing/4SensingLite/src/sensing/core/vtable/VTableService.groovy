package sensing.core.vtable;

import groovy.lang.Binding;
import sensing.core.ServiceManager;
import sensing.core.Service;
import sensing.core.pipeline.*;

class VTableService extends Service {
	
	public VTableService(ServiceManager services) {
		super(services);
		VTableManager.init(services.config.vTableCodebase);
	}
	
	public VTableDefinition getVTableDefinition(String vtName) {
		VTableManager.instance.getVTableDefinition(vtName);
	}
	
	
	/* Pipeline assembly
	 * 
	 */
	
	/* 
	 * newPipeline - build pipeline stage
	 */
	public Pipeline newPipeline(VTableDefinition vd, String stage, Binding context) {
		Pipeline pipe = new Pipeline();
		pipe.context = context;
		Assembler pa = new Assembler(pipe);
		vd.getStage(stage).builders.each { f ->
			Closure builder = f.clone();
			pa.assemble(builder);
		}
		return pipe;
	}
	
	/* 
	 * newPipeline - build full pipeline by concatenation off all stages
	 */
	public Pipeline newPipeline(VTableDefinition vd, Binding context) {
		return newPipelineFrom(VTableDefinition.DATASRC, context);
	}
	
	
	/* 
	 * newPipelineFrom 
	 */
	protected Pipeline newPipelineFrom(VTableDefinition vd, String startStage, Binding context) {
		Pipeline pipe = new Pipeline();
		pipe.context = context;
		Assembler pa = new Assembler(pipe);
		def stage = vd.getStage(startStage);
		while(stage) {
			stage.builders.each{ f ->
				Closure builder = f.clone();
				pa.assemble(builder);
			}
			stage = vd.getStage(stage.nextStage);
		}
		pipe.init();
		return pipe;
	}
}
