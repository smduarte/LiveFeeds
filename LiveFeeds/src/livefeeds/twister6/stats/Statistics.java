package livefeeds.twister6.stats;

import static livefeeds.twister6.config.Config.Config;
import static simsim.logging.Log.Log;

import java.io.File;

import livefeeds.twister6.CatadupaNode;
import livefeeds.twister6.GlobalDB;
import livefeeds.twister6.config.Config;
import livefeeds.twister6.stats.catadupa.CatadupaFanoutStats;
import livefeeds.twister6.stats.catadupa.CatadupaMsgTraffic;
import livefeeds.twister6.stats.catadupa.CatadupaRawTraffic;
import livefeeds.twister6.stats.catadupa.CatadupaRecentAvgTraffic;
import livefeeds.twister6.stats.catadupa.CatadupaRepairStats;
import livefeeds.twister6.stats.catadupa.CatadupaTimeStats;

import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.utils.Persistent;

public class Statistics extends Persistent {

	public Config config ;
	public CatadupaTimeStats timeStats ;
	public CatadupaFanoutStats fanoutStats ;
	public CatadupaRepairStats repairStats ;
	public CatadupaMsgTraffic catadupaMsgTraffic ;
	public CatadupaRawTraffic catadupaRawTraffic ;
	public CatadupaRecentAvgTraffic catadupaRecentAvgTraffic ;
	
	public void recordNodeTraffic( CatadupaNode node ) {
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
				for( CatadupaNode i : GlobalDB.nodes )
					i.state.stats.traffic.updateHistory() ;
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
		
		
		
		return this ;
	}
		
	public static Statistics Statistics = new Statistics() ;
}
