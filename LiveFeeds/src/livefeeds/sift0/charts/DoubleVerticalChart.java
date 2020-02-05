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
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
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

public class DoubleVerticalChart implements Displayable {

	AbstractBinnedTallyDisplay<?> tchart, bchart;
	
	JFreeChart cc ;
	
	private static final int RW = 500, RH = 900 ;
	
	public DoubleVerticalChart( String frame, String title, String ttitle, String btitle, AbstractBinnedTallyDisplay<?>  tchart, AbstractBinnedTallyDisplay<?> bchart ) {
		this.tchart = tchart ; this.bchart = bchart ;
		
		Gui.addDisplayable(frame, this, 1) ;
		Gui.setFrameTransform(frame, RW, RH, 0, false) ;
		Gui.setFrameRectangle(frame, 500, 0, 2*RW/3, 2*RH/3) ;
	
		try {
			XYPlot tplot = (XYPlot)tchart.chart().chart().getXYPlot() ;
			XYPlot bplot = (XYPlot)bchart.chart().chart().getXYPlot() ;
			
			ValueAxis ra = tplot.getDomainAxis() ;
//			ra.setLabelPaint(RGB.WHITE) ;
			
			final CombinedDomainXYPlot parent = new CombinedDomainXYPlot( ra );
						
			ValueAxis tda = new NumberAxis(ttitle), bda = new NumberAxis(btitle) ;
		
			tda.setTickMarksVisible(false) ;
			bda.setTickMarksVisible(false) ;	

			tda.setAxisLineVisible(false) ;
			bda.setAxisLineVisible(false) ;
			
			tda.setTickLabelsVisible(false) ;
			bda.setTickLabelsVisible(false) ;
			
			float fs = tchart.chart().chart().getTitle().getFont().getSize2D() ;
			tda.setLabelFont( tchart.chart().chart().getTitle().getFont().deriveFont( fs * 0.85f) ) ;
			bda.setLabelFont( tchart.chart().chart().getTitle().getFont().deriveFont( fs * 0.85f) ) ;
			
			tplot.setDomainAxes( new ValueAxis[] { tplot.getRangeAxis(), tda} ) ;
			bplot.setDomainAxes( new ValueAxis[] { bplot.getRangeAxis(), bda} ) ;
			
			parent.add( tplot, 1 ) ;
			parent.add( bplot, 1 ) ;
			parent.setGap(0.05) ;
			parent.setBackgroundPaint( RGB.WHITE );
	        parent.setDomainGridlinePaint(RGB.GRAY);
	        parent.setRangeGridlinePaint( RGB.GRAY);

			cc = new JFreeChart("Demo Chart", tchart.chart().chart().getTitle().getFont(), parent, true);
			cc.setTitle( new TextTitle( title, tchart.chart().chart().getTitle().getFont() ) ) ;
			cc.setSubtitles( tchart.chart().chart().getSubtitles() ) ;
			cc.getLegend().setSources( tchart.chart().chart().getLegend().getSources() ) ;

			cc.setBackgroundPaint( RGB.WHITE ) ;
			cc.getLegend().setItemFont( tchart.chart().chart().getLegend().getItemFont() );
	        cc.setAntiAlias(true) ;
	        cc.setTextAntiAlias(true) ;


	        cc.getLegend().setPadding(0, 60,0, 0) ;
	        for( int i = 0 ; i < cc.getSubtitleCount() ;i++ )
	        	cc.getSubtitle(i).setPadding(0, 60, 0, 0) ;
	        
	        cc.getTitle().setPadding(0, 60, 0, 0) ;
	        
	      
//			cc.getLegend().setFrame(BlockBorder.NONE);

	        
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	public void saveChart(String pdfName ) {
		try {
			saveChartToPDF(pdfName, RW, RH) ;
		} catch( Exception x ) {
			x.printStackTrace() ;
		}		
	}
	
	@Override
	public void displayOn(Canvas canvas) {
		
		final Rectangle chartArea = new Rectangle(0, 0, RW, RH);
		try {
			if(cc!=null)
				cc.draw( canvas.gs, chartArea);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
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
