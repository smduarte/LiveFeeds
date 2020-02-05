package livefeeds.sift0.charts;

import static simsim.core.Simulation.Gui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.util.ShapeUtilities;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Square;
import simsim.gui.geom.XY;
import simsim.ssj.charts.AbstractBinnedTallyDisplay;

public class DoubleHorizontalChart implements Displayable {

	AbstractBinnedTallyDisplay<?> lchart, rchart;
	
	JFreeChart cc ;
	
	public DoubleHorizontalChart( String frame, String title, String ltitle, String rtitle, AbstractBinnedTallyDisplay<?>  lchart, AbstractBinnedTallyDisplay<?> rchart ) {
		this.lchart = lchart ; this.rchart = rchart ;
		
		Gui.addDisplayable(frame, this, 1) ;
		Gui.setFrameTransform(frame, 900, 500, 0, false) ;
		Gui.setFrameRectangle(frame, 0, 0, 900, 500) ;
	
		try {
			XYPlot lplot = (XYPlot)lchart.chart().chart().getXYPlot() ;
			XYPlot rplot = (XYPlot)rchart.chart().chart().getXYPlot() ;

			ValueAxis ra = lplot.getRangeAxis() ;
			ra.setLabelPaint(RGB.WHITE) ;
			
			final CombinedRangeXYPlot parent = new CombinedRangeXYPlot( ra );
						
			ValueAxis lda = new NumberAxis(ltitle), rda = new NumberAxis(rtitle) ;
			
			lda.setTickMarksVisible(false) ;
			rda.setTickMarksVisible(false) ;	

			lda.setAxisLineVisible(false) ;
			rda.setAxisLineVisible(false) ;

			lda.setTickLabelsVisible(false) ;
			rda.setTickLabelsVisible(false) ;
			
			float fs = lchart.chart().chart().getTitle().getFont().getSize2D() ;
			lda.setLabelFont( lchart.chart().chart().getTitle().getFont().deriveFont( fs * 0.85f) ) ;
			rda.setLabelFont( lchart.chart().chart().getTitle().getFont().deriveFont( fs * 0.85f) ) ;
			
			lplot.setDomainAxes( new ValueAxis[] { lplot.getDomainAxis(), lda} ) ;
			rplot.setDomainAxes( new ValueAxis[] { rplot.getDomainAxis(), rda} ) ;
			
			parent.add( lplot, 1 ) ;
			parent.add( rplot, 1 ) ;
			parent.setGap(0.1) ;
			parent.setBackgroundPaint( RGB.WHITE );
	        parent.setDomainGridlinePaint(RGB.GRAY);
	        parent.setRangeGridlinePaint( RGB.GRAY);

			cc = new JFreeChart("Demo Chart", lchart.chart().chart().getTitle().getFont(), parent, true);
			cc.setTitle( new TextTitle( title, lchart.chart().chart().getTitle().getFont() ) ) ;
			cc.setSubtitles( lchart.chart().chart().getSubtitles() ) ;
			cc.getLegend().setSources( lchart.chart().chart().getLegend().getSources() ) ;

			cc.setBackgroundPaint( RGB.WHITE ) ;
			cc.getLegend().setFrame(BlockBorder.NONE);
			cc.getLegend().setItemFont( lchart.chart().chart().getLegend().getItemFont() );
	        cc.setAntiAlias(true) ;
	        cc.setTextAntiAlias(true) ;


		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	public void saveChart(String pdfName ) {
		try {
			saveChartToPDF(pdfName, 900, 500) ;
		} catch( Exception x ) {
			x.printStackTrace() ;
		}		
	}
	
	@Override
	public void displayOn(Canvas canvas) {
		
		final Rectangle chartArea = new Rectangle(0, 0, 900, 500);
		try {
			if(cc!=null)
				cc.draw( canvas.gs, chartArea);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	private void saveChartToPDF(String fileName, int width, int height) throws Exception {
	    if (cc != null) {
	        BufferedOutputStream out = null;
	        try {
	            out = new BufferedOutputStream(new FileOutputStream(fileName));
	               
	            //convert chart to PDF with iText:
	            com.itextpdf.text.Rectangle pagesize = new com.itextpdf.text.Rectangle(width, height);
	            Document document = new Document(pagesize, 50,50,50,50) ;
	            try {
	                PdfWriter writer = PdfWriter.getInstance(document, out);
	                document.addAuthor("JFreeChart");
	                document.open();
	       
	                PdfContentByte cb = writer.getDirectContent();
	                PdfTemplate tp = cb.createTemplate(width, height);
	                Graphics2D g2 = tp.createGraphics(width, height, new DefaultFontMapper());
	       
	                Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
	                cc.draw(g2, r2D, null);
	                g2.dispose();
	                cb.addTemplate(tp, 0, 0);
	            } finally {
	                document.close();
	            }
	        } finally {
	            if (out != null) {
	                out.close();
	            }
	        }
	    }//else: input values not available
	}//saveChartToPDF()
	
}
