package sensing.core.vtable;

import org.codehaus.groovy.control.CompilerConfiguration;
import sensing.core.query.Query;
import VTableManager;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

public class VTableManager {

	protected static VTableManager instance;
	protected GroovyScriptEngine engine;
	protected Map vtableDefCache = [:];

	protected VTableManager(String codeBase) {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass(VTableDefinition.class.getName());
		GroovyClassLoader cl= new GroovyClassLoader(this.class.getClassLoader(), cc, false)
		engine = new GroovyScriptEngine(codeBase, cl);
	}
	
	public static void init(String codeBase) {
		if(instance == null) {
			instance = new VTableManager(codeBase);
		}
	}
	

	public void invalidateCache() {
		vtableDefCache = [:];
	}

//	public Pipeline getPipeline(Query q, Binding b) {
//		Pipeline p = (Pipeline) engine.run(q.getPipelineName()+".groovy", b);
//		p.context = b;
//		return p;
//	}

	public VTableDefinition getVTableDefinition(String vtName) {
		String vtableName = vtName.replaceAll(/\./, '/');
		VTableDefinition vtDef;
		if(vtableDefCache[vtableName]) {
			vtDef = vtableDefCache[vtableName];
		} else {
			vtDef = vtableDefCache[vtableName] = (VTableDefinition) engine.run(vtableName+".groovy", new Binding());
		}
		return vtDef;
	}
	
}
