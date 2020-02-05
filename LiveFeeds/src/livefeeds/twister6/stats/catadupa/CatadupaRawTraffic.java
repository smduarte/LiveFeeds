package livefeeds.twister6.stats.catadupa;

import livefeeds.twister6.CatadupaNode;
import livefeeds.twister6.stats.TrafficStatistics;
import livefeeds.twister6.stats.gui.DeviationBinnedTallyDisplay;

import simsim.utils.Persistent;

public class CatadupaRawTraffic extends Persistent {

	public TrafficStatistics upload_stats ;
	public TrafficStatistics download_stats ;

	public CatadupaRawTraffic() {
		upload_stats = new TrafficStatistics("CatadupaUpload") ;
		download_stats = new TrafficStatistics("CatadupaDownload") ;
	}
		
	public void recordNodeTraffic( CatadupaNode node ) {
		double sessionDuration = node.upTime() ;
		
		upload_stats.tally( sessionDuration, node.address.uploadedBytes / sessionDuration) ;
		download_stats.tally(sessionDuration, node.address.downloadedBytes / sessionDuration ) ;
	}
	
	public CatadupaRawTraffic init() {

		new DeviationBinnedTallyDisplay("Catadupa-Upload", upload_stats) ;
		new DeviationBinnedTallyDisplay("Catadupa-Download", download_stats) ;
		return this ;
	}
}
