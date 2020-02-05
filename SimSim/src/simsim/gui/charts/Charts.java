package simsim.gui.charts;

import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.pdf.PDF;

public class Charts {

	/**
	* Save chart as PDF file. Requires iText library.
	*
	* @param chart JFreeChart to save.
	* @param fileName Name of file to save chart in.
	* @param width Width of chart graphic.
	* @param height Height of chart graphic.
	* @throws Exception if failed.
	* @see <a href="http://www.lowagie.com/iText">iText</a>
	*/

	static public void saveChartToPDF(final JFreeChart chart, String fileName, final int width, final int height) throws Exception {
		PDF.saveToPDF(new Displayable() {
			public void displayOn(Canvas c) {
                Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
                chart.draw(c.gs, r2D, null);
			}
		}, fileName, width, height) ;
	}
}
