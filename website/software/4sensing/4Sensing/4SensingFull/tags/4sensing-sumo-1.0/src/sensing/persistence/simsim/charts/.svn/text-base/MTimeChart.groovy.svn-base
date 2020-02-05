package sensing.persistence.simsim.charts;

import sensing.persistence.simsim.*;
import simsim.gui.charts.XYLineChart;
import org.jfree.data.xy.XYSeries;


class MTimeChart extends Metric {
	final  XYLineChart chart;
	Map series;
	
	public MTimeChart(Map params) {
		//this.series = series
		chart = createChart(title: params.title, xLabel: "Time", yLabel: params.yLabel); 
		series = params - [title:params.title, yLabel:params.yLabel];
		series.each{ String seriesName, Closure seriesMetric ->
			chart.setSeriesLinesAndShapes(seriesName, true, false) ;
		}
		
	}
		
	public void update() {
		double time = PipelineSimulation.currentTime();
		series.each{ String seriesName, Closure seriesMetric ->
			def val =  seriesMetric();
			if(val) {
				chart.getSeries(seriesName).add(time, seriesMetric());
			}
		}
	}
}
