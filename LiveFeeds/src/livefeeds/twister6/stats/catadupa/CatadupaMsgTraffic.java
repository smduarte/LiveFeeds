package livefeeds.twister6.stats.catadupa;

import livefeeds.twister6.CatadupaNode;
import livefeeds.twister6.stats.Traffic;
import livefeeds.twister6.stats.TrafficStatistics;
import livefeeds.twister6.stats.gui.StackedBinnedTallyDisplay;

import simsim.utils.Persistent;

public class CatadupaMsgTraffic extends Persistent {

	public TrafficStatistics casting_u, casting_d;
	public TrafficStatistics dbRepair_u, dbRepair_d;
	public TrafficStatistics dbDownload_u, dbDownload_d;
	public TrafficStatistics dbEndpoints_u, dbEndpoints_d;
	public TrafficStatistics dbFilters_u, dbFilters_d;

	public TrafficStatistics catDaparture_u, catDaparture_d;

	public TrafficStatistics download, upload;

	public CatadupaMsgTraffic() {
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

		new StackedBinnedTallyDisplay("Catadupa-sUpload", catDaparture_u, dbRepair_u, casting_u, dbEndpoints_u, dbFilters_u);
		new StackedBinnedTallyDisplay("Catadupa-sDownload", catDaparture_d, dbRepair_d, casting_d, dbEndpoints_d, dbFilters_d);
		
		return this ;
	}
}

