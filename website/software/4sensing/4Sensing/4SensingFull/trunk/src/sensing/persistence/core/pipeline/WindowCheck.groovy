package sensing.persistence.core.pipeline
import sensing.persistence.core.pipeline.Tuple

class WindowCheck extends Processor {
	def prevWindow = null;
	def currWindow = [];
	
	public Tuple process(Tuple t) {
		currWindow << t;
		return null;
	}

	public void process(EOS eos) {
		if(prevWindow) {
			int inter = prevWindow.intersect(currWindow).size();
			if(inter > 0) {
				println "intersect: ${inter}";
			}
		}
		prevWindow = currWindow;
		currWindow.each{forward(it)}
		forward(EOS.instance)
		currWindow = [];
	}
}
