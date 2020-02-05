package livefeeds.sift0.charts;

import java.awt.Font;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.*;

import livefeeds.sift0.stats.Statistics;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;

import simsim.core.Displayable;
import simsim.core.Simulation;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.ssj.BinnedTally;
import simsim.ssj.charts.*;
import simsim.utils.Persistent;


public class Catadupa extends Simulation implements Displayable {

	public static Map<String, String> translator = new HashMap<String, String>();

	public Catadupa() {
		super(1, null);
	}

	public Catadupa init() {

		Gui.setDesktopSize(1280, 800);

		Gui.setFrameRectangle("foo", 0, 0, 500, 400) ;
		try {
//			raw_averages_4_8_250_38() ;
//			late_average_rates();
//
//			detailed_rates() ;
			
//			total_averages();
//			detailed_rates15min();
//			total_averages() ;
			
			sessionRatios() ;
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.setSimulationMaxTimeWarp(1);
		super.start();
		return this;
	}

	public static void main(String[] args) throws Exception {
		new Catadupa().init();
	}

	static final String CAT_PREFIX = "livefeeds.twister7.config.Catadupa_";

	String cat_filename(String f) {
		return "/tmp/" + CAT_PREFIX + f + "-stats.xml";
	}

	static String t(String s) {
		String res = translator.get(s);
		return res == null ? s : res;
	}

	private void total_averages() throws Exception {

		Statistics S0 = Persistent.loadFromXml(cat_filename("A4_M8_250_00_38"));
		Statistics S00 = Persistent.loadFromXml(cat_filename("A4_M8_500_00_38"));
		Statistics S1 = Persistent.loadFromXml(cat_filename("A8_M16_250_00_19"));
		Statistics S11 = Persistent.loadFromXml(cat_filename("A8_M16_500_00_19"));
		Statistics S2 = Persistent.loadFromXml(cat_filename("A2_M4_250_00_76"));
		Statistics S22 = Persistent.loadFromXml(cat_filename("A2_M4_500_00_76"));

//		Statistics SX = Persistent.loadFromXml(cat_filename("A4_M8_500_00_38_NOLB"));

		List<BinnedTally> ta = new ArrayList<BinnedTally>();

		add(ta, S22.catadupaMsgTraffic.upload.ratio(S11.catadupaMsgTraffic.upload),"(B) 2h - 7.6");
		add(ta, S00.catadupaMsgTraffic.upload.ratio(S11.catadupaMsgTraffic.upload),"(A) 4h - 3.8");
//		add(ta, S11.catadupaMsgTraffic.upload.ratio(S1.catadupaMsgTraffic.upload),"(C) 8h - 1.9");


		List<BinnedTally> tb = new ArrayList<BinnedTally>();

		add(tb, S22.catadupaMsgTraffic.download.ratio(S11.catadupaMsgTraffic.download),"(B) 2h - 7.6");
		add(tb, S00.catadupaMsgTraffic.download.ratio(S11.catadupaMsgTraffic.download),"(A) 4h - 3.8");
//		add(tb, S11.catadupaMsgTraffic.download.ratio(S1.catadupaMsgTraffic.download),"(C) 8h - 1.9");

		BinnedTallyDisplay mcu = new BinnedTallyDisplay(ta);
		//mcu.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis());
		commonConfig(mcu, "Catadupa Upload / Download Ratios", "", "", "session duration (h)");
		mcu.chart().setXRange(false, 0.1, 4);
		mcu.chart().setYRange(false, 0.0, 6);

		BinnedTallyDisplay mcd = new BinnedTallyDisplay(tb);
//		mcd.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis());
		commonConfig(mcd, "Catadupa Session Download Rate", "", "", "session duration (h)");
		mcd.chart().setXRange(false, 0.1, 16);
		mcd.chart().setYRange(false, 0.0, 6);

		mcu.saveChart("/tmp/charts/cat_50k_avg_session_upload_rate.pdf");
		mcd.saveChart("/tmp/charts/cat_50k_avg_session_download_rate.pdf");

		new DoubleChart("cat_tot_averages", true, "Catadupa Traffic Averages", "Upload","Download", mcu, mcd).saveChart("/tmp/charts/cat_50k_rates.pdf") ;		

//		new DoubleVerticalChart("Cat_Sessions", "Catadupa Upload / Download Ratios", "entire session","last 15 minutes", mcu, mcd).saveChart("/tmp/charts/cat_50k_session_ratios.pdf") ;		
	}

	
	private void total_averages2() throws Exception {

		Statistics S0 = Persistent.loadFromXml(cat_filename("A4_M8_250_00_38"));
		Statistics S1 = Persistent.loadFromXml(cat_filename("A4_M8_250_50_38"));
		Statistics S2 = Persistent.loadFromXml(cat_filename("A4_M8_250_75_38"));
		Statistics S3 = Persistent.loadFromXml(cat_filename("A4_M8_250_95_38"));

		Statistics S4 = Persistent.loadFromXml(cat_filename("A4_M8_500_00_38"));
		Statistics S5 = Persistent.loadFromXml(cat_filename("A4_M8_500_50_38"));
		Statistics S6 = Persistent.loadFromXml(cat_filename("A4_M8_500_75_38"));
		Statistics S7 = Persistent.loadFromXml(cat_filename("A4_M8_500_95_38"));

		changeNames(S0, "250-0%");
		changeNames(S1, "250-50%");
		changeNames(S2, "250-75%");
		changeNames(S3, "250-95%");
		changeNames(S4, "500-0%");
		changeNames(S5, "500-50%");
		changeNames(S6, "500-75%");
		changeNames(S7, "500-95%");

		changeNames(S0, "250-0%");
		changeNames(S1, "250-50%");
		changeNames(S2, "250-75%");
		changeNames(S3, "250-95%");
		changeNames(S4, "500-0%");
		changeNames(S5, "500-50%");
		changeNames(S6, "500-75%");
		changeNames(S7, "500-95%");

		List<BinnedTally> tul = new ArrayList<BinnedTally>();

		tul.add(S0.catadupaRawTraffic.upload_stats);
		tul.add(S1.catadupaRawTraffic.upload_stats);
		tul.add(S2.catadupaRawTraffic.upload_stats);
		tul.add(S3.catadupaRawTraffic.upload_stats);
		tul.add(S4.catadupaRawTraffic.upload_stats);
		tul.add(S5.catadupaRawTraffic.upload_stats);
		tul.add(S6.catadupaRawTraffic.upload_stats);
		tul.add(S7.catadupaRawTraffic.upload_stats);

		List<BinnedTally> tdl = new ArrayList<BinnedTally>();

		tdl.add(S0.catadupaRawTraffic.download_stats);
		tdl.add(S1.catadupaRawTraffic.download_stats);
		tdl.add(S2.catadupaRawTraffic.download_stats);
		tdl.add(S3.catadupaRawTraffic.download_stats);
		tdl.add(S4.catadupaRawTraffic.download_stats);
		tdl.add(S5.catadupaRawTraffic.download_stats);
		tdl.add(S6.catadupaRawTraffic.download_stats);
		tdl.add(S7.catadupaRawTraffic.download_stats);

		DeviationBinnedTallyDisplay mcu = new DeviationBinnedTallyDisplay("Upload", tul);
		mcu.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis("YYY"));
		mcu.chart().setXRange(false, 0.1, 8);
		mcu.chart().setYRange(false, 100, 15000);
		commonConfig(mcu, "Catadupa Session Upload Rate", "~ 50 000 nodes", "log byte/s", "session duration (h)");

		mcu.saveChart("/tmp/charts/cat_50k_avg_upload_rate.pdf");

		DeviationBinnedTallyDisplay mcd = new DeviationBinnedTallyDisplay("Download", tdl);

		mcd.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis("YYY"));
		mcd.chart().setXRange(false, 0.1, 8);
		mcd.chart().setYRange(false, 100, 15000);
		commonConfig(mcd, "Catadupa Session Download Rate", "~ 50 000 nodes", "log byte/s", "session duration (h)");

