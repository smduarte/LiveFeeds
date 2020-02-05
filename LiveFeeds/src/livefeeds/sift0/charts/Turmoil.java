package livefeeds.sift0.charts;

import java.awt.Font;
import java.awt.Stroke;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import livefeeds.twister7.stats.Statistics;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.title.TextTitle;

import simsim.core.Displayable;
import simsim.core.Globals;
import simsim.core.Simulation;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.ssj.BinnedTally;
import simsim.ssj.charts.AbstractBinnedTallyDisplay;
import simsim.ssj.charts.BinnedTallyDisplay;
import simsim.ssj.charts.DeviationBinnedTallyDisplay;
import simsim.utils.Persistent;

import umontreal.iro.lecuyer.stat.Tally;

public class Turmoil extends Simulation implements Displayable {

	public static Map<String, String> translator = new HashMap<String, String>();

	public Turmoil() {
		super(1, null);
	}

	public Turmoil init() {


		Gui.setDesktopSize(1280, 800);

		try {
			turmoil_width_cdf();
			turmoil_net_load();
			turmoil_cpu_load();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.setSimulationMaxTimeWarp(1);
		super.start();
		return this;
	}

	public static void main(String[] args) throws Exception {
		new Turmoil().init();
	}


	static final String TUR_PREFIX = "livefeeds.twister7.config.Turmoil_";

	String tur_filename(String f) {
		return "/tmp/" + TUR_PREFIX + f + "-stats.xml";
	}

	private void turmoil_width_cdf() throws Exception {


		Statistics S0 = Persistent.loadFromXml(tur_filename("1_t"));
		Statistics S1 = Persistent.loadFromXml(tur_filename("2_t"));
		Statistics S2 = Persistent.loadFromXml(tur_filename("2_f"));


		List<BinnedTally> pop = new ArrayList<BinnedTally>();

		add(pop, S2.evStats.popularity, "(A) Mostly Narrow");
		add(pop, S0.evStats.popularity, "(B) Uniformly Distributed");
		add(pop, S1.evStats.popularity, "(C) Mostly Wide");

		BinnedTallyDisplay mc = new BinnedTallyDisplay("filter width cdf", pop) {
			protected Double binValue(int tsi, BinnedTally i, Tally j) {

				double n = 0;
				for (Tally k : i.bins)
					if (k != j)
						n += k.numberObs();
					else
						break;

				return 100 * n / i.totalObs();

			}
		};
		mc.chart().setXRange(false, 0.1, 100);
		mc.chart().setYRange(false, 0.0, 100);

		commonConfig(mc, "Filter Width CDFs", "", "nodes accum (%)", "filter width (%)");
		mc.chart().setAlpha(0.125);

		mc.saveChart("/tmp/charts/sift_csf.pdf") ;
	}

	private void turmoil_cpu_load() throws Exception {
		
		Statistics S0 = Persistent.loadFromXml(tur_filename("1_t"));
		Statistics S1 = Persistent.loadFromXml(tur_filename("2_t"));
		Statistics S2 = Persistent.loadFromXml(tur_filename("2_f"));


		List<BinnedTally> pop = new ArrayList<BinnedTally>();
		add(pop, S2.evStats.filter_cpuLoad, "(A) Mostly Narrow");
		add(pop, S0.evStats.filter_cpuLoad, "(B) Uniformly Distributed");
		add(pop, S1.evStats.filter_cpuLoad, "(C) Mostly Wide");

		DeviationBinnedTallyDisplay mc = new DeviationBinnedTallyDisplay("cpu load vs popularity", pop);
		mc.chart().chart().getXYPlot().setRangeAxis(new LogarithmicAxis("log by"));
		mc.chart().setXRange(false, 0.1, 100);
		mc.chart().setYRange(false, 0.1, 50);

		
		commonConfig(mc, "Sifter Evaluations Load", "", "event forwards / received events", "filter width (%)");
		mc.chart().setAlpha(0.125);

		mc.saveChart("/tmp/charts/sift_cpu_load.pdf") ;

	}

	private void turmoil_net_load() throws Exception {

		Statistics S0 = Persistent.loadFromXml(tur_filename("1_t"));
		Statistics S1 = Persistent.loadFromXml(tur_filename("2_t"));
		Statistics S2 = Persistent.loadFromXml(tur_filename("2_f"));


		List<BinnedTally> pop = new ArrayList<BinnedTally>();
		add(pop, S2.evStats.filter_forwards, "(A) Mostly Narrow");
		add(pop, S0.evStats.filter_forwards, "(B) Uniformly Distributed");
		add(pop, S1.evStats.filter_forwards, "(C) Mostly Wide");

		DeviationBinnedTallyDisplay mc = new DeviationBinnedTallyDisplay("net load vs popularity", pop);
		
		mc.chart().setXRange(false, 0.1, 100);
		mc.chart().setYRange(false, 0.75, 1.25);
		commonConfig(mc, "Sifter Forwarding Load", "", "event forwards / received events", "filter width (%)");
		mc.chart().setAlpha(0.10);
		
		mc.saveChart("/tmp/charts/sift_net_load.pdf") ;
	}
	
	
	void commonConfig(AbstractBinnedTallyDisplay<?> chart, String title, String subtitle, String rangeLabel, String domainLabel) {
		chart.setUnitsScale(1.0 / 1.0, 1.0);
		Stroke s = new Pen(RGB.BLACK, 1).stroke;
		for (BinnedTally i : chart.items()) {
			chart.chart().setSeriesLinesAndShapes(i.name() + "@", false, true);
			chart.chart().setSeriesStroke(i.name() + "@", s);
		}
		for (BinnedTally i : chart.items()) {
			chart.chart().setSeriesLinesAndShapes(i.name(), true, false);
			chart.chart().setSeriesStroke(i.name(), s);
		}
		chart.chart().filterLegend("@");
		chart.chart().setAlpha(0.20);
		Font font = new java.awt.Font("Helvetica", Font.BOLD, 18);
		chart.chart().chart().setTitle(new TextTitle(title, font));

		if (subtitle.length() > 0)
			chart.chart().chart().addSubtitle(new TextTitle(subtitle, font.deriveFont(Font.PLAIN, 16f)));

		chart.chart().chart().setAntiAlias(true);
		chart.chart().chart().setTextAntiAlias(true);
		chart.chart().chart().getXYPlot().getRangeAxis().setLabel(rangeLabel);
		chart.chart().chart().getXYPlot().getDomainAxis().setLabel(domainLabel);
		chart.chart().chart().getXYPlot().getRangeAxis().setLabelFont(font.deriveFont(14f));
		chart.chart().chart().getXYPlot().getDomainAxis().setLabelFont(font.deriveFont(14f));

		chart.chart().chart().getLegend().setFrame(BlockBorder.NONE);
		chart.chart().chart().getLegend().setItemFont(font.deriveFont(Font.PLAIN, 14f));

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
}
