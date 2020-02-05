package livefeeds.twister7.stats.catadupa;

import livefeeds.twister7.CatadupaNode;

import simsim.utils.Persistent;

public class CatadupaRawTraffic extends Persistent {

	public CatadupaTrafficStatistics upload_stats, download_stats, ratio ;

	public CatadupaRawTraffic() {
		ratio = new CatadupaTrafficStatistics("CatadupaRatio") ;
		upload_stats = new CatadupaTrafficStatistics("CatadupaUpload") ;
		download_stats = new CatadupaTrafficStatistics("CatadupaDownload") ;
	}
		
	public void recordNodeTraffic( CatadupaNode node ) {
		double sessionDuration = node.upTime() ;
		
		upload_stats.tally( sessionDuration, node.address.uploadedBytes / sessionDuration) ;
		download_stats.tally(sessionDuration, node.address.downloadedBytes / sessionDuration ) ;
		ratio.tally( sessionDuration, node.address.uploadedBytes / (1 + node.address.downloadedBytes) ) ;
	}
	
	public CatadupaRawTraffic init() {


		return this ;
	}
}