		mcd.saveChart("/tmp/charts/cat_50k_avg_download_rate.pdf");
	}

	private void late_average_rates() throws Exception {

		Statistics S0 = Persistent.loadFromXml(cat_filename("A4_M8_250_00_38"));
		Statistics S1 = Persistent.loadFromXml(cat_filename("A4_M8_250_50_38"));
		Statistics S2 = Persistent.loadFromXml(cat_filename("A4_M8_250_75_38"));
		Statistics S3 = Persistent.loadFromXml(cat_filename("A4_M8_250_95_38"));

		Statistics S4 = Persistent.loadFromXml(cat_filename("A4_M8_500_00_38"));
		Statistics S5 = Persistent.loadFromXml(cat_filename("A4_M8_500_50_38"));
		Statistics S6 = Persistent.loadFromXml(cat_filename("A4_M8_500_75_38"));
		Statistics S7 = Persistent.loadFromXml(cat_filename("A4_M8_500_95_38"));

		changeNames(S0, "250-0%");
		changeNames(S1, "250-50%");
		changeNames(S2, "250-75%");
		changeNames(S3, "250-95%");
		changeNames(S4, "500-0%");
		changeNames(S5, "500-50%");
		changeNames(S6, "500-75%");
		changeNames(S7, "500-95%");

		List<BinnedTally> tul = new ArrayList<BinnedTally>();
		tul.add(S0.catadupaRecentAvgTraffic.upload);
		tul.add(S1.catadupaRecentAvgTraffic.upload);
		// tul.add(S2.catadupaRecentAvgTraffic.upload);
		tul.add(S3.catadupaRecentAvgTraffic.upload);
		tul.add(S4.catadupaRecentAvgTraffic.upload);
		tul.add(S5.catadupaRecentAvgTraffic.upload);
		// tul.add(S6.catadupaRecentAvgTraffic.upload);
		tul.add(S7.catadupaRecentAvgTraffic.upload);

		List<BinnedTally> tdl = new ArrayList<BinnedTally>();

		tdl.add(S0.catadupaRecentAvgTraffic.download);
		tdl.add(S1.catadupaRecentAvgTraffic.download);
		// tdl.add(S2.catadupaRecentAvgTraffic.download);
		tdl.add(S3.catadupaRecentAvgTraffic.download);
		tdl.add(S4.catadupaRecentAvgTraffic.download);
		tdl.add(S5.catadupaRecentAvgTraffic.download);
		// tdl.add(S6.catadupaRecentAvgTraffic.download);
		tdl.add(S7.catadupaRecentAvgTraffic.download);

		DeviationBinnedTallyDisplay mcu = new DeviationBinnedTallyDisplay(null, tul);
		mcu.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis("log y"));
		mcu.chart().setXRange(false, 0, 8);
		mcu.chart().setYRange(false, 100, 15000);

		commonConfig(mcu, "Catadupa Session Upload Rates", "(last 15 minutes)", "byte / s", "session duration (h)");
		mcu.chart().setAlpha(0.120);

		mcu.saveChart("/tmp/charts/cat_late_upload_rate.pdf");

		DeviationBinnedTallyDisplay mcd = new DeviationBinnedTallyDisplay(null, tdl);
		mcd.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis("log y"));
		mcd.chart().setXRange(false, 0, 8);
		mcd.chart().setYRange(false, 100, 15000);

		commonConfig(mcd, "Catadupa Session Download Rates", "(last 15 minutes)", "byte / s", "session duration (h)");

		mcd.chart().setAlpha(0.120);

		mcd.saveChart("/tmp/charts/cat_late_download_rate.pdf");
		

	}



	private void detailed_rates15min() throws Exception {

		Statistics S0 = Persistent.loadFromXml(cat_filename("A4_M8_250_00_38"));
		
		List<BinnedTally> tul = new ArrayList<BinnedTally>();
		add(tul, S0.catadupaRecentAvgTraffic.catDaparture_u,"departures");
		add(tul, S0.catadupaRecentAvgTraffic.dbRepair_u,"repair");
		add(tul, S0.catadupaRecentAvgTraffic.dbEndpoints_u,"endpoints");
		add(tul, S0.catadupaRecentAvgTraffic.dbFilters_u, "filters");
		add(tul, S0.catadupaRecentAvgTraffic.casting_u, "broadcasting");

		List<BinnedTally> tdl = new ArrayList<BinnedTally>();
		add(tdl, S0.catadupaRecentAvgTraffic.catDaparture_d,"departures");
		add(tdl, S0.catadupaRecentAvgTraffic.dbRepair_d,"repair");
		add(tdl, S0.catadupaRecentAvgTraffic.dbEndpoints_d,"endpoints");
		add(tdl, S0.catadupaRecentAvgTraffic.dbFilters_d, "filters");
		add(tdl, S0.catadupaRecentAvgTraffic.casting_d, "broadcasting");

		DeviationBinnedTallyDisplay mcu = new StackedBinnedTallyDisplay(tul);
		
		mcu.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis(""));
		commonConfig(mcu, "Catadupa Session Upload Rate", "(last 15 minutes)", "byte / s", "session duration (h)");
		mcu.chart().setXRange(false, 10/60.0, 8);
		mcu.chart().setYRange(false, 10, 15000);
		
		mcu.saveChart("/tmp/charts/cat_detail_upload_rate.pdf");

		DeviationBinnedTallyDisplay mcd = new StackedBinnedTallyDisplay(tdl);
		
		mcd.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis(""));
		commonConfig(mcd, "Catadupa Session Download Rate", "(last 15 minutes)", "byte / s", "session duration (h)");
		mcd.chart().setXRange(false, 10/60.0, 8);
		mcd.chart().setYRange(false, 10, 15000);

		mcd.saveChart("/tmp/charts/cat_detail_download_rate.pdf");
		
		new DoubleChart("Cat_Details", true, "Catadupa Communication Costs", "Upload","Download", mcu, mcd).saveChart("/tmp/charts/cat_50k_detailed_rates.pdf") ;		
	}

	private void detailed_rates() throws Exception {

		Statistics S0 = Persistent.loadFromXml(cat_filename("A4_M8_250_00_38"));
		
		List<BinnedTally> tul = new ArrayList<BinnedTally>();
		add(tul, S0.catadupaMsgTraffic.catDaparture_u,"departures");
		add(tul, S0.catadupaMsgTraffic.dbRepair_u,"repair");
		add(tul, S0.catadupaMsgTraffic.dbEndpoints_u,"endpoints");
		add(tul, S0.catadupaMsgTraffic.dbFilters_u, "filters");
		add(tul, S0.catadupaMsgTraffic.casting_u, "broadcasting");

		List<BinnedTally> tdl = new ArrayList<BinnedTally>();
		add(tdl, S0.catadupaMsgTraffic.catDaparture_d,"departures");
		add(tdl, S0.catadupaMsgTraffic.dbRepair_d,"repair");
		add(tdl, S0.catadupaMsgTraffic.dbEndpoints_d,"endpoints");
		add(tdl, S0.catadupaMsgTraffic.dbFilters_d, "filters");
		add(tdl, S0.catadupaMsgTraffic.casting_d, "broadcasting");

		DeviationBinnedTallyDisplay mcu = new StackedBinnedTallyDisplay(tul);
		
		mcu.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis(""));
		commonConfig(mcu, "Catadupa Session Upload Rate", "", "byte / s", "session duration (h)");
		mcu.chart().setXRange(false, 10/60.0, 8);
		mcu.chart().setYRange(false, 10, 15000);
		
		mcu.saveChart("/tmp/charts/cat_detail_upload_rate.pdf");

		DeviationBinnedTallyDisplay mcd = new StackedBinnedTallyDisplay(tdl);
		
		mcd.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis(""));
		commonConfig(mcd, "Catadupa Session Download Rate", "", "byte / s", "session duration (h)");
		mcd.chart().setXRange(false, 10/60.0, 8);
		mcd.chart().setYRange(false, 10, 15000);

		mcd.saveChart("/tmp/charts/cat_detail_download_rate.pdf");
		
		new DoubleChart("Cat_TOT_Details", true, "Catadupa Communication Costs", "Upload","Download", mcu, mcd).saveChart("/tmp/charts/cat_50k_detailed_rates.pdf") ;		
	}
	

	private void systemSize1() throws Exception {

		Statistics S0 = Persistent.loadFromXml(cat_filename("A4_M8_250_75_38"));
		Statistics S1 = Persistent.loadFromXml(cat_filename("A4_M8_500_75_38"));
		Statistics S2 = Persistent.loadFromXml(cat_filename("A8_M16_250_75_19"));
		Statistics S3 = Persistent.loadFromXml(cat_filename("A8_M16_500_75_19"));
		Statistics S4 = Persistent.loadFromXml(cat_filename("A2_M4_250_75_76"));
		Statistics S5 = Persistent.loadFromXml(cat_filename("A2_M4_500_75_76"));


		changeNames(S0, "(A) 4h/250/3.8");
		changeNames(S1, "(B) 4h/500/3.8");
		changeNames(S2, "(C) 8h/250/1.9");
		changeNames(S3, "(D) 8h/500/1.9");
		changeNames(S4, "(E) 2h/250/7.6");
		changeNames(S5, "(F) 2h/500/7.6");

		List<BinnedTally> tul = new ArrayList<BinnedTally>();

		int SAMPLES = 6 ;

		tul.add(S0.catadupaRawTraffic.upload_stats.resample(SAMPLES));
		tul.add(S1.catadupaRawTraffic.upload_stats.resample(SAMPLES));
		tul.add(S2.catadupaRawTraffic.upload_stats.resample(SAMPLES));
		tul.add(S3.catadupaRawTraffic.upload_stats.resample(SAMPLES));
		tul.add(S4.catadupaRawTraffic.upload_stats.resample(SAMPLES));
		tul.add(S5.catadupaRawTraffic.upload_stats.resample(SAMPLES));

		List<BinnedTally> tdl = new ArrayList<BinnedTally>();

		tdl.add(S0.catadupaRawTraffic.download_stats.resample(SAMPLES));
		tdl.add(S1.catadupaRawTraffic.download_stats.resample(SAMPLES));
		tdl.add(S2.catadupaRawTraffic.download_stats.resample(SAMPLES));
		tdl.add(S3.catadupaRawTraffic.download_stats.resample(SAMPLES));
		tdl.add(S4.catadupaRawTraffic.download_stats.resample(SAMPLES));
		tdl.add(S5.catadupaRawTraffic.download_stats.resample(SAMPLES));

		DeviationBinnedTallyDisplay mcu = new DeviationBinnedTallyDisplay("System Size Upload", tul);

		mcu.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis("log by"));
		mcu.chart().setXRange(false, 0.1, 16);
		mcu.chart().setYRange(false, 100, 15000);
		commonConfig(mcu, "Catadupa Session Upload Rate", " ~ 50 000 nodes / 75% filter redundancy", "bytes / s", "session duration (h)");

		//mcu.chart().chart().getXYPlot().setRangeAxisLocation( AxisLocation.TOP_OR_RIGHT) ;

		DeviationBinnedTallyDisplay mcd = new DeviationBinnedTallyDisplay("System Size Download", tdl);

		mcd.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis("log by"));
		mcd.chart().setXRange(false, 0.1, 16);
		mcd.chart().setYRange(false, 100, 15000);
		commonConfig(mcd, "Catadupa Session Download Rate", "~ 50 000 nodes / 75% filter redundancy", "bytes / s", "session duration (h)");

		mcu.saveChart("/tmp/charts/cat_50k_avg_session_upload_rate.pdf");
		mcd.saveChart("/tmp/charts/cat_50k_avg_session_download_rate.pdf");

