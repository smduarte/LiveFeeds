package livefeeds.rtrees.stats.catadupa;

import static simsim.core.Simulation.Gui;

import java.util.ArrayList;
import java.util.List;

import livefeeds.rtrees.CatadupaNode;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.data.xy.YIntervalSeries;

import simsim.core.Displayable;
import simsim.core.Globals;
import simsim.gui.canvas.Canvas;
import simsim.gui.charts.XYDeviationLineChart;
import simsim.utils.Persistent;

import umontreal.iro.lecuyer.stat.Tally;

public class CatadupaRawTraffic extends Persistent {

	public CatadupaTrafficStatistics upload_stats ;
	public CatadupaTrafficStatistics download_stats ;

	public CatadupaRawTraffic() {
		upload_stats = new CatadupaTrafficStatistics("CatadupaUpload") ;
		download_stats = new CatadupaTrafficStatistics("CatadupaDownload") ;
	}
		
	public void recordNodeTraffic( CatadupaNode node ) {
		double sessionDuration = node.upTime() ;
		
		upload_stats.tally( sessionDuration, node.address.uploadedBytes / sessionDuration) ;
		download_stats.tally(sessionDuration, node.address.downloadedBytes / sessionDuration ) ;
	}
	
	public CatadupaRawTraffic init() {

//		new CatadupaRawTrafficDisplay("Catadupa-Upload", upload_stats) ;
//		new CatadupaRawTrafficDisplay("Catadupa-Download", download_stats) ;
		return this ;
	}
}

class CatadupaRawTrafficDisplay {
	
	private String frame ;
	private XYDeviationLineChart chart ;
	private List<CatadupaTrafficStatistics> items = new ArrayList<CatadupaTrafficStatistics>() ;
	
	public CatadupaRawTrafficDisplay( String frame, CatadupaTrafficStatistics ... args ) {
		this.frame = frame ;
		for( Object i : args ) 
			items.add( (CatadupaTrafficStatistics) i) ;
		
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
	
	public void prepareChartSeries( CatadupaTrafficStatistics ts, double xAxisUnitMultiplier) {
		YIntervalSeries a = (YIntervalSeries)chart.getSeries(ts.name) ;
		YIntervalSeries b = (YIntervalSeries)chart.getSeries(ts.name + "_minmax") ;
		
		a.clear() ;
		b.clear() ;
		chart.copySeriesColors(ts.name, ts.name + "_minmax");

		double x = ts.SAMPLE_DURATION/2 ;
		for( Tally i : ts.bins ) {
			if( i.numberObs() > 2) {
				double y = i.average() ;
				double l = Math.max( i.min(), i.average() - i.standardDeviation()), h = Math.min( i.max(), i.average() + i.standardDeviation() );
				if( displayAverageOnly ) {
					a.add(x * xAxisUnitMultiplier, y, y, y) ;	
				} else {
					a.add(x * xAxisUnitMultiplier, y, l, h) ;
					b.add(x * xAxisUnitMultiplier, y, i.min(), i.max()) ;
				}
			}
			x += ts.SAMPLE_DURATION ;
		}
	}
	
	private void prepare() {
		for( CatadupaTrafficStatistics i : items )
			prepareChartSeries( i, 1.0 / 3600.0 ) ; // in hours		
	}
	
	void init() {
		chart = new XYDeviationLineChart("Catadupa Traffic by Session Duration", 0.0, "bytes/s", "time(h)") ;
		chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("bytes/sec")) ;
		chart.setYRange(false, 1, 50) ;
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
