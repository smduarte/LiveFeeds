package simsim.gui.charts;

import java.util.*;

import org.jfree.chart.*;
import org.jfree.data.xy.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;


/**
 * A convenience wrapper class for using  XYScatterChart charts from the JFreeChart package. 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class XYScatterChart extends XYChart<XYSeries> {

	protected String name ;
	protected JFreeChart chart;
	protected XYSeriesCollection data;
	protected Map<String, XYSeries> series ;
	protected String xAxisLabel, yAxisLabel ;
	protected XYDotRenderer renderer ;
	
	public XYScatterChart( String frame, double fps, String xAxisLabel, String yAxisLabel ) {
		super( frame, fps, xAxisLabel, yAxisLabel) ;
	}

	protected void createChart() {
		data = new XYSeriesCollection();
		series = new HashMap<String,XYSeries>() ;
		renderer = new XYDotRenderer() ;		
		chart = ChartFactory.createXYLineChart( name, yAxisLabel, xAxisLabel, data, PlotOrientation.VERTICAL, true, false, false);
		chart.getXYPlot().setRenderer( renderer);
        chart.setAntiAlias(true) ;
        chart.setTextAntiAlias(true) ;
	}
	

	protected XYSeries createSeries(String name) {
		XYSeries s = new XYSeries(name) ;
		((XYSeriesCollection)data).addSeries(s) ;
		return s;
	}
}