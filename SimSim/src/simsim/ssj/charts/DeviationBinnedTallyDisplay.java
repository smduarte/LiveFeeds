package simsim.ssj.charts;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Collection;
import java.util.Iterator;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.data.xy.YIntervalSeries;

import simsim.core.Globals;
import simsim.gui.charts.XYDeviationLineChart;
import simsim.gui.geom.Line;
import simsim.gui.geom.XY;
import simsim.ssj.BinnedTally;
import umontreal.iro.lecuyer.stat.Tally;

public class DeviationBinnedTallyDisplay extends AbstractBinnedTallyDisplay<YIntervalSeries> {
		
	public DeviationBinnedTallyDisplay( String frame, BinnedTally ... args ) {
		super( frame, args ) ;
	}

	public DeviationBinnedTallyDisplay( Collection<BinnedTally> args ) {
		super( null, args.toArray( new BinnedTally[ args.size() ]) ) ;
	}
	
	public DeviationBinnedTallyDisplay( String frame, Collection<BinnedTally> args ) {
		super( frame, args.toArray( new BinnedTally[ args.size() ]) ) ;
	}

	
	protected void computeChartSeries( int tsi, BinnedTally ts ) {
		YIntervalSeries a = (YIntervalSeries)chart.getSeries(ts.name) ;

		a.clear() ;
		
		double x = ts.binSize ;
		for( Tally i : ts.bins ) {
			if( i.numberObs() > 2 ) {
				double y = i.average() ;
				double l = Math.max( i.min(), i.average() - i.standardDeviation()), h = Math.min( i.max(), i.average() + i.standardDeviation() );
				if( displayAverageOnly ) {
					a.add(x * xScale, y * yScale, y * yScale, y * yScale) ;	
				} else {
					a.add(x * xScale, y * yScale, l * yScale, h * yScale) ;
					//a.add(x * xScale, y, i.min(), i.max()) ;
				}
			}
			x += ts.binSize ;
		}

		
//		YIntervalSeries b = (YIntervalSeries)chart.getSeries(ts.name + "@") ;
//		chart.copySeriesColors(ts.name, ts.name + "@") ;
//		b.clear() ;
//
//		int j = tsi ;
//		x = ts.binSize / 2 ;
//		for( Tally i : ts.bins ) {
//			if( i.numberObs() > 2 && (++j % 2 == 0 ) ) {
//				double y = i.average() ;
//				b.add(x * xScale, y * yScale, y * yScale, y * yScale) ;	
//			}
//			x += ts.binSize ;
//		}
	}
		
	@Override
	protected void createChart() {
		chart = new XYDeviationLineChart("no title", 0.0, "y", "x") ;
		chart.setAlpha(0.25);
	}

	protected void init() {
		super.init() ;
		prepare() ;
		replaceLegend() ;
	}
	
	public void replaceLegend() {
		
		final LegendItemCollection nlic = new LegendItemCollection() ;
		LegendItemCollection lic = chart.chart().getXYPlot().getLegendItems()  ;
		
		Shape shape = null ;
		for( Iterator<?> i = lic.iterator() ; i.hasNext() ; ) {
			LegendItem j = (LegendItem) i.next()  ;			
			if( shape == null )
				shape = j.getShape() ;		
			
			shape = new Line( new XY(0,0), new XY(25,0)) ;
			Stroke stroke = new BasicStroke(10.0f) ;
			String label = j.getLabel() ;
			nlic.add( new LegendItem( label == null ? j.getLabel() : label, j.getDescription(), "", "", shape, stroke, j.getLinePaint() ) ) ;
		}
		
		LegendItemSource source = new LegendItemSource() {			
			@Override
			public LegendItemCollection getLegendItems() {
				return nlic;
			}
		};		

		chart.chart().getLegend().setItemFont( chart.chart().getTitle().getFont().deriveFont( Font.PLAIN).deriveFont( 15.0f ) ) ;
		chart.chart().getLegend().setSources(  new LegendItemSource[] { source }  ) ;
	}
	
	boolean displayAverageOnly = Globals.get("Catadupa_DisplayAverageOnly", false ) ; // TODO Make this generic...
}
