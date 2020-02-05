package livefeeds.twister6.stats.gui;

import static simsim.core.Simulation.Gui;

import java.util.ArrayList;
import java.util.List;

import livefeeds.twister6.config.Config;
import livefeeds.twister6.stats.TrafficStatistics;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.data.xy.XYSeries;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.charts.XYStackedAreaChart;
import simsim.ssj.BinnedTally;

import umontreal.iro.lecuyer.stat.Tally;

public class StackedBinnedTallyDisplay {
	
	private String frame ;
	private XYStackedAreaChart chart ;
	private List<BinnedTally> items = new ArrayList<BinnedTally>() ;
	
	public StackedBinnedTallyDisplay( String frame, BinnedTally ... args ) {
		this.frame = frame ;
		for( BinnedTally i : args ) 
			items.add( (TrafficStatistics) i) ;
		
		init() ;
	}
	
	public void saveChart(String pdfName ) {
		try {
			prepare() ;
			chart.saveChartToPDF(pdfName, 500, 500) ;
		} catch( Exception x ) {
			x.printStackTrace() ;
		}		
	}
	
	synchronized private void prepareChartSeries( BinnedTally ts, double xAxisUnitMultiplier) {
		XYSeries a = chart.getSeries(ts.name) ;
		
		a.clear() ;
		
		double x = ts.binSize/2 ;
		for( Tally i : ts.bins ) {
			if( i.numberObs() > 2) {
				a.addOrUpdate(x * xAxisUnitMultiplier,  i.average() ) ;
			}
			x += ts.binSize ;
		}
	}
	
	private void prepare() {
		for( BinnedTally i : items )
			prepareChartSeries( i, 1.0 / 3600.0 ) ; // in hours		
	}
	
	void init() {
		chart = new XYStackedAreaChart("Catadupa Traffic by Session Duration", 0.0, "bytes/s", "time(h)") ;
		chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("bytes/sec")) ;
		chart.setYRange(false, 1, 10000) ;
		chart.setXRange(false, 0, Config.Config.MAX_SESSION_DURATION/3600) ;

		Pen p0 = new Pen(new RGB(0,1,0,0.25));
		Pen p1 = new Pen(new RGB(0,0,1,0.25)) ;
		Pen p2 = new Pen(new RGB(1,0,0,0.25)) ;
		Pen p3 = new Pen(new RGB(1,0.7,0,0.33)) ;
		Pen p4 = new Pen(new RGB(0,0.7,1,0.33)) ;
	
		Pen[] pens = new Pen[] {p0, p1, p2, p3, p4} ;
		
		for( int i = 0 ; i < Math.min( pens.length, items.size()) ; i++ )
			chart.setSeriesPen(  items.get(i).name, pens[i] ) ;
		
		Gui.addDisplayable(frame, new Displayable() {
			public void displayOn( Canvas canvas) {	
				prepare() ;
				chart.displayOn( canvas) ;
			}
		}, 0.5) ;
		
		Gui.setFrameRectangle(frame, 0, 0, 480, 480) ;
		Gui.setFrameTransform(frame, 500, 500, 0, false) ;		
	}
}
