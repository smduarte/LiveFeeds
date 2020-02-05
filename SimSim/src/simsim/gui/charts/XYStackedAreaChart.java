package simsim.gui.charts;

import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;

/**
 * A convenience wrapper class for using simple XYLineChart charts of the JFreeChart package. 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class XYStackedAreaChart extends AbstractXYChart<XYSeries> {

	public XYStackedAreaChart( String frame, double fps, String xAxisLabel, String yAxisLabel ) {
		super( frame, fps, xAxisLabel, yAxisLabel) ;
	}

	protected void createChart() {
		data = new DefaultTableXYDataset();
		series = new HashMap<String,XYSeries>() ;
		renderer = new StackedXYAreaRenderer2();
		
		chart = ChartFactory.createStackedXYAreaChart( name, yAxisLabel, xAxisLabel, (DefaultTableXYDataset)data, PlotOrientation.VERTICAL, true, false, false);
		chart.getXYPlot().setRenderer( renderer);

	}
	
	protected XYSeries createSeries(String name) {
		XYSeries s = new XYSeries(name,true,false) ;
		((DefaultTableXYDataset)data).addSeries(s) ;
		return s;
	}
}