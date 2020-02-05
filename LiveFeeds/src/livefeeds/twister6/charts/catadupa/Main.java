package livefeeds.twister6.charts.catadupa;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import livefeeds.twister6.stats.Statistics;

import org.jfree.chart.title.TextTitle;

import simsim.core.Displayable;
import simsim.core.Globals;
import simsim.core.Simulation;
import simsim.ssj.BinnedTally;
import simsim.utils.Persistent;

public class Main extends Simulation implements Displayable {

	public static Map<String, String> translator = new HashMap<String, String>();

	public Main() {
		super(1, null);
	}

	public Main init() {

		// translator.put("casting_u", "Broadcasting") ;
		// translator.put("casting_d", "Broadcasting") ;
		//
		// translator.put("dbEndpoints_u", "Endpoints") ;
		// translator.put("dbEndpoints_d", "Endpoints") ;
		//
		// translator.put("dbFilters_u", "Filters") ;
		// translator.put("dbFilters_d", "Filters") ;
		//
		// translator.put("catDaparture_u", "Departure") ;
		// translator.put("catDaparture_d", "Departure") ;
		//
		// translator.put("dbRepair_u", "Epidemic Repair") ;
		// translator.put("dbRepair_d", "Epidemic Repair") ;
		//
		// translator.put("dbDownload_u", "Database") ;
		// translator.put("dbDownload_d", "Database") ;
		//
		//		
		// translator.put("total upload", "Total") ;
		// translator.put("total download", "Total") ;

		Gui.setDesktopSize(1280, 800);
		// Gui.setFrameRectangle("MainFrame", 0, 0, 320, 320);

		Globals.set("Catadupa_DisplayAverageOnly", true);

		try {
			//foo();
			chart_0_250_500_total();
			//rtrees() ;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.setSimulationMaxTimeWarp(1);
		super.start();
		return this;
	}

	public static void main(String[] args) throws Exception {
		new Main().init();
	}

	private void chart_0_250_500_total() throws Exception {

		String PREFIX = "livefeeds.twister6.config.Catadupa_" ;

		translator.put(PREFIX + "1_0-total upload", "1+0");
		translator.put(PREFIX + "1_0-total download", "1+0");

		translator.put(PREFIX + "1_250-total upload", "1+250");
		translator.put(PREFIX + "1_250-total download", "1+250");

		translator.put(PREFIX + "1_500-total upload", "1+500");
		translator.put(PREFIX + "1_500-total download", "1+500");

		translator.put(PREFIX + "1_1000-total upload", "1+1000");
		translator.put(PREFIX + "1_1000-total download", "1+1000");

		translator.put(PREFIX + "2_0-total upload", "2+0");
		translator.put(PREFIX + "2_0-total download", "2+0");

		translator.put(PREFIX + "2_500-total upload", "2+500");
		translator.put(PREFIX + "2_500-total download", "2+500");

		translator.put(PREFIX + "08_250_LB_95-total upload", "total");
		translator.put(PREFIX + "08_250_LB_95-dbDownload_u", "other uploads");
		translator.put(PREFIX + "08_250_LB_95-casting_u", "DyRT broadcasts");

		translator.put(PREFIX + "08_250_LB_95-total download", "total");
		translator.put(PREFIX + "08_250_LB_95-dbDownload_d", "other downloads");
		translator.put(PREFIX + "08_250_LB_95-casting_d", "DyRT broadcasts");

		
//		Statistics s1_000 = Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_1_0-stats.xml");
//		Statistics s1_250 = Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_1_250-stats.xml");
//		Statistics s1_500 = Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_1_500-stats.xml");
//		Statistics s1_1000 = Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_1_1000-stats.xml");
//
//		Statistics s2_000 = Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_2_0-stats.xml");
//		Statistics s2_500 = Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_2_500-stats.xml");

		Statistics S8_250_LB_75 = Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_08_250_LB_95-stats.xml");

		Font font = new java.awt.Font("Helvetica", Font.BOLD, 18);
		TextTitle uTitle = new TextTitle("", font);
		TextTitle dTitle = new TextTitle("", font);

		List<BinnedTally> tul = new ArrayList<BinnedTally>();
//		tul.add( S8_250_LB_75.catadupaMsgTraffic.upload);
//		tul.add( S8_250_LB_75.catadupaMsgTraffic.casting_u);
//		tul.add( S8_250_LB_75.catadupaRecentAvgTraffic.casting_u);
//		tul.add( S8_250_LB_75.catadupaRecentAvgTraffic.upload);
//		tul.add( S8_250_LB_75.catadupaRecentAvgTraffic.dbDownload_u);

		tul.add(S8_250_LB_75.catadupaMsgTraffic.upload);
		tul.add(S8_250_LB_75.catadupaMsgTraffic.dbDownload_u);
		tul.add(S8_250_LB_75.catadupaMsgTraffic.casting_u);
//		tul.add(S8_250_LB_75.catadupaMsgTraffic.catDaparture_u);
//		tul.add(S8_250_LB_75.catadupaMsgTraffic.dbRepair_u);
		
		
//		tul.add( s1_000.catadupaMsgTraffic.upload);
//		tul.add( s1_250.catadupaMsgTraffic.upload ) ;
//		tul.add( s1_500.catadupaMsgTraffic.upload ) ;
//		tul.add( s1_1000.catadupaMsgTraffic.upload ) ;
//		tul.add( s2_000.catadupaMsgTraffic.upload);
//		tul.add( s2_500.catadupaMsgTraffic.upload ) ;

		List<BinnedTally> tdl = new ArrayList<BinnedTally>();
		tdl.add(S8_250_LB_75.catadupaMsgTraffic.download);
		tdl.add(S8_250_LB_75.catadupaMsgTraffic.dbDownload_d);
		tdl.add(S8_250_LB_75.catadupaMsgTraffic.casting_d);

		//		tdl.add( S8_250_LB_75.catadupaMsgTraffic.casting_d);
//		tdl.add( S8_250_LB_75.catadupaMsgTraffic.download);
//		tdl.add( S8_250_LB_75.catadupaRecentAvgTraffic.casting_d);
//		tdl.add( S8_250_LB_75.catadupaRecentAvgTraffic.download);
		
//		tdl.add( s1_000.catadupaMsgTraffic.download);
//		tdl.add( s1_250.catadupaMsgTraffic.download ) ;
//		tdl.add( s1_500.catadupaMsgTraffic.download ) ;
//		tdl.add( s1_1000.catadupaMsgTraffic.download ) ;
//		tdl.add( s2_000.catadupaMsgTraffic.download);
//		tdl.add( s2_500.catadupaMsgTraffic.download ) ;

		new CatadupaMultiConfigTrafficDeviationChart(uTitle, "cat_rtd_105k_upload", tul).init();
		new CatadupaMultiConfigTrafficDeviationChart(dTitle, "cat_rtd_105k_download", tdl).init();

		Gui.setFrameRectangle("Upload", 0, 0, 512, 512);
		Gui.setFrameRectangle("Download", 520, 0, 512, 512);

	}

	private void foo() throws Exception {
		// Statistics s1_0_LB =
		// Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister5.config.Catadupa_1_0_LB-stats.xml")
		// ;
		// Statistics s8_512_LB_90_90_10 =
		// Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister5.config.Catadupa_8_512_LB_90_90_10-stats.xml")
		// ;

		Statistics s1_512 = Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.config.Catadupa_01_500_RLB-stats.xml");
		// Statistics s1_512_LB =
		// Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_1_512_LB-stats.xml")
		// ;
		// Statistics s1_512_LB_10 =
		// Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_1_512_LB_10-stats.xml")
		// ;
		// Statistics s2_512_LB_75_50 =
		// Persistent.loadFromXml("/Users/smd/runs.16jun/livefeeds.twister6.config.Catadupa_1_512_LB_10-stats.xml")
		// ;
		//
		// Statistics s4_512 =
		// Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister5.config.Catadupa_4_512-stats.xml")
		// ;
		// Statistics s4_512_LB =
		// Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister5.config.Catadupa_4_512_LB-stats.xml")
		// ;
		// Statistics s4_512_LB_10 =
		// Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister5.config.Catadupa_4_512_LB_10-stats.xml")
		// ;
		// Statistics s4_512_LB_90_90_10 =
		// Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister5.config.Catadupa_4_512_LB_90_90_10-stats.xml")
		// ;

		List<BinnedTally> rul = new ArrayList<BinnedTally>();

		rul.add(s1_512.catadupaRecentAvgTraffic.catDaparture_u);
		rul.add(s1_512.catadupaRecentAvgTraffic.dbRepair_u);
		rul.add(s1_512.catadupaRecentAvgTraffic.casting_u);
		rul.add(s1_512.catadupaRecentAvgTraffic.dbDownload_u);

		// ul.add( s1_512.catadupaRecentAvgTraffic.dbEndpoints_u ) ;
		// ul.add( s1_512.catadupaRecentAvgTraffic.dbFilters_u ) ;
		// ul.add( s1_512.catadupaRecentAvgTraffic.upload ) ;

		List<BinnedTally> rdl = new ArrayList<BinnedTally>();
		rdl.add(s1_512.catadupaRecentAvgTraffic.catDaparture_d);
		rdl.add(s1_512.catadupaRecentAvgTraffic.dbRepair_d);
		rdl.add(s1_512.catadupaRecentAvgTraffic.casting_d);
		rdl.add(s1_512.catadupaRecentAvgTraffic.dbDownload_d);

		// dl.add( s1_512.catadupaRecentAvgTraffic.dbEndpoints_d ) ;
		// dl.add( s1_512.catadupaRecentAvgTraffic.dbFilters_d ) ;
		// dl.add( s1_512.catadupaRecentAvgTraffic.download ) ;

		// dl.add( s1_512_LB_90_90_10.catadupaRecentAvgTraffic.dbEndpoints_d ) ;
		// dl.add( s1_512_LB_90_90_10.catadupaRecentAvgTraffic.dbFilters_d ) ;

		Font font = new java.awt.Font("Helvetica", Font.BOLD, 18);
		TextTitle uTitle = new TextTitle("Catadupa traffic by session duration", font);
		TextTitle dTitle = new TextTitle("Download traffic by session duration", font);

		// new CatadupaMultiConfigTrafficChart( uTitle, "mUpload", ul ).init() ;
		// new CatadupaMultiConfigTrafficChart( dTitle, "mDownload", dl ).init()
		// ;
		//
		//				

		// new CatadupaMultiConfigTrafficDeviationChart( uTitle, "dUpload", ul
		// ).init() ;
		// new CatadupaMultiConfigTrafficDeviationChart( dTitle, "dDownload", dl
		// ).init() ;

		new CatadupaMultiConfigTrafficStackedChart(uTitle, "rsUpload", rul).init();
		new CatadupaMultiConfigTrafficStackedChart(dTitle, "rsDownload", rdl).init();

		List<BinnedTally> tul = new ArrayList<BinnedTally>();

		tul.add(s1_512.catadupaMsgTraffic.catDaparture_u);
		tul.add(s1_512.catadupaMsgTraffic.dbRepair_u);
		tul.add(s1_512.catadupaMsgTraffic.casting_u);
		tul.add(s1_512.catadupaMsgTraffic.dbDownload_u);

		List<BinnedTally> tdl = new ArrayList<BinnedTally>();
		tdl.add(s1_512.catadupaMsgTraffic.catDaparture_d);
		tdl.add(s1_512.catadupaMsgTraffic.dbRepair_d);
		tdl.add(s1_512.catadupaMsgTraffic.casting_d);
		tdl.add(s1_512.catadupaMsgTraffic.dbDownload_d);

		new CatadupaMultiConfigTrafficStackedChart(uTitle, "tsUpload", tul).init();
		new CatadupaMultiConfigTrafficStackedChart(dTitle, "tsDownload", tdl).init();

		Gui.setFrameRectangle("Upload", 0, 0, 512, 512);
		Gui.setFrameRectangle("Download", 520, 0, 512, 512);

	}
	
	private void rtrees() throws Exception {
		

		String PREFIX = "livefeeds.twister6.config.Catadupa_" ;

		translator.put(PREFIX + "1_0_ST-CataduapaUpload", "non random trees");
		translator.put(PREFIX + "1_0_RT-CataduapaUpload", "random trees");
		translator.put(PREFIX + "1_0_RTD-CataduapaUpload", "dynamic random trees");
		
		Statistics s_ST = Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_2_250_ST-stats.xml");
		Statistics s_RT = Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_2_250_RT-stats.xml");
		Statistics s_RTD = Persistent.loadFromXml("/Users/smd/runs/livefeeds.twister6.config.Catadupa_2_250_RTD-stats.xml");
		
		List<BinnedTally> rul = new ArrayList<BinnedTally>();

		rul.add(s_RTD.catadupaMsgTraffic.upload);
		rul.add(s_RTD.catadupaMsgTraffic.dbDownload_u);
		rul.add(s_RTD.catadupaMsgTraffic.casting_u);

//		rul.add(s_ST.catadupaMsgTraffic.upload);
//		rul.add(s_RT.catadupaMsgTraffic.upload);
//		rul.add(s_RTD.catadupaMsgTraffic.download);

		Font font = new java.awt.Font("Helvetica", Font.BOLD, 18);
//		TextTitle uTitle = new TextTitle("Upload traffic by session duration", font);
//		TextTitle dTitle = new TextTitle("Download traffic by session duration", font);

		TextTitle uTitle = new TextTitle("", font);
		new CatadupaMultiConfigTrafficDeviationChart(uTitle, "xxxrtd_26k_download", rul).init();

	}
}
