package livefeeds.twister6.stats.catadupa;

import livefeeds.twister6.CatadupaNode;
import livefeeds.twister6.stats.Traffic;
import livefeeds.twister6.stats.TrafficStatistics;
import livefeeds.twister6.stats.gui.DeviationBinnedTallyDisplay;
import livefeeds.twister6.stats.gui.StackedBinnedTallyDisplay;

import simsim.utils.Persistent;

public class CatadupaRecentAvgTraffic extends Persistent {

	public TrafficStatistics casting_u, casting_d;
	public TrafficStatistics dbRepair_u, dbRepair_d;
	public TrafficStatistics dbDownload_u, dbDownload_d;
	public TrafficStatistics dbEndpoints_u, dbEndpoints_d;
	public TrafficStatistics dbFilters_u, dbFilters_d;
	public TrafficStatistics catDaparture_u, catDaparture_d;

	public TrafficStatistics download, upload;

	public CatadupaRecentAvgTraffic() {
		casting_u = new TrafficStatistics("casting_u") ;
		casting_d = new TrafficStatistics("casting_d") ;

		dbRepair_u = new TrafficStatistics("dbRepair_u") ;
		dbRepair_d = new TrafficStatistics("dbRepair_d") ;

		dbDownload_u = new TrafficStatistics("dbDownload_u") ;
		dbDownload_d = new TrafficStatistics("dbDownload_d") ;

		dbEndpoints_u = new TrafficStatistics("dbEndpoints_u") ;
		dbEndpoints_d = new TrafficStatistics("dbEndpoints_d") ;

		dbFilters_u = new TrafficStatistics("dbFilters_u") ;
		dbFilters_d = new TrafficStatistics("dbFilters_d") ;

		catDaparture_u = new TrafficStatistics("catDaparture_u") ;
		catDaparture_d = new TrafficStatistics("catDaparture_d") ;

		upload = new TrafficStatistics("total upload") ;
		download = new TrafficStatistics("total download") ;
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

//		new StackedBinnedTallyDisplay("Catadupa-recUpload", catDaparture_u, dbRepair_u, casting_u, dbEndpoints_u, dbFilters_u);
//		new StackedBinnedTallyDisplay("Catadupa-recDownload", catDaparture_d, dbRepair_d, casting_d, dbEndpoints_d, dbFilters_d);

		new DeviationBinnedTallyDisplay("Catadupa-recUpload", catDaparture_u, dbRepair_u, casting_u, dbEndpoints_u, dbFilters_u);
		new DeviationBinnedTallyDisplay("Catadupa-recDownload", catDaparture_d, dbRepair_d, casting_d, dbEndpoints_d, dbFilters_d);

		return this ;
	}
}
