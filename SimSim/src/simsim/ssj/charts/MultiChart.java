package simsim.ssj.charts;

import static simsim.core.Simulation.Gui;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.charts.AbstractXYChart;
import simsim.gui.charts.Charts;

public class MultiChart implements Displayable {

	private int WIDTH, HEIGHT;
	List<AbstractBinnedTallyDisplay<?>> subCharts ;
	
	JFreeChart chart ;

	public MultiChart( String frame, boolean shareRange, String title, AbstractBinnedTallyDisplay<?> ... list ) {
		this( frame, shareRange, title, Arrays.asList( list ) ) ;
	}
	
	public void setSubChartTitle(int index, String subTitle ) {
		AbstractXYChart<?> first = subCharts.get(0).chart() ;
		
		ValueAxis va = new NumberAxis( subTitle ) ;
		va.setTickMarksVisible(false) ;
		va.setAxisLineVisible(false) ;
		va.setTickLabelsVisible(false) ;

		float fs = first.chart().getTitle().getFont().getSize2D() ;
		va.setLabelFont( first.chart().getTitle().getFont().deriveFont( fs * 0.85f) ) ;
		subCharts.get(index).chart().chart().getXYPlot().setDomainAxes( new ValueAxis[] { first.chart().getXYPlot().getDomainAxis(), va} ) ;
	}
	
	public MultiChart( String frame, boolean shareRange, String title, List<AbstractBinnedTallyDisplay<?>> subCharts ) {
		this.subCharts = subCharts ;
	
		try {

		XYPlot parent = null;
		if( !shareRange ) {
			WIDTH = 900 ; HEIGHT = 500 ; 
			CombinedRangeXYPlot p = new CombinedRangeXYPlot() ;	
			for( AbstractBinnedTallyDisplay<?> i : subCharts )
				p.add( i.chart.chart().getXYPlot() ) ;

			p.setGap(10) ;			
			parent = p ;
		} else {
			WIDTH = 500 ; HEIGHT = 900 ;
			CombinedDomainXYPlot p = new CombinedDomainXYPlot() ;
			for( AbstractBinnedTallyDisplay<?> i : subCharts )
				p.add( i.chart.chart().getXYPlot() ) ;

			p.setGap(10) ;
			parent = p ;
		}
		chart = new JFreeChart("Multi Chart", java.awt.Font.getFont("Helvetica"), parent, true);
		
		AbstractXYChart<?> first = subCharts.get(0).chart() ;

		parent.setBackgroundPaint( RGB.WHITE );
        parent.setDomainGridlinePaint(RGB.GRAY);
        parent.setRangeGridlinePaint( RGB.GRAY);

		chart = new JFreeChart("Demo Chart", first.chart().getTitle().getFont(), parent, true);
		chart.setTitle( new TextTitle( title, first.chart().getTitle().getFont() ) ) ;
		chart.setSubtitles( first.chart().getSubtitles() ) ;
		chart.getLegend().setSources( first.chart().getLegend().getSources() ) ;

		chart.setBackgroundPaint( RGB.WHITE ) ;
		chart.getLegend().setFrame(BlockBorder.NONE);
		chart.getLegend().setItemFont( first.chart().getLegend().getItemFont() );
        chart.setAntiAlias(true) ;
        chart.setTextAntiAlias(true) ;

        chart.getLegend().setPadding(0, 70, 0, 0) ;
		Gui.addDisplayable(frame, this, 0.5) ;
		Gui.setFrameTransform(frame, WIDTH, HEIGHT, 0, false) ;
		Gui.setFrameRectangle(frame, 0, 0, 2*WIDTH/3, 2*HEIGHT/3) ;
		
		config() ;
		} catch( Exception x ) {
			x.printStackTrace() ;
		}
	}
	
	public void config() {
		
	}
	
	public void saveChart(String pdfName ) throws Exception {
		for( AbstractBinnedTallyDisplay<?> i : subCharts ) {
			i.prepare() ;
		}
 		Charts.saveChartToPDF(chart, pdfName, WIDTH, HEIGHT) ;
	}
	
	@Override
	public void displayOn(Canvas canvas) {
		try {
			if(chart!=null) {
				for( AbstractBinnedTallyDisplay<?> i : subCharts ) {
					i.prepare() ;
				}
				chart.draw( canvas.gs, new Rectangle(0,0,WIDTH, HEIGHT) );
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}	
}
