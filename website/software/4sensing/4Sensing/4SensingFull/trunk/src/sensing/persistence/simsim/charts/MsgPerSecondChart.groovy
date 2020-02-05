package sensing.persistence.simsim.charts;

import sensing.persistence.simsim.*;
import simsim.gui.charts.XYLineChart;
import org.jfree.data.xy.XYSeries;


@Singleton(lazy=true) class MsgPerSecondChart extends Metric {
	final  XYLineChart chart;
	double start;
	def record = [];
	File output;
	
	public MsgPerSecondChart() {
		chart = new XYLineChart("Msg/Second", 1.0, "Msg/Second", "Time") ;
		chart.setSeriesLinesAndShapes("Msg/Second", true, false) ;
		PipelineSimulation.Gui.setFrameRectangle("Msg/Second", 484, 0, 480, 480);	
	}
	
	public void init() {
		output = openOutputFile("MsgPerSecond.csv") { writer ->
			writer << "Result\n"
		}
		//output.append("TIME,TOTAL_MSG,AVG_MSG\n";)
		start = PipelineSimulation.currentTime();
	}
	
	public void stop() {
		output.append("\nTIME");
		record.each{output.append(",${it[0]}")}
		output.append("\nMSGS");
		record.each{output.append(",${it[1]}")}
		output.append("\nAVG_MSGS");
		record.each{output.append(",${it[2]}")}
		output.append("\n");
	}
		
	
	public void update() {
		double msgSent = PipelineSimulation.nodeList.inject(0) {sum, Node node->
			sum += node.getSentMsg("sensing.persistence.core.query.QueryData");
			sum += node.getSentMsg("sensing.persistence.core.query.QueryResult");
			sum += node.getRemoteAcquiredCount();
		}

		double delta = PipelineSimulation.currentTime() - start;
		if(msgSent == 0 || delta == 0) return;
		def series = chart.getSeries("Msg/Second");
		//println "msgSent: ${msgSent} ratio:${msgSent/delta}";
		series.add(PipelineSimulation.currentTime(), msgSent/delta);
		record << [(int)delta, (int)msgSent, msgSent/delta]
	}
}
