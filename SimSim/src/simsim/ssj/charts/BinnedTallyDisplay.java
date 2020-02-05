package simsim.ssj.charts;

import java.util.Collection;

import org.jfree.data.xy.XYSeries;

import simsim.gui.charts.XYLineChart;
import simsim.ssj.BinnedTally;
import umontreal.iro.lecuyer.stat.Tally;

public class BinnedTallyDisplay extends AbstractBinnedTallyDisplay<XYSeries> {
	
	protected boolean cumulative = false ;
	
	public BinnedTallyDisplay( String frame, BinnedTally ... args ) {
		super( frame, args ) ;
	}

	public BinnedTallyDisplay( Collection<BinnedTally> args ) {
		super( null, args.toArray( new BinnedTally[ args.size() ]) ) ;
	}

	public BinnedTallyDisplay( String frame, Collection<BinnedTally> args ) {
		super( frame, args.toArray( new BinnedTally[ args.size() ]) ) ;
	}
	
	
	protected void createChart() {
		chart = new XYLineChart("no title", 0.0, "y", "x") ;		
	}
	
	public BinnedTallyDisplay cumulative() {
		cumulative = true ;
		return this ;
	}
	
	protected Double binValue( int i, BinnedTally ti, Tally e ) {
		return e.numberObs() > 2 ? e.average() : null ;
	}
	
	protected Double cumulativeValue(int tsi, BinnedTally i, Tally j) {

		double n = 0;
		for (Tally k : i.bins)
			if (k != j)
				n += k.numberObs();
			else
				break;

		return 100 * n / i.totalObs();

	}
	protected void computeChartSeries( int tsi, BinnedTally ts ) {
		XYSeries a = chart.getSeries(ts.name ) ;

		a.clear() ;

		double x = ts.binSize/2 ;
		for( Tally i : ts.bins ) {
			Double v = cumulative? cumulativeValue( tsi, ts, i ) : binValue( tsi, ts, i ) ;
			if( v != null )
				a.addOrUpdate(x * xScale,  v.doubleValue() * yScale ) ;
			x += ts.binSize ;
		}
	}
}
