package sensing.persistence.simsim;
import groovy.lang.GroovyObject;

public interface PipelineSimJ {
	public void registerMobileNode(MobileNode m);
	
	public int getNumRegisteredMobileNodes();
	
	public GroovyObject newTuple(String className);
	
	public <T> T getConfig(String name);

}
