package simsim.gui.charts;

import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
/**
 * A convenience wrapper class for using simple XYDeviationLineChart charts from the JFreeChart package. 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class XYDeviationLineChart extends XYChart<YIntervalSeries> {
	
	private DeviationRenderer dr ;
	
	public XYDeviationLineChart( String frame, double fps, String xAxisLabel, String yAxisLabel ) {
		super( frame, fps, xAxisLabel, yAxisLabel) ;
	}

	@SuppressWarnings("serial")
	protected void createChart() {
		data = new YIntervalSeriesCollection();
		
		dr = new DeviationRenderer(true, false) {
			public boolean getItemShapeVisible(int series, int item)  {
				Boolean v = getSeriesShapesVisible(series) ;
				return item % shapeSpacing == 0 && (v != null && v );
			} ;
		} ;
		renderer = dr ;
		dr.setAlpha(0.1f) ;
		dr.setDrawSeriesLineAsPath(true) ;
		
		series = new HashMap<String, YIntervalSeries>() ;		
		chart = ChartFactory.createXYLineChart( name, yAxisLabel, xAxisLabel, data, PlotOrientation.VERTICAL, true, false, false);
		chart.getXYPlot().setRenderer( renderer);
	}

	protected YIntervalSeries createSeries(String name) {
		YIntervalSeries s = new YIntervalSeries(name) ;
		((YIntervalSeriesCollection)data).addSeries(s) ;
		return s;
	}
	
	public void setAlpha( double alpha ) {
		dr.setAlpha((float)alpha) ;
	}
	
}