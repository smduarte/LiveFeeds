package livefeeds.sift0.stats.catadupa;

import static livefeeds.sift0.config.Config.Config;

import org.jfree.chart.axis.LogarithmicAxis;

import livefeeds.sift0.CatadupaNode;

import simsim.ssj.charts.DeviationBinnedTallyDisplay;
import simsim.ssj.charts.DoubleChart;
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
		ratio.tally( sessionDuration, node.address.uploadedBytes / node.address.downloadedBytes) ;
	}
	
	public CatadupaRawTraffic init() {

		DeviationBinnedTallyDisplay chart1 = new DeviationBinnedTallyDisplay(null, upload_stats) {
			protected void init() {
				super.init();
				super.setUnitsScale( 1/3600.0, 1.0) ;
				chart.chart().setTitle("RAW Catadupa-Upload") ;
				chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("")) ;
				chart.setYRange(false, 1, 10000) ;
				chart.setXRange(false, 0, Config.MAX_SESSION_DURATION/3600) ;
				chart.setAxisLabels("session duration (h)", "bytes / s");
			}
			
		};
		DeviationBinnedTallyDisplay chart2 = new DeviationBinnedTallyDisplay(null, download_stats) {
			protected void init() {
				super.init();
				super.setUnitsScale( 1/3600.0, 1.0) ;
				chart.chart().setTitle("RAW Catadupa-Download") ;
				chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("")) ;
				chart.setYRange(false, 1, 10000) ;
				chart.setXRange(false, 0, Config.MAX_SESSION_DURATION/3600) ;
				chart.setAxisLabels("session duration (h)", "bytes / s");
			}
		};
		
		
		new DoubleChart("RAW-Catadupa Traffic" , false, "Catadupa Traffic", "Upload", "Download", chart1, chart2) ;

		return this ;
	}
}
