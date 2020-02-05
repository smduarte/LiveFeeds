package simsim.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import simsim.gui.canvas.Canvas;

@SuppressWarnings("serial")
class TileMapPanel extends ImgPanel implements InputHandler {

	JXMapViewer mapViewer;

	TileMapPanel() {
		super(true);

		mapViewer = new JXMapViewer() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);				
				backImage().drawTo( (Graphics2D)g );
			}
		};
		mapViewer.setLoadingImage(new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR));
		mapViewer.addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent evt) {
			}

			public void mouseClicked(MouseEvent evt) {
				Point pt = evt.getPoint();
				GeoPosition pos = mapViewer.convertPointToGeoPosition(new Point2D.Double(pt.getX(), pt.getY()));
				mapViewer.setZoom(Math.max(0, mapViewer.getZoom() - 1));
				mapViewer.setCenterPosition(pos);
			}
		});

		mapViewer.setVisible(true);
		super.setLayout(new GridLayout(1, 1));
		super.add(mapViewer);
		super.doLayout();
	}

	public void renderDisplayables(Canvas canvas) {
		canvas = new Canvas(canvas.gu, canvas.gu ) ;
		canvas.map = mapViewer ;
		super.renderDisplayables( canvas ) ;
	}
	
	public void addMouseMotionListener(MouseMotionListener l) {
		mapViewer.addMouseMotionListener(l);
	}
}
