package livefeeds.rtrees.stats.catadupa;

import static simsim.core.Simulation.Gui;

import java.util.ArrayList;
import java.util.List;

import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.stats.Traffic;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;

import simsim.core.Displayable;
import simsim.gui.InputHandler0;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.charts.XYDeviationLineChart;
import simsim.gui.charts.XYStackedAreaChart;
import simsim.gui.geom.XY;
import simsim.utils.Persistent;

import umontreal.iro.lecuyer.stat.Tally;

public class CatadupaRecentAvgTraffic extends Persistent {

	public CatadupaTrafficStatistics casting_u, casting_d;
	public CatadupaTrafficStatistics dbRepair_u, dbRepair_d;
	public CatadupaTrafficStatistics dbDownload_u, dbDownload_d;
	public CatadupaTrafficStatistics dbEndpoints_u, dbEndpoints_d;
	public CatadupaTrafficStatistics dbFilters_u, dbFilters_d;
	public CatadupaTrafficStatistics catDaparture_u, catDaparture_d;

	public CatadupaTrafficStatistics download, upload;

	public CatadupaRecentAvgTraffic() {
		casting_u = new CatadupaTrafficStatistics("casting_u") ;
		casting_d = new CatadupaTrafficStatistics("casting_d") ;

		dbRepair_u = new CatadupaTrafficStatistics("dbRepair_u") ;
		dbRepair_d = new CatadupaTrafficStatistics("dbRepair_d") ;

		dbDownload_u = new CatadupaTrafficStatistics("dbDownload_u") ;
		dbDownload_d = new CatadupaTrafficStatistics("dbDownload_d") ;

		dbEndpoints_u = new CatadupaTrafficStatistics("dbEndpoints_u") ;
		dbEndpoints_d = new CatadupaTrafficStatistics("dbEndpoints_d") ;

		dbFilters_u = new CatadupaTrafficStatistics("dbFilters_u") ;
		dbFilters_d = new CatadupaTrafficStatistics("dbFilters_d") ;

		catDaparture_u = new CatadupaTrafficStatistics("catDaparture_u") ;
		catDaparture_d = new CatadupaTrafficStatistics("catDaparture_d") ;

		upload = new CatadupaTrafficStatistics("total upload") ;
		download = new CatadupaTrafficStatistics("total download") ;
	}
	
	
	public void recordNodeTraffic( CatadupaNode node ) {
		Traffic t = node.state.stats.traffic ;
		
		double upTime = node.upTime() ;

		dbDownload_u.tally(upTime, t.recent_db_upload_rate());
		dbDownload_d.tally(upTime, t.recent_db_download_rate()) ;
		
		dbRepair_u.tally(upTime, t.recent_repair_upload_rate());
		dbRepair_d.tally(upTime, t.recent_repair_download_rate());
		
		casting_u.tally(upTime, t.recent_casting_upload_rate() );		
		casting_d.tally(upTime, t.recent_casting_download_rate() );
		
		dbEndpoints_u.tally(upTime, t.recent_endpoints_upload_rate());
		dbEndpoints_d.tally(upTime, t.recent_endpoints_download_rate()) ;

		dbFilters_u.tally(upTime, t.recent_filters_upload_rate());
		dbFilters_d.tally(upTime, t.recent_filters_download_rate()) ;
		
		catDaparture_u.tally(upTime, t.recent_departure_upload_rate());
		catDaparture_d.tally(upTime, t.recent_departure_download_rate()) ;

		upload.tally(upTime, t.recent_upload_rate());
		download.tally(upTime, t.recent_download_rate()) ;

	}
	
	public CatadupaRecentAvgTraffic init() {

//		new CatadupaRecentAvgTrafficDisplay("Catadupa-recUpload", catDaparture_u, casting_u, dbRepair_u, dbEndpoints_u, dbFilters_u, upload);
//		new CatadupaRecentAvgTrafficDisplay("Catadupa-recDownload", catDaparture_d, casting_d, dbRepair_d, dbEndpoints_d, dbFilters_d, download);
		
		return this ;
	}
}

class CatadupaRecentAvgTrafficDisplay {
	
