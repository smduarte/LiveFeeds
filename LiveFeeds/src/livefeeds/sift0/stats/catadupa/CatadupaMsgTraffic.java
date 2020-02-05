package livefeeds.sift0.stats.catadupa;

import static livefeeds.sift0.config.Config.Config;
import livefeeds.sift0.CatadupaNode;

import simsim.ssj.charts.StackedBinnedTallyDisplay;
import simsim.ssj.charts.DeviationBinnedTallyDisplay;
import simsim.ssj.charts.DoubleChart;
import simsim.utils.Persistent;

public class CatadupaMsgTraffic extends Persistent {

	public CatadupaTrafficStatistics casting_u, casting_d;
	public CatadupaTrafficStatistics slicecasting_u, slicecasting_d;
	public CatadupaTrafficStatistics dbRepair_u, dbRepair_d;
	public CatadupaTrafficStatistics dbDownload_u, dbDownload_d;
	public CatadupaTrafficStatistics dbEndpoints_u, dbEndpoints_d;
	public CatadupaTrafficStatistics dbFilters_u, dbFilters_d;

	public CatadupaTrafficStatistics catDaparture_u, catDaparture_d;

	public CatadupaTrafficStatistics download, upload, ratio;

	public CatadupaMsgTraffic() {
		casting_u = new CatadupaTrafficStatistics("casting_u") ;
		casting_d = new CatadupaTrafficStatistics("casting_d") ;

		slicecasting_u = new CatadupaTrafficStatistics("slicecasting_u") ;
		slicecasting_d = new CatadupaTrafficStatistics("slicecasting_d") ;

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
		
		ratio = new CatadupaTrafficStatistics("ratio") ;
		upload = new CatadupaTrafficStatistics("total upload") ;
		download = new CatadupaTrafficStatistics("total download") ;
	}
	
	
	public void recordNodeTraffic( CatadupaNode node ) {
		CatadupaTraffic t = node.state.stats.catadupaTraffic ;
		
		double upTime = node.upTime() ;

		dbDownload_u.tally(upTime, t.db_upload() / upTime);
		dbDownload_d.tally(upTime, t.db_download() / upTime) ;
		
		dbRepair_u.tally(upTime, t.repair_upload() / upTime);
		dbRepair_d.tally(upTime, t.repair_download() / upTime);
		
		casting_u.tally(upTime, t.casting_upload() / upTime );
		casting_d.tally(upTime, t.casting_download() / upTime);		

		slicecasting_u.tally(upTime, t.slicecasting_upload() / upTime );
		slicecasting_d.tally(upTime, t.slicecasting_download() / upTime);		

		dbEndpoints_u.tally(upTime, t.endpoints_upload() / upTime);
		dbEndpoints_d.tally(upTime, t.endpoints_download() / upTime) ;

		dbFilters_u.tally(upTime, t.filters_upload() / upTime);
		dbFilters_d.tally(upTime, t.filters_download() / upTime) ;
		
		catDaparture_u.tally(upTime, t.departure_upload() / upTime);
		catDaparture_d.tally(upTime, t.departure_download() / upTime) ;
		
		ratio.tally(upTime, t.upload() / t.download() );
		upload.tally(upTime, t.upload() / upTime );
		download.tally(upTime, t.download() / upTime ) ;

	}
	
	public CatadupaMsgTraffic init() {
/*
		StackedBinnedTallyDisplay chart1 = new StackedBinnedTallyDisplay(null, catDaparture_u, dbRepair_u, casting_u, slicecasting_u, dbEndpoints_u, dbFilters_u) {
			protected void init() {
				super.init();
				super.setUnitsScale( 1/3600.0, 1.0) ;
				chart.chart().setTitle("MSG Catadupa-Upload") ;
				chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis()) ;
				chart.setYRange(false, 1, 10000) ;
				chart.setXRange(false, 0, Config.MAX_SESSION_DURATION/3600) ;
				chart.setAxisLabels("session duration (h)", "bytes / s");
				super.replaceLegend() ;
			}
		};
		
		StackedBinnedTallyDisplay chart2 = new StackedBinnedTallyDisplay(null, catDaparture_d, dbRepair_d, casting_d, slicecasting_d, dbEndpoints_d, dbFilters_d) {
			protected void init() {
				super.init();
				super.setUnitsScale( 1/3600.0, 1.0) ;
				chart.chart().setTitle("MSG Catadupa-Download") ;
				chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis()) ;
				chart.setYRange(false, 1, 10000) ;
				chart.setXRange(false, 0, Config.MAX_SESSION_DURATION/3600) ;
				chart.setAxisLabels("session duration (h)", "bytes / s");
				super.replaceLegend() ;
			}
		};
		
		new DeviationBinnedTallyDisplay("MSG Catadupa-Ratio", ratio) {
			protected void init() {
				super.init();
				super.setUnitsScale( 1/3600.0, 1.0) ;
				chart.chart().setTitle("MSG Catadupa-Ratio") ;
				chart.setYRange(false, 0.1, 5.0) ;
				chart.setXRange(false, 0, Config.MAX_SESSION_DURATION/3600) ;
			}
		};
		
		new DoubleChart("MSG-Catadupa Traffic" , false, "Catadupa Traffic", "Upload", "Download", chart1, chart2) ;
		*/
		return this ;
	}
}

