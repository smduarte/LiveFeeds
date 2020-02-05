package livefeeds.rtrees.charts.catadupa;

import static simsim.core.Simulation.Gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.charts.XYChart;
import simsim.gui.charts.XYLineChart;
import simsim.ssj.BinnedTally;

import umontreal.iro.lecuyer.stat.Tally;

public class CatadupaMultiConfigTrafficChart {

	private String frame;
	private TextTitle title;
	private XYChart<?> chart;
	private List<BinnedTally> items = new ArrayList<BinnedTally>();

	public CatadupaMultiConfigTrafficChart(String frame, BinnedTally... args) {
		this( frame, Arrays.asList( args ) ) ;
	}

	public CatadupaMultiConfigTrafficChart(TextTitle title, String frame, BinnedTally... args) {
		this( title, frame, Arrays.asList( args ) ) ;
	}

	
	public CatadupaMultiConfigTrafficChart(String frame, Collection<BinnedTally> args) {
		this(null, frame, args ) ;
	}
	
	public CatadupaMultiConfigTrafficChart( TextTitle tt, String frame, Collection<BinnedTally> args) {
		this.frame = frame;
		for (BinnedTally i : args)
			items.add(i);

		if( tt != null )
			title = tt ;
		else 
			title = new TextTitle() ;
		
		init();

	}
	
	public void saveChart(String pdfName) {
		try {
			prepare();
			chart.saveChartToPDF(pdfName, 500, 500);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void prepareChartSeries(BinnedTally cts, double xAxisUnitMultiplier) {
		XYSeries a = (XYSeries) chart.getSeries(cts.name);
		chart.setSeriesLinesAndShapes(cts.name, true, false);
		a.clear();

		double x = cts.binSize / 2;
		for (Tally i : cts.bins) {
			if (i.numberObs() > 2) {
				double y = i.average();
				a.add(x * xAxisUnitMultiplier, y);
			}
			x += cts.binSize;
		}
	}

	private void prepare() {
		for (BinnedTally i : items)
			prepareChartSeries(i, 1.0 / 3600.0); // in hours
	}

	void init() {
		chart = new XYLineChart("no title", 0.0, "bytes/s", "time(h)");
		chart.chart().setTitle( title ) ;
		chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("log bytes/s")) ;
		chart.setYRange(false, 1, 10000);
		
		chart.chart().getLegend().setFrame(BlockBorder.NONE);

		Gui.addDisplayable(frame, new Displayable() {
			public void displayOn(Canvas canvas) {
				prepare();
				chart.displayOn(canvas);
			}
		}, 0.5);

		
		Gui.setFrameRectangle(frame, 0, 0, 480, 480);
		Gui.setFrameTransform(frame, 500, 500, 0, false);
		
		saveChart("/tmp/" + frame + ".pdf") ;
		
	}
}
