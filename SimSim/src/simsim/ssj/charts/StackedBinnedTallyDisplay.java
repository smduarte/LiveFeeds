package simsim.ssj.charts;

import java.util.Collection;

import org.jfree.data.xy.YIntervalSeries;

import simsim.ssj.BinnedTally;
import umontreal.iro.lecuyer.stat.Tally;

public class StackedBinnedTallyDisplay extends DeviationBinnedTallyDisplay {

	public StackedBinnedTallyDisplay(String frame, BinnedTally... args) {
		super(frame, args);
	}
	
	public StackedBinnedTallyDisplay(Collection<BinnedTally> args) {
		super(null, args.toArray(new BinnedTally[args.size()]));
	}

	public StackedBinnedTallyDisplay(String frame, Collection<BinnedTally> args) {
		super(frame, args.toArray(new BinnedTally[args.size()]));
	}

	protected void computeChartSeries(int tsi, BinnedTally ts) {
		YIntervalSeries a = (YIntervalSeries) chart.getSeries(ts.name);

		a.clear();

		double x = ts.binSize;
		L1: for (int i = 0; i < ts.bins.size(); i++, x += ts.binSize) {

			double l = 0;
			for (BinnedTally j : items)
				if (j == ts)
					break;
				else {
					Tally tj = j.bin(i);
					if (tj != null && tj.numberObs() > 2)
						l += tj.average();
					else
						continue L1;
				}

			Tally si = ts.bin(i);
			if (si != null && si.numberObs() > 2) {
				double v = si.average();
				a.add(x * xScale, l + v, l, l + v);
			}
		}
	}
	
	protected void init() {
		super.init() ;
		
		for( BinnedTally i : items )
			chart.setSeriesLinesAndShapes(i.name, false, false) ;
	}
}