//		new DoubleVerticalChart("Cat_Sessions", "Catadupa Traffic Rates", "Upload","Download", mcu, mcd).saveChart("/tmp/charts/cat_50k_session_rates.pdf") ;		
	}

	private void systemSize2() throws Exception {

		Statistics S0 = Persistent.loadFromXml(cat_filename("A4_M8_250_00_38"));
		Statistics S1 = Persistent.loadFromXml(cat_filename("A4_M8_500_00_38"));
		Statistics S2 = Persistent.loadFromXml(cat_filename("A8_M16_250_00_19"));
		Statistics S3 = Persistent.loadFromXml(cat_filename("A8_M16_500_00_19"));
		Statistics S4 = Persistent.loadFromXml(cat_filename("A2_M4_250_00_76"));
		Statistics S5 = Persistent.loadFromXml(cat_filename("A2_M4_500_00_76"));


		changeNames(S0, "(A) 4h/250/3.8");
		changeNames(S2, "(C) 8h/250/1.9");
		changeNames(S4, "(B) 2h/250/7.6");

//		changeNames(S1, "(B) 4h/500/3.8");
//		changeNames(S3, "(D) 8h/500/1.9");
//		changeNames(S5, "(F) 2h/500/7.6");

		List<BinnedTally> tul = new ArrayList<BinnedTally>();

		int SAMPLES = 6 ;

		tul.add(S0.catadupaRecentAvgTraffic.upload.ratio( S0.catadupaRecentAvgTraffic.download).resample(SAMPLES));
		tul.add(S2.catadupaRecentAvgTraffic.upload.ratio( S2.catadupaRecentAvgTraffic.download).resample(SAMPLES));
		tul.add(S4.catadupaRecentAvgTraffic.upload.ratio( S4.catadupaRecentAvgTraffic.download).resample(SAMPLES));

		List<BinnedTally> tdl = new ArrayList<BinnedTally>();

		tul.add(S0.catadupaRecentAvgTraffic.upload.ratio( S0.catadupaRecentAvgTraffic.download).resample(SAMPLES));
		tul.add(S2.catadupaRecentAvgTraffic.upload.ratio( S2.catadupaRecentAvgTraffic.download).resample(SAMPLES));
		tul.add(S4.catadupaRecentAvgTraffic.upload.ratio( S4.catadupaRecentAvgTraffic.download).resample(SAMPLES));

		BinnedTallyDisplay mcu = new BinnedTallyDisplay("System Size Upload", tul);

//		mcu.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis("log by"));
		mcu.chart().setXRange(false, 0.1, 16);
		mcu.chart().setYRange(false, 0, 5);
		commonConfig(mcu, "Catadupa Session Upload Rate", "", "bytes / s", "session duration (h)");

		//mcu.chart().chart().getXYPlot().setRangeAxisLocation( AxisLocation.TOP_OR_RIGHT) ;

		BinnedTallyDisplay mcd = new BinnedTallyDisplay("System Size Download", tdl);

//		mcd.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis("log by"));
		mcd.chart().setXRange(false, 0.1, 16);
		mcd.chart().setYRange(false, 100, 5000);
		commonConfig(mcd, "Catadupa Session Download Rate", "", "bytes / s", "session duration (h)");

		mcu.saveChart("/tmp/charts/cat_50k_avg_session_upload_rate.pdf");
		mcd.saveChart("/tmp/charts/cat_50k_avg_session_download_rate.pdf");

//		new DoubleVerticalChart("Cat_Sessions", "Catadupa Traffic Rates", "Upload","Download", mcu, mcd).saveChart("/tmp/charts/cat_50k_session_rates.pdf") ;		
	}
	
	private void sessionRatios() throws Exception {

		Statistics SX = Persistent.loadFromXml(cat_filename("A4_M8_250_00_38_NOLB"));

		Statistics S0 = Persistent.loadFromXml(cat_filename("A4_M8_250_00_38"));
		Statistics S1 = Persistent.loadFromXml(cat_filename("A8_M16_250_00_19"));
		Statistics S2 = Persistent.loadFromXml(cat_filename("A2_M4_250_00_76"));


		changeNames(S0, "(A) 4h - 3.8");
		changeNames(S1, "(C) 8h - 1.9");
		changeNames(S2, "(B) 2h - 7.6");
		changeNames(SX, "(X) 4h - 3.8 - unbiased");

		List<BinnedTally> ta = new ArrayList<BinnedTally>();

		ta.add(S0.catadupaMsgTraffic.ratio);
		ta.add(S1.catadupaMsgTraffic.ratio);
		ta.add(S2.catadupaMsgTraffic.ratio);
		ta.add(SX.catadupaMsgTraffic.ratio);

		List<BinnedTally> tb = new ArrayList<BinnedTally>();

		tb.add(S0.catadupaRecentAvgTraffic.ratio);
		tb.add(S1.catadupaRecentAvgTraffic.ratio);
		tb.add(S2.catadupaRecentAvgTraffic.ratio);
		tb.add(SX.catadupaRecentAvgTraffic.ratio);

		DeviationBinnedTallyDisplay mcu = new DeviationBinnedTallyDisplay(ta);

		commonConfig(mcu, "Catadupa Upload / Download Ratios", "", "", "session duration (h)");
		mcu.chart().setShapeSpacing(8) ;
		mcu.chart().setXRange(false, 0.1, 16);
		mcu.chart().setYRange(false, 0.1, 3.5);

		BinnedTallyDisplay mcd = new BinnedTallyDisplay(tb);

		commonConfig(mcd, "Catadupa Session Download Rate", "", "", "session duration (h)");
		mcd.chart().setXRange(false, 0.1, 16);
		mcd.chart().setYRange(false, 0.1, 3.5);
		mcd.chart().setShapeSpacing(8) ;

		mcu.saveChart("/tmp/charts/cat_50k_avg_session_upload_rate.pdf");
		mcd.saveChart("/tmp/charts/cat_50k_avg_session_download_rate.pdf");

		new DoubleChart("Cat_Sessions", true, "Catadupa Upload / Download Ratios", "entire session","last 15 minutes", mcu, mcd).saveChart("/tmp/charts/cat_50k_session_ratios.pdf") ;		
	}

	
	
	void commonConfig(AbstractBinnedTallyDisplay<?> chart, String title, String subtitle, String rangeLabel, String domainLabel) {
		chart.setUnitsScale(1.0 / 3600.0, 1.0);
		Stroke s = new Pen(RGB.BLACK, 1).stroke;
		for (BinnedTally i : chart.items()) {
			chart.chart().setSeriesLinesAndShapes(i.name(), true, true);
			chart.chart().setSeriesStroke(i.name(), s);
		}
		chart.chart().setAlpha(0.20);
		Font font = new java.awt.Font("Helvetica", Font.BOLD, 18);
		chart.chart().chart().setTitle(new TextTitle(title, font));

		if (subtitle.length() > 0)
			chart.chart().chart().addSubtitle(new TextTitle(subtitle, font.deriveFont(Font.PLAIN, 16f)));

		chart.chart().setShapeSpacing(5) ;
		chart.chart().chart().setAntiAlias(true);
		chart.chart().chart().setTextAntiAlias(true);
		chart.chart().chart().getXYPlot().getRangeAxis().setLabel(rangeLabel);
		chart.chart().chart().getXYPlot().getDomainAxis().setLabel(domainLabel);
		chart.chart().chart().getXYPlot().getRangeAxis().setLabelFont(font.deriveFont(15f));
		chart.chart().chart().getXYPlot().getDomainAxis().setLabelFont(font.deriveFont(15f));
		chart.chart().chart().getXYPlot().getRangeAxis().setTickLabelFont( font.deriveFont( Font.PLAIN, 14f) ) ;
		chart.chart().chart().getXYPlot().getDomainAxis().setTickLabelFont( font.deriveFont( Font.PLAIN, 14f) ) ;

		chart.chart().chart().getLegend().setFrame(BlockBorder.NONE);
		chart.chart().chart().getLegend().setItemFont(font.deriveFont(Font.PLAIN, 16f));

	}

	void changeNames(Object o, String value) {
		try {
			Set<Object> done = new HashSet<Object>() ;
			
			HashSet<Object> remaining = new HashSet<Object>() ;
			remaining.add(o) ;
			

			while( remaining.size() > 0 ) {

				List<Object> extras = new ArrayList<Object>() ;
				for( Iterator<?> it = remaining.iterator() ; it.hasNext() ; ) {
					Object i = it.next() ;
					done.add(i) ;
					it.remove() ;
					
					for( Field ii : i.getClass().getFields()) {
						Object j = ii.get(i) ;
						if( j instanceof BinnedTally) {
							((BinnedTally)j).name = value ;
						}
						else
							if( j != null && ! done.contains(j) && j.getClass().getName().startsWith("livefeeds") )
								extras.add(j) ;
					}
				}
				remaining.addAll( extras ) ;
			}
		} catch( Exception x ) {
			x.printStackTrace();
		}
	}	
			
	void add( List<BinnedTally> l, BinnedTally bt, String newName ) {
		bt.name = newName ;
		l.add( bt ) ;
	}
	
	void add( int samples, List<BinnedTally> l, BinnedTally bt, String newName ) {
		bt.name = newName ;
		l.add( bt ) ;
	}
}