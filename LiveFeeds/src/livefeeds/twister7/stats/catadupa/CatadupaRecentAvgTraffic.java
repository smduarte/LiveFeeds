package livefeeds.twister7.stats.catadupa;

import static livefeeds.twister7.config.Config.Config;

import org.jfree.chart.axis.LogarithmicAxis;

import livefeeds.twister7.CatadupaNode;

import simsim.ssj.charts.DeviationBinnedTallyDisplay;
import simsim.ssj.charts.DoubleChart;
import simsim.utils.Persistent;

public class CatadupaRecentAvgTraffic extends Persistent {

	public CatadupaTrafficStatistics casting_u, casting_d;
	public CatadupaTrafficStatistics dbRepair_u, dbRepair_d;
	public CatadupaTrafficStatistics dbDownload_u, dbDownload_d;
	public CatadupaTrafficStatistics dbEndpoints_u, dbEndpoints_d;
	public CatadupaTrafficStatistics dbFilters_u, dbFilters_d;
	public CatadupaTrafficStatistics catDaparture_u, catDaparture_d;

	public CatadupaTrafficStatistics download, upload, ratio;

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

		ratio = new CatadupaTrafficStatistics("total ratio") ;

	}
	
	
	public void recordNodeTraffic( CatadupaNode node ) {
		CatadupaTraffic t = node.state.stats.catadupaTraffic ;
		
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

		ratio.tally( upTime, t.recent_upload_rate() / t.recent_download_rate() ) ;
	}
	
	public CatadupaRecentAvgTraffic init() {

//		new StackedBinnedTallyDisplay("Catadupa-recUpload", catDaparture_u, dbRepair_u, casting_u, dbEndpoints_u, dbFilters_u);
//		new StackedBinnedTallyDisplay("Catadupa-recDownload", catDaparture_d, dbRepair_d, casting_d, dbEndpoints_d, dbFilters_d);

		DeviationBinnedTallyDisplay chart1 = new DeviationBinnedTallyDisplay(null, catDaparture_u, dbRepair_u, casting_u, dbEndpoints_u, dbFilters_u, upload) {
			protected void init() {
				super.init();
				super.setUnitsScale( 1/3600.0, 1.0) ;
				chart.chart().setTitle("RCNT Catadupa-Upload") ;
				chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("")) ;
				chart.setYRange(false, 1, 10000) ;
				chart.setXRange(false, 0, Config.MAX_SESSION_DURATION/3600) ;
				chart.setAxisLabels("session duration (h)", "bytes / s");
			}
		};
		DeviationBinnedTallyDisplay chart2 = new DeviationBinnedTallyDisplay(null, catDaparture_d, dbRepair_d, casting_d, dbEndpoints_d, dbFilters_d, download) {
			protected void init() {
				super.init();
				super.setUnitsScale( 1/3600.0, 1.0) ;
				chart.chart().setTitle("RCNT Catadupa-Download") ;
				chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("")) ;
				chart.setYRange(false, 1, 10000) ;
				chart.setXRange(false, 0, Config.MAX_SESSION_DURATION/3600) ;
				chart.setAxisLabels("session duration (h)", "bytes / s");
			}
		};
/*
		new DeviationBinnedTallyDisplay("RCNT Catadupa-Ratio", ratio) {
			protected void init() {
				super.init();
				super.setUnitsScale( 1/3600.0, 1.0) ;
				chart.chart().setTitle("Catadupa Recent Ratio") ;
				chart.setYRange(false, 0.1, 5.0) ;
				chart.setXRange(false, 0, Config.MAX_SESSION_DURATION/3600) ;
			}
		};
*/		
		new DoubleChart("RCNT-Catadupa Traffic" , false, "Catadupa Traffic", "Upload", "Download", chart1, chart2) ;
		return this ;
	}
}
