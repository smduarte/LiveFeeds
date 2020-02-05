package livefeeds.rtrees.gui;

import static livefeeds.rtrees.config.Config.Config;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;

public class TrafficPatternHistogramDisplay implements Displayable {
	
	static final int W = 100;
	public static TrafficPatternHistogramDisplay TrafficPatternHistogram ;
	
	int[] rSamples = new int[W + 1];
	int[] aSamples = new int[W + 1];

	public TrafficPatternHistogramDisplay() {
		TrafficPatternHistogram = this ;
	}
	
	void add(double src, double dst) {
		src /= ((1L << Config.NODE_KEY_LENGTH) - 1);
		dst /= ((1L << Config.NODE_KEY_LENGTH) - 1);

		double d = (dst - src + 1.0) / 2.0;
		int r = (int) (W * d);
		if (r >= 0 && r < rSamples.length)
			rSamples[r]++;

		int a = (int) (W * dst);
		if (a >= 0 && a < aSamples.length)
			aSamples[a]++;
	}

	public void displayOn(Canvas canvas) {
		double rT = 0, rM = Integer.MIN_VALUE;
		for (int i : rSamples) {
			rT += i;
			rM = Math.max(rM, Math.log1p(i));
		}

		double aT = 0, aM = Integer.MIN_VALUE;
		for (int i : aSamples) {
			aT += i;
			aM = Math.max(rM, Math.log1p(i));
		}

		double M = Math.max(rM, aM);

		new Pen(RGB.BLUE, 3).useOn(canvas.gs);

		double sX = 1000.0 / rSamples.length, sY = 1000.0 / M;

		for (int i = 0; i < rSamples.length; i++) {
			double x = i * sX;
			double y = 1000 - Math.log1p(rSamples[i]) * sY;
			canvas.sDraw(new Circle(x, y, 2));
		}

		new Pen(RGB.RED, 3).useOn(canvas.gs);
		for (int i = 0; i < rSamples.length; i++) {
			double x = i * sX;
			double y = 1000 - Math.log1p(aSamples[i]) * sY;
			canvas.sDraw(new Circle(x, y, 2));
		}

	}
}
