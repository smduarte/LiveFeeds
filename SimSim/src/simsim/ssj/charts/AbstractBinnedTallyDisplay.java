package simsim.ssj.charts;

import static simsim.core.Simulation.Gui;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Series;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.charts.AbstractXYChart;
import simsim.gui.charts.Charts;
import simsim.ssj.BinnedTally;

abstract public class AbstractBinnedTallyDisplay<T extends Series> {

	protected String frame;
	protected AbstractXYChart<T> chart;
	protected double xScale = 1.0, yScale = 1.0;
	protected List<BinnedTally> items = new ArrayList<BinnedTally>();

	public AbstractBinnedTallyDisplay(String frame, BinnedTally... args) {
		this.frame = frame;
		for (BinnedTally i : args)
			items.add(i);

		init();
	}

	public List<BinnedTally> items() {
		return items;
	}

	public void setUnitsScale(double xs, double ys) {
		xScale = xs;
		yScale = ys;
	}

	public AbstractXYChart<T> chart() {
		return chart;
	}

	public JFreeChart jfChart() {
		return chart.chart();
	}

	public void saveChart(String pdfName) {
		try {
			prepare();
			Charts.saveChartToPDF(chart.chart(), pdfName, 500, 500);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	void prepare() {
		int j = 0;
		for (BinnedTally i : items)
			computeChartSeries(j++, i);
	}

	protected void init() {
		createChart();

		if (frame != null) {
			Gui.addDisplayable(frame, new Displayable() {
				public void displayOn(Canvas canvas) {
					prepare();
					chart.displayOn(canvas);
				}
			}, 5.0);
			Gui.setFrameRectangle(frame, 0, 0, 480, 480);
			Gui.setFrameTransform(frame, 500, 500, 0, false);
		}

		Pen p0 = new Pen(RGB.RED.darker(), 2);
		Pen p1 = new Pen(RGB.GREEN.darker(), 2);
		Pen p2 = new Pen(RGB.BLUE.darker(), 2);
		Pen p3 = new Pen(RGB.CYAN.darker(), 2);
		Pen p4 = new Pen(RGB.ORANGE.darker(), 2);
		Pen p5 = new Pen(RGB.PINK.darker(), 2);
		Pen p6 = new Pen(RGB.MAGENTA.darker(), 2);

		Pen[] pens = new Pen[] { p0, p1, p2, p3, p4, p5, p6 };

		for (int i = 0; i < Math.min(pens.length, items.size()); i++) {
			chart.setSeriesPen(items.get(i).name, pens[i]);
			chart.setSeriesFillPaint(items.get(i).name, pens[i]);
		}
		chart.setAlpha(0.35);
	}

	abstract protected void createChart();

	abstract protected void computeChartSeries(int tsi, BinnedTally ts);
}
