package simsim.gui.pdf;

import java.awt.Graphics2D;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;


public class PDF {

	/**
	 * Save chart as PDF file. Requires iText library.
	 * 
	 * @param chart
	 *            JFreeChart to save.
	 * @param fileName
	 *            Name of file to save chart in.
	 * @param width
	 *            Width of chart graphic.
	 * @param height
	 *            Height of chart graphic.
	 * @throws Exception
	 *             if failed.
	 * @see <a href="http://www.lowagie.com/iText">iText</a>
	 */
	@SuppressWarnings("deprecation")
	static public void saveToPDF(Displayable d, String fileName, int width, int height) {
		try {
			BufferedOutputStream out = null;
			try {
				out = new BufferedOutputStream(new FileOutputStream(fileName));

				// convert chart to PDF with iText:
				Rectangle pagesize = new Rectangle(width, height);
				Document document = new Document(pagesize, 50, 50, 50, 50);
				try {
					PdfWriter writer = PdfWriter.getInstance(document, out);
					document.addAuthor("SimSim");
					document.open();

					PdfContentByte cb = writer.getDirectContent();
					PdfTemplate tp = cb.createTemplate(width, height);
					Graphics2D g2 = tp.createGraphics(width, height, new DefaultFontMapper());
					d.displayOn(new Canvas(g2, g2));
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
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
