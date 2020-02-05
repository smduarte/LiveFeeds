package livefeeds.twister7.stats;

import static livefeeds.twister7.config.Config.Config;
import static simsim.logging.Log.Log;

import java.io.File;

import livefeeds.twister7.CatadupaNode;
import livefeeds.twister7.GlobalDB;
import livefeeds.twister7.config.Config;
import livefeeds.twister7.stats.catadupa.CatadupaFanoutStats;
import livefeeds.twister7.stats.catadupa.CatadupaMsgTraffic;
import livefeeds.twister7.stats.catadupa.CatadupaRawTraffic;
import livefeeds.twister7.stats.catadupa.CatadupaRecentAvgTraffic;
import livefeeds.twister7.stats.catadupa.CatadupaRepairStats;
import livefeeds.twister7.stats.catadupa.CatadupaTimeStats;
import livefeeds.twister7.stats.turmoil.TurmoilEventStats;

import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.ssj.charts.DeviationBinnedTallyDisplay;
import simsim.ssj.charts.DoubleChart;
import simsim.utils.Persistent;

public class Statistics extends Persistent {

	public Config config ;
	public CatadupaTimeStats timeStats ;
	public CatadupaFanoutStats fanoutStats ;
	public CatadupaRepairStats repairStats ;
	public CatadupaMsgTraffic catadupaMsgTraffic ;
	public CatadupaRawTraffic catadupaRawTraffic ;
	public CatadupaRecentAvgTraffic catadupaRecentAvgTraffic ;
	
	public TurmoilEventStats evStats ;
	
	public void recordCatadupaTraffic( CatadupaNode node ) {
		if( node.upTime() < 1 * 60 || ! node.state.db.loadedFilters ) return ;
	
		
		catadupaMsgTraffic.recordNodeTraffic(node);
		catadupaRawTraffic.recordNodeTraffic(node);
		catadupaRecentAvgTraffic.recordNodeTraffic(node) ;
	}
	
	public Statistics init() {

		config = Config ;
		timeStats = new CatadupaTimeStats() ;
		fanoutStats = new CatadupaFanoutStats() ;
		repairStats = new CatadupaRepairStats() ;
		catadupaMsgTraffic = new CatadupaMsgTraffic().init() ;
		catadupaRawTraffic = new CatadupaRawTraffic().init() ;
		catadupaRecentAvgTraffic = new CatadupaRecentAvgTraffic().init() ;
		
		evStats = new TurmoilEventStats().init() ;

		new PeriodicTask(300, 300 ) {
			double rt = Simulation.realTime() ;
			public void run() {
				double now = Simulation.realTime() ;
				if( now - rt < 60 ) return ;
				rt = now ;
				
				String basePath = Config.saveFilename;
				File tmp = new File( basePath + ".tmp" ) ; ;
				File fok = new File( basePath) ;
				try {
					saveXmlTo( tmp.getAbsolutePath() ) ;
					tmp.renameTo( fok ) ;
				} catch( Exception x ) {
					x.printStackTrace() ;
				}				
			}
		} ;
		

		new PeriodicTask(300) {
			public void run() {
				for( CatadupaNode i : GlobalDB.nodes ) {
					i.state.stats.turmoilTraffic.updateHistory() ;
					i.state.stats.catadupaTraffic.updateHistory() ;
				}
			}
		};

		new PeriodicTask(60) {
			public void run() {
				for( CatadupaNode i : GlobalDB.nodes )
					i.state.stats.churn.updateHistory() ;
			}
		};

		
		new PeriodicTask(30) {
			public void run() {
				
//				System.out.println( repairStats.repairReplies.report() ) ;
//				System.out.println( fanoutStats.fanout.report() ) ;
//				System.out.println( timeStats.castLatency.report() ) ;
//				System.out.println( timeStats.time2join.report() ) ;
				Log.info(String.format("Time: %.2fh Nodes:%d/%d (%.0f%%))\n", Simulation.currentTime()/3600, GlobalDB.size(), GlobalDB.deadNodes.size(), 100.0 * GlobalDB.deadNodes().size() / GlobalDB.size() )) ;
			}
		};
		
	/*	
		DeviationBinnedTallyDisplay chart1 = new DeviationBinnedTallyDisplay(null, Statistics.catadupaRawTraffic.upload_stats, Statistics.catadupaMsgTraffic.upload) {
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
		DeviationBinnedTallyDisplay chart2 = new DeviationBinnedTallyDisplay(null, Statistics.catadupaRawTraffic.download_stats, Statistics.catadupaMsgTraffic.download) {
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
*/
		
		return this ;
	}
		
	public static Statistics Statistics = new Statistics() ;
}
