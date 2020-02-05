package sensing.persistence.test.pipeline;
import sensing.persistence.core.pipeline.*;

// Pipeline
Pipeline p = new Pipeline();

p.select {
	insert(1);
};

p.process new NodeCount();

p.filter {int i -> i > 0 }
