package simsim.ssj.charts;

import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.charts.XYStackedAreaChart;
import simsim.ssj.BinnedTally;

public class StackedBinnedTallyDisplayBuggy extends BinnedTallyDisplay {
		
	public StackedBinnedTallyDisplayBuggy( String frame, BinnedTally ... args ) {
		super( frame, args ) ;
	}
	
	protected void createChart() {
		chart = new XYStackedAreaChart(" no title", 0.0, " y ", " x ") ;
	}
	
	
	@Override
	protected void init() {
		super.init() ;
		
		Pen p0 = new Pen(new RGB(0,1,0,0.25));
		Pen p1 = new Pen(new RGB(0,0,1,0.25)) ;
		Pen p2 = new Pen(new RGB(1,0,0,0.25)) ;
		Pen p3 = new Pen(new RGB(1,0.7,0,0.33)) ;
		Pen p4 = new Pen(new RGB(0,0.7,1,0.33)) ;

		Pen[] pens = new Pen[] {p0, p1, p2, p3, p4} ;
		
		for( int i = 0 ; i < Math.min( pens.length, items.size()) ; i++ )
			chart.setSeriesPen(  items.get(i).name, pens[i] ) ;
	}
}
