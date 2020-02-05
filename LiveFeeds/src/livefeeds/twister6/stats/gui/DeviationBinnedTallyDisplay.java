package livefeeds.twister6.stats.gui;

import static simsim.core.Simulation.Gui;

import java.util.ArrayList;
import java.util.List;

import livefeeds.twister6.config.Config;

import org.jfree.data.xy.YIntervalSeries;

import simsim.core.Displayable;
import simsim.core.Globals;
import simsim.gui.canvas.Canvas;
import simsim.gui.charts.XYDeviationLineChart;
import simsim.ssj.BinnedTally;

import umontreal.iro.lecuyer.stat.Tally;

public class DeviationBinnedTallyDisplay {
	
	private String frame ;
	private XYDeviationLineChart chart ;
	private List<BinnedTally> items = new ArrayList<BinnedTally>() ;
	
	public DeviationBinnedTallyDisplay( String frame, BinnedTally ... args ) {
		this.frame = frame ;
		for( BinnedTally i : args ) 
			items.add(i) ;
		
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
	
	public void prepareChartSeries( BinnedTally ts, double xAxisUnitMultiplier) {
		YIntervalSeries a = (YIntervalSeries)chart.getSeries(ts.name) ;
//		YIntervalSeries b = (YIntervalSeries)chart.getSeries(ts.name + "_minmax") ;
		
		a.clear() ;
//		b.clear() ;
//		chart.copySeriesColors(ts.name, ts.name + "_minmax");

		double x = ts.binSize / 2 ;
		for( Tally i : ts.bins ) {
			if( i.numberObs() > 2) {
				double y = i.average() ;
				double l = Math.max( i.min(), i.average() - i.standardDeviation()), h = Math.min( i.max(), i.average() + i.standardDeviation() );
				if( displayAverageOnly ) {
					a.add(x * xAxisUnitMultiplier, y, y, y) ;	
				} else {
					a.add(x * xAxisUnitMultiplier, y, l, h) ;
					//b.add(x * xAxisUnitMultiplier, y, i.min(), i.max()) ;
				}
			}
			x += ts.binSize ;
		}
	}
	
	private void prepare() {
		for( BinnedTally i : items )
			prepareChartSeries( i, 1.0 / 3600.0 ) ; // in hours		
	}
	
	void init() {
		chart = new XYDeviationLineChart("Catadupa Traffic by Session Duration", 0.0, "bytes/s", "time(h)") ;
		//chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("bytes/sec")) ;
		chart.setYRange(false, 1, 500) ;
		chart.setXRange(false, 0, Config.Config.MAX_SESSION_DURATION/3600) ;
		chart.setAlpha(0.25);

		
		Gui.addDisplayable(frame, new Displayable() {
			public void displayOn( Canvas canvas) {	
				prepare() ;
				chart.displayOn( canvas) ;
			}
		}, 0.5) ;
		
		Gui.setFrameRectangle(frame, 0, 0, 480, 480) ;
		Gui.setFrameTransform(frame, 500, 500, 0, false) ;		
	}
	
	boolean displayAverageOnly = Globals.get("Catadupa_DisplayAverageOnly", false ) ;
}
