package simsim.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.painter.Painter;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;

@SuppressWarnings("serial")
class MapPanel extends ImgPanel implements InputHandler {


	
	JXMapViewer mapViewer ;
	Painter<JXMapViewer> painter ;

	
	MapPanel() {
		super();
		
		mapViewer = new JXMapViewer() {
			public void paintComponent( Graphics g ) {
				super.paintComponent(g) ;
				//backImage().drawTo((Graphics2D) g);
				
				Graphics2D g2d = (Graphics2D)g.create() ;	
//				int zoom = this.getZoom() ;
//				double sf = 0.1 / zoom ;
//				g2d.scale( sf, sf ) ;
				renderDisplayables( new Canvas( g2d, g2d)) ;
				g2d.dispose() ;
			}	
		};
		
		mapViewer.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent evt) {
				System.out.println( evt ) ;
			}

			public void mouseMoved(MouseEvent evt) {
				System.out.println( evt ) ;
			}
		});
			
		final int max = 17;
        TileFactoryInfo cloudmade = new TileFactoryInfo(1,max-2,max, 256, true, true, // tile size is 256 and x/y orientation is normal
                "http://b.tile.cloudmade.com/http://b.tile.cloudmade.com/9a2ba90d32a25736a326c403beb36633/46800/256",
                "x","y","z") {
            public String getTileUrl(int x, int y, int zoom) {
                zoom = max-zoom;
                String url = this.baseURL +"/"+zoom+"/"+x+"/"+y+".png";
                return url;
            }

        };
//        TileFactoryInfo googleMaps = new TileFactoryInfo(1,max-2,max, 256, true, true, // tile size is 256 and x/y orientation is normal
//                "http://mt1.google.com/vt/v=w2.106",
//                "x","y","z") {
//            public String getTileUrl(int x, int y, int zoom) {
//            	zoom = max - zoom ;
//                return String.format("%s&x=%s&y=%s&z=%s", baseURL, x, y, zoom) ;
//            }
//
//        };
        
        mapViewer.setTileFactory(new DefaultTileFactory(cloudmade));
        mapViewer.setZoom(4);
        mapViewer.setCenterPosition( new GeoPosition(38.728711,-9.149981)) ;
        
        mapViewer.setVisible(true) ;        
        super.setLayout( new GridLayout(1,1)) ;
        super.add( mapViewer ) ;
        super.doLayout() ;
	}

	public void renderDisplayables(Canvas canvas) {
		//super.paintComponent( canvas.gs );		
		for (Displayable i : displayables) {
			i.displayOn(canvas);
		}
	}
}
