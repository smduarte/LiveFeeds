package simsim.gui.charts;


import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Series;

/**
 * A convenience wrapper class for using simple XYLineChart charts of the JFreeChart package. 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
abstract public class XYChart<T extends Series> extends AbstractXYChart<T> {

	
	public XYChart( String frame, double fps, String xAxisLabel, String yAxisLabel ) {
		super( frame, fps, xAxisLabel, yAxisLabel) ;
	}
	
	public void setSeriesLinesAndShapes( String name, boolean visibleLines, boolean shapesVisible ) {
		int i = getSeriesIndex( name ) ;
		((XYLineAndShapeRenderer)renderer).setSeriesLinesVisible(i, visibleLines);
		((XYLineAndShapeRenderer)renderer).setSeriesShapesVisible(i, shapesVisible);
	}
	
}