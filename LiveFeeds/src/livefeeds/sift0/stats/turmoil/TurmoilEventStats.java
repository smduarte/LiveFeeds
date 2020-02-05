package livefeeds.sift0.stats.turmoil;

import java.util.Iterator;
import java.util.LinkedList;

import livefeeds.sift0.CatadupaNode;
import livefeeds.sift0.Event;
import livefeeds.sift0.GlobalDB;
import livefeeds.sift0.TurmoilNode;

import org.jfree.chart.axis.NumberAxis;

import simsim.core.PeriodicTask;
import simsim.ssj.BinnedTally;
import simsim.ssj.charts.BinnedTallyDisplay;
import simsim.ssj.charts.DeviationBinnedTallyDisplay;

import umontreal.iro.lecuyer.stat.Tally;

public class TurmoilEventStats {

	public double inView_events = 0, offView_events = 0 ;
	
	transient public LinkedList<Event> events = new LinkedList<Event>() ;
	
	public BinnedTally pop_cpuLoad = new BinnedTally( 1.0, "CPU Load vs Event Popularity") ;		
	
	public BinnedTally popularity = new BinnedTally( 1.0, "Event Popularity") ;		
	
	public BinnedTally width = new BinnedTally( 1.0, "Filter Width") ;		
	
	public BinnedTally width_live = new BinnedTally( 1.0, "Live Filter Width") ;		
	
	public BinnedTally filter_cpuLoad = new BinnedTally( 1, "CPU Load vs Filter Width") ;		
	public BinnedTally filter_forwards = new BinnedTally( 1, "Routing vs Filter Width ") ;		

	public BinnedTally filter_cpuLoad_live = new BinnedTally( 1.0, "Live CPU Load vs Filter Width") ;		
	public BinnedTally filter_forwards_live = new BinnedTally( 1.0, "Live Routing vs Filter Width ") ;		
	
	public void accountDeadNode( CatadupaNode node ) {
		TurmoilFilterStats x = node.state.stats.filter ;
		if( x.accepted_events > 0 ) {
			filter_cpuLoad.tally( 100*x.filter_width(), x.cpuLoad_Accepted() ) ;		
		}
		if( x.accepted_events > 0 ) {
			filter_forwards.tally( 100*x.filter_width(), x.netLoad_Accepted() ) ;		
		}
		if( x.total_events() > 0 )
			width.tally(100 * x.filter_width(), 1) ;
	}
	
	public TurmoilEventStats init() {
	
//		if( true )
//			return this ;
		
		new PeriodicTask(30) {
			public void run() {
				for( Iterator<Event> i = events.iterator() ; i.hasNext() ; ) {
					Event j = i.next() ;
					if( j.elapsed() > 60 ) {
						j.account() ;
						i.remove() ;
					}
				}
			}
		};
		
		
		new PeriodicTask(300) {			
			public void run() {
				filter_cpuLoad_live.init() ;
				filter_forwards_live.init() ;
				width_live.init() ;
				
				for( CatadupaNode i : GlobalDB.liveNodes() ) 
					if( i.state.joined ) {
						TurmoilNode j = (TurmoilNode) i ;
						TurmoilFilterStats x = j.state.stats.filter ;
						if( x.accepted_events > 0 ) {
							filter_cpuLoad_live.tally( 100*x.filter_width(), x.cpuLoad_Accepted() ) ;		
						}
						if( x.accepted_events > 0 ) {
							filter_forwards_live.tally( 100*x.filter_width(), x.netLoad_Accepted() ) ;		
						}
						if( x.total_events() > 0 )
							width_live.tally(100 * x.filter_width(), 1) ;

					}
			}
		};
		
		new BinnedTallyDisplay("Event Popularity", popularity ) {

			protected Double binValue( int tsi, BinnedTally i, Tally j ) {	
				return tsi == 0 ? actual( tsi, i, j ) : cummulative(tsi, i, j) ;
			}
			
			protected Double actual( int tsi, BinnedTally i, Tally j ) {				
				return 100 * j.numberObs() / i.totalObs() ;
			}

			protected Double cummulative( int tsi, BinnedTally i, Tally j ) {				
				double n = 0 ;
				for( Tally k : i.bins )
					if( k != j )
						n += k.numberObs() ;
					else
						break ;
				
				return 100 * n / i.totalObs() ;
			}

			protected void init() {
				super.init() ;
				
				chart.chart().getXYPlot().setRangeAxis( new NumberAxis("accum %")) ;
				chart.setYRange(false, 0, 100) ;
				chart.setXRange(false, 1, 100) ;
				chart.chart().setTitle("Cummulative Event Popularity Distribution") ;

				for( BinnedTally i : items )
					chart().setSeriesLinesAndShapes( i.name, true, false ) ;
			}
		};
		
		new BinnedTallyDisplay("Filter Width", width, width_live ) {		
			protected Double binValue( int tsi, BinnedTally i, Tally j ) {
				
				double n = 0 ;
				for( Tally k : i.bins )
					if( k != j )
						n += k.numberObs() ;
					else
						break ;
				
				return 100 * n / i.totalObs() ;

			}
				
			protected void init() {
				super.init() ;
				
				chart.chart().getXYPlot().setRangeAxis( new NumberAxis("accum %")) ;
				chart.setYRange(false, 0, 100) ;
				chart.setXRange(false, 1, 100) ;
				chart.chart().setTitle("Cummulative Filter Width Distribution") ;

				for( BinnedTally i : items )
					chart().setSeriesLinesAndShapes( i.name, true, false ) ;
			}
		};
		
		new DeviationBinnedTallyDisplay("Cpu Load vs. Popularity", pop_cpuLoad ) {

			protected void init() {
				super.init() ;
				chart.chart().setTitle("Average Filter Tests") ;
				
//				chart.chart().getXYPlot().setRangeAxis( new NumberAxis("")) ;
				chart.setYRange(true, 0.01, 1000) ;
				chart.setXRange(false, 1, 100) ;

				for( BinnedTally i : items )
					chart().setSeriesLinesAndShapes( i.name, true, false ) ;
			}

		};
		
//		new DeviationBinnedTallyDisplay("Cpu Load vs. Filter Width", filter_cpuLoad ) {
//
//			protected void init() {
//				super.init() ;
//				chart.chart().setTitle("CPU Load vs Filter Width") ;
//				
////				chart.chart().getXYPlot().setRangeAxis( new NumberAxis("")) ;
//				chart.setYRange(true, 0.01, 1000) ;
//				chart.setXRange(false, 1, 100) ;
//
//				for( BinnedTally i : items )
//					chart().setSeriesLinesAndShapes( i.name, true, false ) ;
//			}
//
//		};
//		
		new DeviationBinnedTallyDisplay("Cpu Load vs. Filter Width", filter_cpuLoad, filter_forwards /*, filter_cpuLoad_live, filter_forwards_live*/ ) {

			protected void init() {
				super.init() ;
				chart.chart().setTitle("CPU Load vs Filter Width") ;
				
//				chart.chart().getXYPlot().setRangeAxis( new NumberAxis("")) ;
				chart.setYRange(true, 0.01, 1000) ;
				chart.setXRange(false, 1, 100) ;

				for( BinnedTally i : items )
					chart().setSeriesLinesAndShapes( i.name, true, false ) ;
				
			}
		};
		
		return this;
	}
}
