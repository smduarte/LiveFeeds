package sensing.persistence.simsim.charts;

import sensing.persistence.simsim.*;
import simsim.gui.charts.XYLineChart;
import org.jfree.data.xy.XYSeries;


class TimeChart extends Metric {
	final  XYLineChart chart;
	def record = null;
	Closure indicator;
	String indName;
	File output;
	boolean newOutputFile = false;
	String outputFileName = null;
	boolean display;
	boolean file;
	
	public TimeChart(Closure indicator, String indName, boolean display, boolean file) {
		this.indicator = indicator;
		this.indName = indName;
		this.display = display;
		this.file = file;
		
		if(display) {
			chart = new XYLineChart( "${indName}  Time Series", 1.0, indName, "Time") ;
			chart.setSeriesLinesAndShapes(indName, true, false) ;
			PipelineSimulation.Gui.setFrameRectangle("${indName}  Time Series", 484, 0, 480, 480);	
		}
		if(file) {
			outputFileName = "${indName}TimeSeries.csv";
			newOutputFile = !outputFileExists(outputFileName);	
			record = [];
		}
	}
	
	public void init() {
		if(file) {
			output = openOutputFile(outputFileName);
		}
	}
	
	public void stop() {
		if(file) {
			if(newOutputFile) {
				output.append("Result\n1");
				(PipelineSimulation.setup.RUN_TIME-1).times{
					output.append(",${it+2}")
				}
				output.append("\n");
			}
			output.append(record.head()[1]);
			record.tail().each{output.append(",${it[1]}")}
			output.append("\n");
		}
	}
		
	
	public void update() {
		double time = PipelineSimulation.currentTime();
		double val = indicator();
		if(display) {
			def series = chart.getSeries(indName);
			series.add(time, val);
		}
		if(file) {
			record << [time, val]
		}
	}
}
