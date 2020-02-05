package livefeeds.rtrees.charts.catadupa;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import livefeeds.rtrees.stats.Statistics;

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

			chart_0_250_500_total();

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

		String PREFIX = "livefeeds.rtrees.config.Broadcasting_" ;

		translator.put(PREFIX + "RR_2_ST-total upload", "  f = 2  ");
		translator.put(PREFIX + "RR_3_RT_D-total upload", "  f = 3  ");
		translator.put(PREFIX + "RR_4_RT_D-total upload", "  f = 4  ");
		translator.put(PREFIX + "RR_5_RT_D-total upload", "  f = 5  ");

		Statistics RR_2_ST = Persistent.loadFromXml("/Users/smd/runs/livefeeds.rtrees.config.Broadcasting_RR_2_ST-stats.xml");
//		Statistics RR_3_ST = Persistent.loadFromXml("/Users/smd/runs/livefeeds.rtrees.config.Broadcasting_RR_3_RT_D-stats.xml");
//		Statistics RR_4_ST = Persistent.loadFromXml("/Users/smd/runs/livefeeds.rtrees.config.Broadcasting_RR_4_RT_D-stats.xml");
//		Statistics RR_5_ST = Persistent.loadFromXml("/Users/smd/runs/livefeeds.rtrees.config.Broadcasting_RR_5_RT_D-stats.xml");


		Font font = new java.awt.Font("Helvetica", Font.BOLD, 18);
		TextTitle uTitle = new TextTitle("", font);
		TextTitle dTitle = new TextTitle("Download traffic by session duration", font);

		List<BinnedTally> tul = new ArrayList<BinnedTally>();

		tul.add( RR_2_ST.treeStats.latency);
		
//		tul.add( RR_2_ST.broadcastMsgLiveTraffic.upload);
//		tul.add( RR_3_ST.broadcastMsgLiveTraffic.upload);
//		tul.add( RR_4_ST.broadcastMsgLiveTraffic.upload);
//		tul.add( RR_5_ST.broadcastMsgLiveTraffic.upload);

		List<BinnedTally> tdl = new ArrayList<BinnedTally>();
//		tdl.add( RR_2_ST.broadcastMsgLiveTraffic.download);

		new CatadupaMultiConfigTrafficDeviationChart(uTitle, "dUpload", tul).init();
//		new CatadupaMultiConfigTrafficDeviationChart(dTitle, "dDownload", tdl).init();

		Gui.setFrameRectangle("Upload", 0, 0, 512, 512);
		Gui.setFrameRectangle("Download", 520, 0, 512, 512);

	}

}
