package sensing.persistence.simsim.charts
import sensing.persistence.simsim.Metric;
import sensing.persistence.simsim.PipelineSimulation;
import sensing.persistence.core.network.PeerDB;

class NodeInfo  extends Metric {
	Closure metric;
	String metricName;
	String fName;
	List header;

	public NodeInfo(Closure metric, String metricName) {
		this.metric = metric;
		this.metricName = metricName
		this.fName = "NodeInfo_${metricName}.csv";
	}

	public void init() {
		header = (1..PipelineSimulation.setup.TOTAL_NODES);
		openOutputFile(fName, header);
	}
	
	public void output() {
		writeToFile(fName, PeerDB.peersList.collect(metric), header)
	}

}
