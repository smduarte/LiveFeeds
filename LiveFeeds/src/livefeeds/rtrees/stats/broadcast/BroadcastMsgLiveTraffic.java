package livefeeds.rtrees.stats.broadcast;

import static simsim.core.Simulation.Gui;

import java.util.ArrayList;
import java.util.List;

import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.GlobalDB;
import static livefeeds.rtrees.config.Config.Config;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;

import simsim.core.Displayable;
import simsim.core.PeriodicTask;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.charts.XYDeviationLineChart;
import simsim.ssj.BinnedTally;
import simsim.utils.Persistent;

import umontreal.iro.lecuyer.stat.Tally;

public class BroadcastMsgLiveTraffic extends Persistent {

	final double SAMPLE_SIZE = 5 * 60 ;
	
	public BinnedTally download, upload;

	public BroadcastMsgLiveTraffic() {
		upload = new BinnedTally( SAMPLE_SIZE, "total upload");
		download = new BinnedTally(SAMPLE_SIZE, "total download");
	}

	public void recordNodeTraffic(CatadupaNode node) {
		BroadcastTraffic t = node.state.stats.btraffic;

		double elapsed = Simulation.currentTime() - Config.BROADCAST_START;

		if (elapsed > 0) {
			upload.tally(elapsed, t.upload() / elapsed );
			download.tally(elapsed, t.download() / elapsed);
		}
	}

	public BroadcastMsgLiveTraffic init() {

		new BroadcastMsgTrafficDisplay("Broadcast-LiveUpload", upload);
		new BroadcastMsgTrafficDisplay("Broadcast-LiveDownload", download);

		new PeriodicTask(null, Config.BROADCAST_START+30, 60) {
			public void run() {
				updateLiveStats() ;
			}
		};
		return this;
	}

	void updateLiveStats() {
		for (CatadupaNode i : GlobalDB.liveNodes())
			recordNodeTraffic(i);
	}

	class BroadcastMsgTrafficDisplay {

		private String frame;
		private XYDeviationLineChart chart;
		private List<BinnedTally> items = new ArrayList<BinnedTally>();

		public BroadcastMsgTrafficDisplay(String frame, BinnedTally... args) {
			this.frame = frame;
			for (Object i : args)
				items.add((BinnedTally) i);

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

		private void prepareChartSeries(BinnedTally ts, double xAxisUnitMultiplier) {
			YIntervalSeries a = (YIntervalSeries) chart.getSeries(ts.name);
			YIntervalSeries b = (YIntervalSeries)chart.getSeries(ts.name+"_minmax") ;

			a.clear();
			b.clear() ;
			chart.copySeriesColors( ts.name, ts.name + "_minmax");

			double x = ts.binSize / 2;
			for (Tally i : ts.bins) {
				if (i.numberObs() > 2) {
					double y = 100 * i.standardDeviation() / i.average();
//					y = i.average();
					double l = Math.max(i.min(), i.average() - i.standardDeviation()), h = Math.min(i.max(), i.average() + i.standardDeviation());
					a.add(x * xAxisUnitMultiplier, y, y, y);
//					b.add(x * xAxisUnitMultiplier, y, i.min(), i.max());
				}
				x += ts.binSize;
			}
		}

		private void prepare() {
			for (BinnedTally i : items)
				prepareChartSeries(i, 1.0 / 3600.0); // in hours
		}

		void init() {
			chart = new XYDeviationLineChart("Traffic Standard Deviation by Session Duration", 0.0, "% (standard deviation/average)", "time(h)");
			// chart.chart().getXYPlot().setRangeAxis(new
			// LogarithmicAxis("% (standard deviation/average)"));

			chart.chart().getXYPlot().setDomainAxis(new LogarithmicAxis("time(log h)"));

			chart.setYRange(true, 0.1, 100);
			chart.setAlpha(0.25);

			Gui.addDisplayable(frame, new Displayable() {
				public void displayOn(Canvas canvas) {
					prepare();
					chart.displayOn(canvas);
				}
			}, 0.5);

			Gui.setFrameRectangle(frame, 0, 0, 480, 480);
			Gui.setFrameTransform(frame, 500, 500, 0, false);
		}
	}
}
