package livefeeds.rtrees.charts.catadupa;

import java.awt.GradientPaint;
import java.util.Arrays;
import java.util.Collection;

import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.YIntervalSeries;

import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.ssj.BinnedTally;

import umontreal.iro.lecuyer.stat.Tally;

public class CatadupaMultiConfigTrafficStackedChart extends CatadupaMultiConfigTrafficDeviationChart {

	public CatadupaMultiConfigTrafficStackedChart(String frame, BinnedTally... args) {
		super( frame, Arrays.asList( args ) ) ;
	}

	public CatadupaMultiConfigTrafficStackedChart(TextTitle title, String frame, BinnedTally... args) {
		super( title, frame, Arrays.asList( args ) ) ;
	}

	
	public CatadupaMultiConfigTrafficStackedChart(String frame, Collection<BinnedTally> args) {
		super(null, frame, args ) ;
	}
		
	
	public CatadupaMultiConfigTrafficStackedChart( TextTitle tt, String frame, Collection<BinnedTally> args) {
		super( tt, frame, args ) ;
	}
	
	public void prepareChartSeries( BinnedTally s, double xAxisUnitMultiplier) {
		YIntervalSeries a = (YIntervalSeries)chart.getSeries(s.name ) ;
		a.clear() ;

		double x = s.binSize/2 ;
		for( int i = 0 ; i < s.bins.size() ; i++ )  {

			double l = 0, v = s.bin(i).average() ;
			for(BinnedTally j : items )				
				if( j == s ) 
					break ;
				else {
					Tally tj = j.bin(i) ;
					if( tj != null && tj.numberObs() > 2 )
						l += tj.average() ;
				}
			
			a.add(x * xAxisUnitMultiplier, l+v, l, l+v ) ;
			x += s.binSize ;
		}
	}
	
	protected void prepare() {
		for (BinnedTally i : items)
			prepareChartSeries(i, 1.0 / 3600.0); // in hours
				
	}
	
	@Override
	public void init() {
		super.init();
		Pen g4 = new Pen(new RGB(0.85,0.85,0.85, 0.75 ), 0);
		Pen g3 = new Pen(new RGB(0.65,0.65,0.65, 0.75), 0);
		Pen g2 = new Pen(new RGB(0.45,0.45,0.45, 0.75 ), 0);
		Pen g1 = new Pen(new RGB(0.25,0.25,0.25, 0.75 ), 0);
		Pen g0 = new Pen(new RGB(0.1,0.1,0.1, 0.75 ), 0 );

		Pen p0 = new Pen(new RGB(0,0,1,0.25)) ;
		Pen p1 = new Pen(new RGB(1,0,0,0.25)) ;
		Pen p2 = new Pen(new RGB(0,1,0,0.25));
		Pen p3 = new Pen(new RGB(1,0.7,0,0.33)) ;
		Pen p4 = new Pen(new RGB(0,0.7,1,0.33)) ;

		GradientPaint gp0 = new GradientPaint( 1, 1, g0.color, 3, 1, p0.color, true);
		GradientPaint gp1 = new GradientPaint( 5, 5, g1.color, 10, 5, p1.color, true);
		GradientPaint gp2 = new GradientPaint( 5, 5, g2.color, 10, 5, p2.color, true);
		GradientPaint gp3 = new GradientPaint( 5, 5, g3.color, 10, 5, p3.color, true);
		GradientPaint gp4 = new GradientPaint( 5, 5, g4.color, 10, 5, p4.color, true);
		
		
		Pen[] g_pens = new Pen[] {g0, g1, g2, g3, g4} ;
		Pen[] c_pens = new Pen[] {p0, p1, p2, p3, p4} ;
		GradientPaint[] p_pens = new GradientPaint[] {gp0, gp1, gp2, gp3, gp4} ;
		for( int i = 0 ; i < Math.min( c_pens.length , chart.numSeries()) ; i++ ) {
			chart.setSeriesPen(  items.get(i).name, c_pens[i] ) ;
			chart.setSeriesFillPaint(  items.get(i).name, c_pens[i] ) ;
		}
		
		replaceLegend();
		
		chart.setXRange(false, 0, 8) ;
		chart.setAlpha(1) ;

		saveChart("/tmp/" + frame + ".pdf") ;
	}
	
	
	
}