	private String frame ;
	private XYDeviationLineChart chart ;
	private List<CatadupaTrafficStatistics> items = new ArrayList<CatadupaTrafficStatistics>() ;
	
	public CatadupaRecentAvgTrafficDisplay( String frame, CatadupaTrafficStatistics ... args ) {
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
	
	
	private void prepareChartSeries( CatadupaTrafficStatistics ts, double xAxisUnitMultiplier) {
		YIntervalSeries a = (YIntervalSeries)chart.getSeries(ts.name ) ;
//		YIntervalSeries b = (YIntervalSeries)chart.getSeries(ts.name()+"_minmax") ;
		
		a.clear() ;
//		b.clear() ;
//		chart.copySeriesColors(ts.name(), ts.name() + "_minmax");

		double x = ts.SAMPLE_DURATION/2 ;
		for( Tally i : ts.bins ) {
			if( i.numberObs() > 2) {
				double y = i.average() ;
				double l = Math.max( i.min(), i.average() - i.standardDeviation()), h = Math.min( i.max(), i.average() + i.standardDeviation() );
				a.add(x * xAxisUnitMultiplier, y, l, h) ;
				//b.add(x * xAxisUnitMultiplier, y, i.min(), i.max()) ;
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
		chart.setYRange(false, 1, 10000) ;
		chart.setAlpha(0.25);

		
		Gui.addDisplayable(frame, new Displayable() {
			public void displayOn( Canvas canvas) {	
				prepare() ;
				chart.displayOn( canvas) ;
			}
		}, 0.5) ;
		Gui.addInputHandler(frame, new InputHandler0() {
			boolean toggle = false ;
			public void onMouseClick(int button, XY pu, XY ps) {
				toggle = !toggle ;
				if( toggle ) {
					chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("log bytes/sec ")) ;
					chart.setYRange(false, 1, 10000) ;
				}
				else
					chart.chart().getXYPlot().setRangeAxis( new NumberAxis("bytes/sec")) ;
			}
		}) ;
		Gui.setFrameRectangle(frame, 0, 0, 480, 480) ;
		Gui.setFrameTransform(frame, 500, 500, 0, false) ;		
	}
}


class CatadupaStackedRecentAvgTrafficDisplay {
	
	private String frame ;
	private XYStackedAreaChart chart ;
	private List<CatadupaTrafficStatistics> items = new ArrayList<CatadupaTrafficStatistics>() ;
	
	public CatadupaStackedRecentAvgTrafficDisplay( String frame, CatadupaTrafficStatistics ... args ) {
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
	
	synchronized private void prepareChartSeries( CatadupaTrafficStatistics ts, double xAxisUnitMultiplier) {
		XYSeries a = chart.getSeries(ts.name) ;
		
		a.clear() ;
		
		double x = ts.SAMPLE_DURATION/2 ;
		for( Tally i : ts.bins ) {
			if( i.numberObs() > 2) {
				a.addOrUpdate(x * xAxisUnitMultiplier,  i.average() ) ;
			}
			x += ts.SAMPLE_DURATION ;
		}
	}
	
	private void prepare() {
		for( CatadupaTrafficStatistics i : items )
			prepareChartSeries( i, 1.0 / 3600.0 ) ; // in hours		
	}
	
	void init() {
		chart = new XYStackedAreaChart("Recent Catadupa Traffic by Session Duration", 0.0, "bytes/s", "time(h)") ;
		chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("bytes/sec")) ;
		chart.setYRange(false, 1, 10000) ;
   
		Pen p0 = new Pen(new RGB(0,1,0,0.25));
		Pen p1 = new Pen(new RGB(0,0,1,0.25)) ;
		Pen p2 = new Pen(new RGB(1,0,0,0.25)) ;
		Pen p3 = new Pen(new RGB(1,0.7,0,0.33)) ;
		Pen p4 = new Pen(new RGB(0,0.7,1,0.33)) ;
	
		Pen[] pens = new Pen[] {p0, p1, p2, p3, p4} ;
		
		for( int i = 0 ; i < pens.length ; i++ )
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
