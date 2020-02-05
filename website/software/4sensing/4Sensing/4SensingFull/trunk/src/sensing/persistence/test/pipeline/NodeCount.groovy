package sensing.persistence.test.pipeline;
import sensing.persistence.core.pipeline.Processor;


class NodeCount extends Processor {
	int count = 0;
	int numNodes = 0;
	
	public process(int input) {
		count += input;
		numNodes++;
		println("${pipeline.context.peercontext.n} [Count: ${count} NumNodes: ${numNodes} Children: ${pipeline.context.querycontext.children.size()}]");
		if(numNodes == pipeline.context.querycontext.children.size() ) {
			println "sending output";
			return count;
		} else {
			return null;
		}
	}
};
