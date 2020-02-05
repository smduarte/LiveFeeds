package sensing.persistence.simsim.speedsense.charts
import groovy.lang.Closure;
import sensing.persistence.simsim.charts.MTimeChart;
import org.jfree.data.xy.XYSeries;

class SegmentMTimeChart extends MTimeChart {
	
	public SegmentMTimeChart(Map params) {
		super(params);
	}
	
	public void segmentSelected() {
		series.collect{ String seriesName, Closure seriesMetric ->
			chart.getSeries(seriesName);
		}*.clear();
	}

}