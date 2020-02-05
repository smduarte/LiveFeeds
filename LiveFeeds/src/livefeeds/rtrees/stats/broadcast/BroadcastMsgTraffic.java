package livefeeds.rtrees.stats.broadcast;

import static simsim.core.Simulation.Gui;

import java.util.ArrayList;
import java.util.List;

import livefeeds.rtrees.CatadupaNode;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.charts.XYDeviationLineChart;
import simsim.ssj.BinnedTally;
import simsim.utils.Persistent;

import umontreal.iro.lecuyer.stat.Tally;

public class BroadcastMsgTraffic extends Persistent {

	final double SAMPLE_SIZE = 5 * 60 ;

	public BinnedTally download, upload;

	public BroadcastMsgTraffic() {		
		upload = new BinnedTally(SAMPLE_SIZE, "total upload") ;
		download = new BinnedTally( SAMPLE_SIZE, "total download") ;
	}
	
	
	public void recordNodeTraffic( CatadupaNode node ) {
		BroadcastTraffic t = node.state.stats.btraffic ;
		
		double upTime = node.upTime() ;
		
		upload.tally(upTime, t.upload() / upTime );
		download.tally(upTime, t.download() / upTime ) ;
		
	}
	
	public BroadcastMsgTraffic init() {

		new BroadcastMsgTrafficDisplay("Broadcast-Upload", upload );
		new BroadcastMsgTrafficDisplay("Broadcast-Download", download);

		return this ;
	}
}

class BroadcastMsgTrafficDisplay {
	
	private String frame ;
	private XYDeviationLineChart chart ;
	private List<BinnedTally> items = new ArrayList<BinnedTally>() ;
	
	public BroadcastMsgTrafficDisplay( String frame, BinnedTally ... args ) {
		this.frame = frame ;
		for( Object i : args ) 
			items.add( (BinnedTally) i) ;
		
		init() ;
	}
	
	public void saveChart(String pdfName ) {
		try {
			prepare() ;
			chart.saveChartToPDF(pdfName, 500, 500) ;
		} catch( Exception x ) {
			x.printStackTrace() ;
		}		
	}
	
	
	private void prepareChartSeries( BinnedTally ts, double xAxisUnitMultiplier) {
		YIntervalSeries a = (YIntervalSeries)chart.getSeries(ts.name) ;
//		YIntervalSeries b = (YIntervalSeries)chart.getSeries(ts.name+"_minmax") ;
		
		a.clear() ;
//		b.clear() ;
//		chart.copySeriesColors( ts.name, ts.name + "_minmax");

		double avg = 0, tot = 0 ;
		for( Tally i : ts.bins ) {
			avg += i.sum() ;
			tot += i.numberObs() ;
		}
		avg /= tot ;
		
		double x = ts.binSize/2 ;
		for( Tally i : ts.bins ) {
			if( i.numberObs() > 2) {
				double y = 100 * i.standardDeviation() / avg ;
				double l = Math.max( i.min(), i.average() - i.standardDeviation()), h = Math.min( i.max(), i.average() + i.standardDeviation() );
				a.add(x * xAxisUnitMultiplier, y, y, y) ;
			}
			x += ts.binSize ;
		}
	}
	
	private void prepare() {
		for( BinnedTally i : items )
			prepareChartSeries( i, 1.0 / 3600.0 ) ; // in hours		
	}
	
	void init() {
		chart = new XYDeviationLineChart("Traffic Standard Deviation by Session Duration", 0.0, "% (standard deviation/average)", "time(h)") ;
		chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("% (standard deviation/average)")) ;
		chart.setYRange(false, 0.01, 100) ;
		chart.setAlpha(0.25);

		
		Gui.addDisplayable(frame, new Displayable() {
			public void displayOn( Canvas canvas) {	
				prepare() ;
				chart.displayOn( canvas) ;
			}
		}, 0.5) ;
		
		Gui.setFrameRectangle(frame, 0, 0, 480, 480) ;
		Gui.setFrameTransform(frame, 500, 500, 0, false) ;		
	}
}

