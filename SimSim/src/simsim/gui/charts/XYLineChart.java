package simsim.gui.charts;

import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * A convenience wrapper class for using simple XYLineChart charts of the JFreeChart package. 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class XYLineChart extends XYChart<XYSeries> {

	public XYLineChart( String frame, double fps, String xAxisLabel, String yAxisLabel ) {
		super( frame, fps, xAxisLabel, yAxisLabel) ;
	}

	@SuppressWarnings("serial")
	protected void createChart() {
		data = new XYSeriesCollection();
		series = new HashMap<String,XYSeries>() ;
		renderer = new XYLineAndShapeRenderer(){
			public boolean getItemShapeVisible(int series, int item)  {
				Boolean v = getSeriesShapesVisible( series ) ;
				return item % shapeSpacing == 0 && (v != null && v );
			} ;
		} ;		
		chart = ChartFactory.createXYLineChart( name, yAxisLabel, xAxisLabel, data, PlotOrientation.VERTICAL, true, false, false);
		chart.getXYPlot().setRenderer( renderer);

	}
	


	protected XYSeries createSeries(String name) {
		XYSeries s = new XYSeries(name) ;
		((XYSeriesCollection)data).addSeries(s) ;
		return s;
	}
}