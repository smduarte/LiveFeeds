package livefeeds.rtrees.stats.broadcast;

import static simsim.core.Simulation.Gui;

import java.util.ArrayList;
import java.util.List;

import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.stats.Traffic;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;

import simsim.core.Displayable;
import simsim.gui.InputHandler0;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.charts.XYDeviationLineChart;
import simsim.gui.charts.XYStackedAreaChart;
import simsim.gui.geom.XY;
import simsim.ssj.BinnedTally;
import simsim.utils.Persistent;

import umontreal.iro.lecuyer.stat.Tally;

public class BroadcastRecentAvgTraffic extends Persistent {

	final double SAMPLE_SIZE = 5 * 60 ;
	
	public BinnedTally download, upload;

	public BroadcastRecentAvgTraffic() {

		upload = new BinnedTally(SAMPLE_SIZE, "total upload") ;
		download = new BinnedTally(SAMPLE_SIZE, "total download") ;
	}
	
	
	public void recordNodeTraffic( CatadupaNode node ) {
		Traffic t = node.state.stats.traffic ;
		
		double upTime = node.upTime() ;

		upload.tally(upTime, t.recent_upload_rate());
		download.tally(upTime, t.recent_download_rate()) ;

	}
	
	public BroadcastRecentAvgTraffic init() {

//		new CatadupaRecentAvgTrafficDisplay("Catadupa-recUpload", catDaparture_u, casting_u, dbRepair_u, dbEndpoints_u, dbFilters_u, upload);
//		new CatadupaRecentAvgTrafficDisplay("Catadupa-recDownload", catDaparture_d, casting_d, dbRepair_d, dbEndpoints_d, dbFilters_d, download);
		
		return this ;
	}
}

class BroadcastRecentAvgTrafficDisplay {
	
	private String frame ;
	private XYDeviationLineChart chart ;
	private List<BinnedTally> items = new ArrayList<BinnedTally>() ;
	
	public BroadcastRecentAvgTrafficDisplay( String frame, BinnedTally ... args ) {
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
		YIntervalSeries a = (YIntervalSeries)chart.getSeries(ts.name ) ;
//		YIntervalSeries b = (YIntervalSeries)chart.getSeries(ts.name()+"_minmax") ;
		
		a.clear() ;
//		b.clear() ;
//		chart.copySeriesColors(ts.name(), ts.name() + "_minmax");

		double x = ts.binSize/2 ;
		for( Tally i : ts.bins ) {
			if( i.numberObs() > 2) {
				double y = i.average() ;
				double l = Math.max( i.min(), i.average() - i.standardDeviation()), h = Math.min( i.max(), i.average() + i.standardDeviation() );
				a.add(x * xAxisUnitMultiplier, y, l, h) ;
				//b.add(x * xAxisUnitMultiplier, y, i.min(), i.max()) ;
			}
			x += ts.binSize ;
		}
	}
	
	private void prepare() {
		for( BinnedTally i : items )
			prepareChartSeries( i, 1.0 / 3600.0 ) ; // in hours		
	}
	
	void init() {
		chart = new XYDeviationLineChart("Catadupa Traffic by Session Duration", 0.0, "bytes/s", "time(h)") ;
		chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("bytes/sec")) ;
		chart.setYRange(false, 1, 10000) ;
		chart.setAlpha(0.25);

		
		Gui.addDisplayable(frame, new Displayable() {
			public void displayOn( Canvas canvas) {	
				prepare() ;
				chart.displayOn( canvas) ;
			}
		}, 0.5) ;
		Gui.addInputHandler(frame, new InputHandler0() {
			boolean toggle = false ;
			public void onMouseClick(int button, XY pu, XY ps) {
				toggle = !toggle ;
				if( toggle ) {
					chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("log bytes/sec ")) ;
					chart.setYRange(false, 1, 10000) ;
				}
				else
					chart.chart().getXYPlot().setRangeAxis( new NumberAxis("bytes/sec")) ;
			}
		}) ;
		Gui.setFrameRectangle(frame, 0, 0, 480, 480) ;
		Gui.setFrameTransform(frame, 500, 500, 0, false) ;		
	}
}