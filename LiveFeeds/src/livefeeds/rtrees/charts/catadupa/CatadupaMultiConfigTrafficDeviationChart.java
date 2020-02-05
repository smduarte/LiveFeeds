package livefeeds.rtrees.charts.catadupa;

import static simsim.core.Simulation.Gui;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.YIntervalSeries;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.charts.XYDeviationLineChart;
import simsim.gui.geom.Line;
import simsim.gui.geom.XY;
import simsim.ssj.BinnedTally;

import umontreal.iro.lecuyer.stat.Tally;

public class CatadupaMultiConfigTrafficDeviationChart {

	protected String frame;
	protected TextTitle title;
	protected XYDeviationLineChart chart ;
	protected List<BinnedTally> items = new ArrayList<BinnedTally>();

	public CatadupaMultiConfigTrafficDeviationChart(String frame, BinnedTally... args) {
		this( frame, Arrays.asList( args ) ) ;
	}

	public CatadupaMultiConfigTrafficDeviationChart(TextTitle title, String frame, BinnedTally... args) {
		this( title, frame, Arrays.asList( args ) ) ;
	}

	
	public CatadupaMultiConfigTrafficDeviationChart(String frame, Collection<BinnedTally> args) {
		this(null, frame, args ) ;
	}
	
	public CatadupaMultiConfigTrafficDeviationChart( TextTitle tt, String frame, Collection<BinnedTally> args) {
		this.frame = frame;
		for (BinnedTally i : args)
			items.add(i);

		if( tt != null )
			title = tt ;
		else 
			title = new TextTitle() ;
		
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

	public void prepareChartSeries( BinnedTally ts, double xAxisUnitMultiplier) {
		YIntervalSeries a = (YIntervalSeries)chart.getSeries(ts.name ) ;
		//YIntervalSeries b = (YIntervalSeries)chart.getSeries(ts.name( Main.translator +"_minmax") ;
		
		a.clear() ;
		//b.clear() ;
		//chart.copySeriesColors(ts.name( Main.translator), ts.name(Main.translator) + "_minmax");

		double si = chart.getSeriesIndex(ts.name) ;
		chart.setSeriesStroke(ts.name, new Pen( RGB.black, 3.0, 2*si ).stroke) ;

		System.out.println( ts.bins ) ;
		
		double x = ts.binSize / 2 ;
		for( Tally i : ts.bins ) {
			if( i.numberObs() > 2) {
				double y = 100 * i.standardDeviation() / i.average() ;
				y = i.average() ;
				System.out.println(i.average() );
				double l = Math.max( i.min(), i.average() - i.standardDeviation()), h = Math.min( i.max(), i.average() + i.standardDeviation() );
					a.add(x * xAxisUnitMultiplier, y, y, y) ;
					//b.add(x * xAxisUnitMultiplier, y, i.min(), i.max()) ;
			} else {
				System.err.println("askdakdkdad");
			}
			x += ts.binSize ;
		}
	}

	protected void prepare() {
		for (BinnedTally i : items)
			prepareChartSeries(i, 1.0 / 3600.0); // in hours
	}

	void init() {
		chart = new XYDeviationLineChart("no title", 0.0, "% stdev(upload rate) / mean upload rate", "time(h)");
		chart.chart().setTitle( title ) ;
//		chart.chart().getXYPlot().setRangeAxis( new LogarithmicAxis("% stdev(upload rate)/mean upload rate")) ;
//		chart.setYRange(false, 0, 30);
		chart.setXRange(false, 0, 4);
		chart.setAlpha(0.5) ;
		
		chart.chart().getLegend().setFrame(BlockBorder.NONE);
		
		int fs = title.getFont().getSize() ;
		chart.chart().getXYPlot().getRangeAxis().setLabelFont(  title.getFont().deriveFont(fs * 0.85f ) ) ;

		chart.chart().getXYPlot().getRangeAxis().setLabelFont(  title.getFont().deriveFont(fs * 0.95f ) ) ;

		Gui.addDisplayable(frame, new Displayable() {
			public void displayOn(Canvas canvas) {
				prepare();
				chart.displayOn(canvas);
			}
		}, 0.5);

		prepare() ;
		replaceLegend() ;
		
		Gui.setFrameRectangle(frame, 0, 0, 480, 480);
		Gui.setFrameTransform(frame, 500, 500, 0, false);
		
		saveChart("/tmp/" + frame + ".pdf") ;
	}
	
	
	void replaceLegend() {
		
		final LegendItemCollection nlic = new LegendItemCollection() ;
		LegendItemCollection lic = chart.chart().getXYPlot().getLegendItems()  ;
		
		Shape shape = null ;
		for( Iterator<?> i = lic.iterator() ; i.hasNext() ; ) {
			LegendItem j = (LegendItem) i.next()  ;			
			if( shape == null )
				shape = j.getShape() ;		
			
			shape = new Line( new XY(0,0), new XY(30,0)) ;
			
			String label = Main.translator.get( j.getLabel() ) ;
			nlic.add( new LegendItem( label == null ? j.getLabel() : label, j.getDescription(), "", "", shape, j.getLineStroke(), j.getLinePaint() ) ) ;
		}
		
		LegendItemSource source = new LegendItemSource() {			
			@Override
			public LegendItemCollection getLegendItems() {
				return nlic;
			}
		};		

		chart.chart().getLegend().setItemFont( title.getFont().deriveFont( 11 ) ) ;
		chart.chart().getLegend().setSources(  new LegendItemSource[] { source }  ) ;
	}
}
