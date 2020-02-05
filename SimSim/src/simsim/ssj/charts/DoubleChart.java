package simsim.ssj.charts;

import static simsim.core.Simulation.Gui;

import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Arrays;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.RectangleInsets;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.charts.Charts;

public class DoubleChart implements Displayable {

	AbstractBinnedTallyDisplay<?> chart1, chart2;
	XYPlot plot1, plot2, parent;
	
	JFreeChart chart ;

	private int WIDTH, HEIGHT;
	public DoubleChart( String frame, boolean shareRange, String title, String stitle, String stitle1, String stitle2, AbstractBinnedTallyDisplay<?>  chart1, AbstractBinnedTallyDisplay<?> chart2 ) {
		this.chart1 = chart1 ; 
		this.chart2 = chart2 ;
		

		try {
			plot1 = (XYPlot)chart1.chart().chart().getXYPlot() ;
			plot2 = (XYPlot)chart2.chart().chart().getXYPlot() ;

			plot2.setRenderer( plot1.getRenderer() ) ;
			
			ValueAxis lda = new NumberAxis(stitle1), rda = new NumberAxis(stitle2) ;
			
			lda.setTickMarksVisible(false) ;
			rda.setTickMarksVisible(false) ;	

			lda.setAxisLineVisible(false) ;
			rda.setAxisLineVisible(false) ;

			lda.setTickLabelsVisible(false) ;
			rda.setTickLabelsVisible(false) ;

			float fs = chart1.chart().chart().getTitle().getFont().getSize2D() ;
			lda.setLabelFont( chart1.chart().chart().getTitle().getFont().deriveFont( fs * 0.85f) ) ;
			rda.setLabelFont( chart2.chart().chart().getTitle().getFont().deriveFont( fs * 0.85f) ) ;

			parent = null;
			if( !shareRange ) {
				WIDTH = 900 ; HEIGHT = 500 ; 
				CombinedRangeXYPlot p = new CombinedRangeXYPlot( plot1.getRangeAxis() ) ;
				p.add( plot1, 1) ;
				p.add( plot2, 1) ;
				p.setGap(10) ;
				parent = p ;
				plot1.setDomainAxes( new ValueAxis[] { plot1.getDomainAxis(), lda} ) ;
				plot2.setDomainAxes( new ValueAxis[] { plot2.getDomainAxis(), rda} ) ;				
			} else {
				WIDTH = 500 ; HEIGHT = 900 ;
				CombinedDomainXYPlot p = new CombinedDomainXYPlot( plot1.getDomainAxis() ) ;
				p.add( plot1, 1) ;
				p.add( plot2, 1) ;
				p.setGap(0.1) ;
				parent = p ;
				plot1.setDomainAxes( new ValueAxis[] { null, lda} ) ;
				plot2.setDomainAxes( new ValueAxis[] { null, rda} ) ;					
			}
						
			parent.setBackgroundPaint( RGB.WHITE );
	        parent.setDomainGridlinePaint(RGB.GRAY);
	        parent.setRangeGridlinePaint( RGB.GRAY);
	        
			chart = new JFreeChart("Demo Chart", chart1.chart().chart().getTitle().getFont(), parent, true);
			chart.setTitle( new TextTitle( title, chart1.chart().chart().getTitle().getFont() ) ) ;
			
			TextTitle sbt = new TextTitle( stitle, chart1.chart().chart().getTitle().getFont().deriveFont( fs * 0.9f) ) ;		
			chart.addSubtitle(sbt);

			chart.getLegend().setSources( chart1.chart().chart().getLegend().getSources() ) ;

			chart.setBackgroundPaint( RGB.WHITE ) ;
			chart.getLegend().setFrame(BlockBorder.NONE);
			chart.getLegend().setItemFont( chart1.chart().chart().getLegend().getItemFont() );
	        chart.setAntiAlias(true) ;
	        chart.setTextAntiAlias(true) ;

	        chart.getLegend().setPadding(0, 70, 0, 0) ;
			Gui.addDisplayable(frame, this, 0.5) ;
			Gui.setFrameTransform(frame, WIDTH, HEIGHT, 0, false) ;
			Gui.setFrameRectangle(frame, 0, 0, 2*WIDTH/3, 2*HEIGHT/3) ;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DoubleChart( String frame, boolean shareRange, String title, String stitle1, String stitle2, AbstractBinnedTallyDisplay<?>  chart1, AbstractBinnedTallyDisplay<?> chart2 ) {
		this( frame, shareRange, title, "", stitle1, stitle2, chart1, chart2) ;
	}
	
	public JFreeChart chart() {
		return chart;
	}
	
	public void saveChart(String pdfName ) throws Exception {
		chart1.prepare() ;
		chart2.prepare() ;
		Charts.saveChartToPDF(chart, pdfName, WIDTH, HEIGHT) ;
	}
	
	@Override
	public void displayOn(Canvas canvas) {
		try {
			if(chart!=null) {
				chart1.prepare() ;
				chart2.prepare() ;
				chart.draw( canvas.gs, new Rectangle(0,0,WIDTH, HEIGHT) );
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}	
}
