package livefeeds.rtrees.stats;

import static livefeeds.rtrees.config.Config.Config;

import java.io.File;

import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.GlobalDB;
import livefeeds.rtrees.config.Config;
import livefeeds.rtrees.stats.broadcast.BroadcastMsgLiveTraffic;
import livefeeds.rtrees.stats.broadcast.BroadcastMsgTraffic;
import livefeeds.rtrees.stats.broadcast.BroadcastTreeStats;
import livefeeds.rtrees.stats.catadupa.CatadupaFanoutStats;
import livefeeds.rtrees.stats.catadupa.CatadupaMsgTraffic;
import livefeeds.rtrees.stats.catadupa.CatadupaRawTraffic;
import livefeeds.rtrees.stats.catadupa.CatadupaRecentAvgTraffic;
import livefeeds.rtrees.stats.catadupa.CatadupaRepairStats;
import livefeeds.rtrees.stats.catadupa.CatadupaTimeStats;

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
	
	public BroadcastMsgTraffic broadcastMsgTraffic ;
	public BroadcastMsgLiveTraffic broadcastMsgLiveTraffic ;
	public BroadcastTreeStats treeStats ;
	
	public void recordNodeTraffic( CatadupaNode node ) {
		if( node.upTime() < 1 * 60 || ! node.state.db.loadedFilters ) return ;
	
		
		catadupaMsgTraffic.recordNodeTraffic(node);
		catadupaRawTraffic.recordNodeTraffic(node);
		catadupaRecentAvgTraffic.recordNodeTraffic(node) ;
		
//		broadcastMsgTraffic.recordNodeTraffic( node ) ;
	}
	
	public Statistics init() {

		config = Config ;
		timeStats = new CatadupaTimeStats() ;
		fanoutStats = new CatadupaFanoutStats() ;
		repairStats = new CatadupaRepairStats() ;
		catadupaMsgTraffic = new CatadupaMsgTraffic().init() ;
		catadupaRawTraffic = new CatadupaRawTraffic().init() ;
		catadupaRecentAvgTraffic = new CatadupaRecentAvgTraffic().init() ;

//		broadcastMsgTraffic = new BroadcastMsgTraffic().init() ;
		broadcastMsgLiveTraffic = new BroadcastMsgLiveTraffic().init() ;

		treeStats = new BroadcastTreeStats() ;
		
		new PeriodicTask(300, 300 ) {
			public void run() {
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
				System.err.printf("Time: %.2fh Nodes:%d/%d (%.0f%%))\n", Simulation.currentTime()/3600, GlobalDB.size(), GlobalDB.deadNodes.size(), 100.0 * GlobalDB.deadNodes().size() / GlobalDB.size() ) ;
			}
		};
		
		
		
		return this ;
	}
		
	public static Statistics Statistics = new Statistics() ;
}
