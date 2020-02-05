package sensing.persistence.simsim.charts
import sensing.persistence.simsim.PipelineSimulation;
import sensing.persistence.simsim.Metric;

class Cumulative extends Metric {
	def metrics;
	String 	name;
	String fName;
	List header;
	
	
	public Cumulative( String name, metrics) {
		this.metrics = metrics;
		this.name = name;
		this.fName = "${name}_Cumulative.csv";
	}
	
	public void init() {
		header = metrics*.name
		openOutputFile(fName, header)	
	}
	
	public void output() {
		writeToFile(fName, metrics*.fn(), header)
	}

}
