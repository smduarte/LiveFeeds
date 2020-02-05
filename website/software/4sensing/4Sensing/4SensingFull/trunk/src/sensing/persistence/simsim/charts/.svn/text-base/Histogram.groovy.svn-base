package sensing.persistence.simsim.charts
import java.util.List;

import sensing.persistence.simsim.PipelineSimulation

import sensing.persistence.simsim.Metric;
import simsim.gui.charts.XYLineChart;

class Histogram extends Metric {
	static final String DEFAULT_SERIES = "Default"
	XYLineChart chart
	String title
	String metricName
	String fileName
	double startVal
	double stepVal
	double maxVal
	Map histSeries = [:]
	boolean display
	boolean store
	
	public Histogram(params) {
		assert params.maxVal || !params.fileName
		
		this.display = params.display != null ? params.display : true
		this.fileName = params.fileName
		this.store = fileName != null
		this.title = params.title ?: ""
		this.metricName = params.metricName   ?: ""
		this.startVal = params.startVal ?: 0
		this.stepVal  = params.stepVal  ?: 1
		this.maxVal   = params.maxVal   ?: 0
		
		if(display) {
			chart = createChart(title: title, xAxis: metricName, yAxis: "Frequency")
		}
	}
	
	
	public void output() {
		updateHist()
		if(store) {
			int maxPos = (int)((maxVal-startVal)/stepVal);
			List header = (0..maxPos).collect{startVal+it*stepVal};
			histSeries.each{String series, List freq ->
				String fName = (series == DEFAULT_SERIES ? "${fileName}_Histogram.csv" : "${fileName}_${series}_Histogram.csv");			
				openOutputFile(fName, header); 
				writeToFile(fName, (0..maxPos).collect{freq[it] ?: 0}, header);
			}
		}
	}
	
	
	public void addValue(series, double val) {
		series = series.toString()
		List freq = histSeries[series];
		if(freq == null) {
			freq = histSeries[series] = [];
			if(display) chart.setSeriesLinesAndShapes(series, true, true);
		}
		int pos = val<=startVal ? 0 : (int)((val-startVal)/stepVal)
		freq[pos] = (freq[pos] ?: 0) + 1
	}
	
	public void addValue(double val) {
		addValue(DEFAULT_SERIES, val);
	}
	
	public void clear(series) {
		histSeries[series] = [];	
	}
	
	public void clear() {
		clear(DEFAULT_SERIES);	
	}
	
	
	public void update() {
		if(display) {
			updateHist()
			display()
		}
	}
	
	protected void updateHist() {}
	
	protected void display() {
		histSeries.each{ String series, List freq ->
			int maxPos = maxVal ? (int)((maxVal-startVal)/stepVal) : freq.size()-1;
			def cSeries = chart.getSeries(series);
			cSeries.clear();
			(0..maxPos).each{ int idx ->
				cSeries.add(startVal+idx*stepVal, freq[idx] ?: 0);
			}
		}
	}
}
