package sensing.persistence.test;

class BasicPipeline {
	def components = [];
	def addComponent(comp) {
		components << comp;
	}
	def print() {
		components.each { println it}
	}
}

class Pipeline {
	def components = [];
	def addComponent(comp) {
		components << comp;
	}
	def print() {
		components.each { println it}
	}
}

class BasicPDL {
	static process( BasicPipeline self) {
		self.addComponent "process"
	}
	static classify( BasicPipeline self) {
		self.addComponent "classify"
	}	
}

class PDL extends BasicPDL {
	static groupby(Pipeline self) {
		self.addComponent "groupby"
	}	
}


p = new Pipeline()
def c = {process(); classify(); groupby();}
use(PDL) {
	c.delegate = p
	c.call()
	p.print()
	println "done"
}
