package sensing.persistence.test.pipeline;
import sensing.persistence.core.pipeline.Processor;
 


local {
	select {
		insert(1);
	};
}

global {
	process new NodeCount();
	filter {int i -> i > 0 };
}
