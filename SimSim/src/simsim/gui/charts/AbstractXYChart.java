package simsim.gui.charts;


import static simsim.core.Simulation.Gui;

import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.xy.AbstractXYDataset;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;

/**
 * A convenience wrapper class for using simple XYLineChart charts of the JFreeChart package. 
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
abstract public class AbstractXYChart<T extends Series> implements Displayable {

	private static RGB[] colors = { RGB.RED, RGB.BLUE, RGB.GREEN, RGB.MAGENTA, RGB.CYAN, RGB.ORANGE } ;

	protected String name ;
	protected JFreeChart chart;
	protected AbstractXYDataset data ;
	protected AbstractXYItemRenderer renderer ;
	protected String xAxisLabel, yAxisLabel ;

	protected Map<String, T> series ;
	
	protected int shapeSpacing = 1 ;
	private boolean filteredLegend = false ;
	
	public AbstractXYChart( String frame, double fps, String xAxisLabel, String yAxisLabel ) {
		this.name = frame ;
		this.xAxisLabel = xAxisLabel ;
		this.yAxisLabel = yAxisLabel ;
		init();		
		if( fps > 0 ) { 
			Gui.addDisplayable(frame, this, fps) ;
			Gui.setFrameTransform(frame, 500, 500, 0, false) ;
		}
	}

	public void setShapeSpacing( int ss) {
		this.shapeSpacing = ss ;
	}
	
	public void setXRange( boolean auto, double min, double max ) {
		chart.getXYPlot().getDomainAxis().setRange( min, max);
		chart.getXYPlot().getDomainAxis().setAutoRange(auto);
	}

	public void setYRange( boolean auto, double min, double max ) {
		chart.getXYPlot().getRangeAxis().setRange( min, max);
		chart.getXYPlot().getRangeAxis().setAutoRange(auto);		
	}

	public void setAxisLabels( String domain, String range ) {
		chart.getXYPlot().getRangeAxis().setLabel( range ) ;
		chart.getXYPlot().getDomainAxis().setLabel( domain ) ;
	}

	public void setSeriesLinesAndShapes( String name, boolean visibleLines, boolean shapesVisible ){
	}
	
	public int numSeries() {
		return series.size();
	}
	
	public Set<String> seriesNames() {
		return series.keySet() ;
	}
	
	public T getSeries( String name ) {
		T s = series.get( name) ;
		if( s == null ) {	
			s = createSeries(name) ;
			series.put( name, s) ;
		}
		return s ;
	}
	
	public void copySeriesColors( String src, String dst ) {
		int i = getSeriesIndex(src) ;
		int j = getSeriesIndex(dst) ;
		renderer.setSeriesStroke( j, renderer.getSeriesStroke(i) ) ;
        renderer.setSeriesPaint( j, renderer.getSeriesPaint(i) );
        renderer.setSeriesFillPaint( j, renderer.getSeriesFillPaint(i) );
	}
	
	public void setAlpha( double alpha ) {
	}
		
	public int getSeriesIndex( String name ) {
		Series s = getSeries( name ) ;
		for( int i = 0 ; i < data.getSeriesCount() ; i++ )
			if( s.getKey().equals( data.getSeriesKey(i) ) )
					return i ;
		return -1;
	}
	
	public void setSeriesPen( String series, Pen p ) {
		int i = getSeriesIndex( series) ;
		renderer.setSeriesStroke(i, p.stroke) ;
        renderer.setSeriesPaint(i, p.color );
	}

	public void setSeriesStroke( String series, Stroke s ) {
		int i = getSeriesIndex( series) ;
		renderer.setSeriesStroke(i, s) ;
	}

	public void setSeriesFillPaint( String series, Paint p ) {
		int i = getSeriesIndex( series) ;
		renderer.setSeriesFillPaint(i, p ) ;
	}

	public void setSeriesFillPaint( String series, Pen p ) {
		int i = getSeriesIndex( series) ;
		renderer.setSeriesFillPaint(i, p.color ) ;
	}
	
	public JFreeChart chart() { 
		if( chart == null ) {
			createChart() ;
		}
		return chart ;
	}
	
	protected abstract void createChart() ;
	protected abstract T createSeries( String name) ;
	/**
	 * Initializes all the FreeChart stuff..
	 */
	public void init() {
		createChart() ;
		
		XYPlot plot = (XYPlot) chart.getPlot() ;
		plot.setBackgroundPaint( RGB.WHITE );
        plot.setDomainGridlinePaint(RGB.GRAY);
        plot.setRangeGridlinePaint( RGB.GRAY);

        int j = 0 ;
        for( RGB i : colors ) {
        	Pen p = new Pen( i, 2) ;
        	renderer.setSeriesStroke(j, p.stroke) ;
            renderer.setSeriesPaint(j, p.color.darker() );
            renderer.setSeriesFillPaint(j, p.color.brighter());
            j++ ;
        }
       
        chart.setAntiAlias(true) ;
        chart.setTextAntiAlias(true) ;
	}

	public void displayOn( Canvas canvas ) {
		final Rectangle chartArea = new Rectangle(0, 0, 500, 500);
		try {
			chart.draw( canvas.gs, chartArea, null, null);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	
	public void saveChartToPDF(String fileName, int width, int height) throws Exception {
		Charts.saveChartToPDF(chart, fileName, width, height) ;
	}
	
	
	public void filterLegend( String pattern ) {
		if( filteredLegend ) return ;
		
		filteredLegend = true ;
		
		final LegendItemCollection nlic = new LegendItemCollection() ;
		LegendItemCollection lic = chart.getXYPlot().getLegendItems()  ;
		
		for( Iterator<?> i = lic.iterator() ; i.hasNext() ; ) {
			LegendItem j = (LegendItem) i.next()  ;	
			if( j.getLabel().endsWith(pattern))
				continue ;			
			nlic.add( legendItem( j, pattern ) ) ;
		}
		
		LegendItemSource source = new LegendItemSource() {			
			@Override
			public LegendItemCollection getLegendItems() {
				return nlic;
			}
		};		
		chart.getLegend().setSources(  new LegendItemSource[] { source }  ) ;
	}
	
	private LegendItem legendItem( LegendItem i, String pattern ) {
		LegendItem j = null ;
		LegendItemCollection lic = chart.getXYPlot().getLegendItems()  ;
		for( Iterator<?> it = lic.iterator() ; it.hasNext() ; ) {
			j = (LegendItem)it.next() ;
			if( j.getLabel().startsWith( i.getLabel() ) && j.getLabel().endsWith(pattern) ) 
				break ;
		}		
		return j == null ? i : new LegendItem( i.getLabel(), "", "", "", true, j.getShape(), j.isShapeFilled(), j.getFillPaint(), j.isShapeOutlineVisible(), j.getOutlinePaint(), j.getOutlineStroke(), true, j.getLine(), j.getLineStroke(), j.getLinePaint() ) ;
	}
}