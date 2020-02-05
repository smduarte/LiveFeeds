package livefeeds.sift0.stats;

import static livefeeds.sift0.config.Config.Config;
import static simsim.logging.Log.Log;

import java.io.File;

import livefeeds.sift0.CatadupaNode;
import livefeeds.sift0.GlobalDB;
import livefeeds.sift0.config.Config;
import livefeeds.sift0.stats.catadupa.CatadupaFanoutStats;
import livefeeds.sift0.stats.catadupa.CatadupaMsgTraffic;
import livefeeds.sift0.stats.catadupa.CatadupaRawTraffic;
import livefeeds.sift0.stats.catadupa.CatadupaRecentAvgTraffic;
import livefeeds.sift0.stats.catadupa.CatadupaRepairStats;
import livefeeds.sift0.stats.catadupa.CatadupaTimeStats;
import livefeeds.sift0.stats.turmoil.TurmoilEventStats;

import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.utils.Persistent;

import umontreal.iro.lecuyer.stat.Tally;

public class Statistics extends Persistent {

	public Config config ;
	public CatadupaTimeStats timeStats ;
	public CatadupaFanoutStats fanoutStats ;
	public CatadupaRepairStats repairStats ;
	public CatadupaMsgTraffic catadupaMsgTraffic ;
	public CatadupaRawTraffic catadupaRawTraffic ;
	public CatadupaRecentAvgTraffic catadupaRecentAvgTraffic ;
	
	public TurmoilEventStats evStats ;
	
	public Tally catadupaDuplicates = new Tally("CatadupaDuplicates") ;

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
		
		//evStats = new TurmoilEventStats().init() ;

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
		
		
		
		return this ;
	}
		
	public static Statistics Statistics = new Statistics() ;
}
