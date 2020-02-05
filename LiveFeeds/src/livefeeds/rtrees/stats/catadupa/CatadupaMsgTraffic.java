package livefeeds.rtrees.stats.catadupa;

import static simsim.core.Simulation.Gui;

import java.util.ArrayList;
import java.util.List;

import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.stats.Traffic;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.charts.XYDeviationLineChart;
import simsim.gui.charts.XYStackedAreaChart;
import simsim.utils.Persistent;

import umontreal.iro.lecuyer.stat.Tally;

public class CatadupaMsgTraffic extends Persistent {

	public CatadupaTrafficStatistics casting_u, casting_d;
	public CatadupaTrafficStatistics dbRepair_u, dbRepair_d;
	public CatadupaTrafficStatistics dbDownload_u, dbDownload_d;
	public CatadupaTrafficStatistics dbEndpoints_u, dbEndpoints_d;
	public CatadupaTrafficStatistics dbFilters_u, dbFilters_d;

	public CatadupaTrafficStatistics catDaparture_u, catDaparture_d;

	public CatadupaTrafficStatistics download, upload;

	public CatadupaMsgTraffic() {
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

		dbDownload_u.tally(upTime, t.db_upload() / upTime);
		dbDownload_d.tally(upTime, t.db_download() / upTime) ;
		
		dbRepair_u.tally(upTime, t.repair_upload() / upTime);
		dbRepair_d.tally(upTime, t.repair_download() / upTime);
		
		casting_u.tally(upTime, t.casting_upload() / upTime );
		casting_d.tally(upTime, t.casting_download() / upTime);		
		
		dbEndpoints_u.tally(upTime, t.endpoints_upload() / upTime);
		dbEndpoints_d.tally(upTime, t.endpoints_download() / upTime) ;

		dbFilters_u.tally(upTime, t.filters_upload() / upTime);
		dbFilters_d.tally(upTime, t.filters_download() / upTime) ;
		
		catDaparture_u.tally(upTime, t.departure_upload() / upTime);
		catDaparture_d.tally(upTime, t.departure_download() / upTime) ;
		
		upload.tally(upTime, t.upload() / upTime );
		download.tally(upTime, t.download() / upTime ) ;

	}
	
	public CatadupaMsgTraffic init() {

//		new CatadupaMsgTrafficDisplay("Catadupa-sUpload", casting_u );
//		new CatadupaMsgTrafficDisplay("Catadupa-sDownload", casting_d);

//		new CatadupaStackedTrafficDisplay("Catadupa-sUpload", catDaparture_u, dbRepair_u, casting_u, dbEndpoints_u, dbFilters_u);
//		new CatadupaStackedTrafficDisplay("Catadupa-sDownload", catDaparture_d, dbRepair_d, casting_d, dbEndpoints_d, dbFilters_d);

		return this ;
	}
}

class CatadupaMsgTrafficDisplay {
	
	private String frame ;
	private XYDeviationLineChart chart ;
	private List<CatadupaTrafficStatistics> items = new ArrayList<CatadupaTrafficStatistics>() ;
	
	public CatadupaMsgTrafficDisplay( String frame, CatadupaTrafficStatistics ... args ) {
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
		YIntervalSeries a = (YIntervalSeries)chart.getSeries(ts.name) ;
//		YIntervalSeries b = (YIntervalSeries)chart.getSeries(ts.name+"_minmax") ;
		
		a.clear() ;
//		b.clear() ;
		chart.copySeriesColors( ts.name, ts.name + "_minmax");

		double x = ts.SAMPLE_DURATION/2 ;
		for( Tally i : ts.bins ) {
			if( i.numberObs() > 2) {
				double y = 100 * i.standardDeviation() / i.average() ;
				//double l = Math.max( i.min(), i.average() - i.standardDeviation()), h = Math.min( i.max(), i.average() + i.standardDeviation() );
				a.add(x * xAxisUnitMultiplier, y, y, y ) ;
//				a.add(x * xAxisUnitMultiplier, y, l, h) ;
//				b.add(x * xAxisUnitMultiplier, y, i.min(), i.max()) ;
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
		//chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("bytes/sec")) ;
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
}

class CatadupaStackedTrafficDisplay {
	
	private String frame ;
	private XYStackedAreaChart chart ;
	private List<CatadupaTrafficStatistics> items = new ArrayList<CatadupaTrafficStatistics>() ;
	
	public CatadupaStackedTrafficDisplay( String frame, CatadupaTrafficStatistics ... args ) {
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
				a.addOrUpdate(x * xAxisUnitMultiplier,  i.standardDeviation() ) ;
			}
			x += ts.SAMPLE_DURATION ;
		}
	}
	
	private void prepare() {
		for( CatadupaTrafficStatistics i : items )
			prepareChartSeries( i, 1.0 / 3600.0 ) ; // in hours		
	}
	
	void init() {
		chart = new XYStackedAreaChart("Catadupa Traffic by Session Duration", 0.0, "bytes/s", "time(h)") ;
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
