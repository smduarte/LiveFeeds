package simsim.gui;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

public class TileMapFrame {

	public static enum Map { OSM, STAMEN} ; 
	
	public JXMapViewer map;

	public TileMapFrame(String name, double fps ) {
		this( name, 38.1, -7.1, 4, fps ) ;
	}
	
	public TileMapFrame(String name, double lat, double lon, double zoom, double fps ) {
		this( name, lat, lon, zoom, fps, Map.OSM) ;
	}	

	public TileMapFrame(String name, double lat, double lon, double zoom, double fps, boolean titleBar ) {
		this( name, lat, lon, zoom, fps, Map.OSM, titleBar) ;
	}	

	public TileMapFrame(String name, double lat, double lon, double zoom, double fps, Map prov ) {
		this( name, lat, lon, zoom, fps, prov, true) ;
	}
	
	public TileMapFrame(String name, double lat, double lon, double zoom, double fps, Map prov, boolean titleBar ) {
		
		map = GuiDesktop.gd.createMapFrame(name, fps, titleBar).mapViewer ;
		
		final int max = 19;


		TileFactoryInfo osm = new TileFactoryInfo(1, max - 2, max, 256, true, true, "http://tile.openstreetmap.org", "x", "y", "z") {
			public String getTileUrl(int x, int y, int zoom) {
				zoom = max - zoom;
				String url = this.baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
				return url;
			}

		};

		TileFactoryInfo stamen = new TileFactoryInfo(1, 18, 19, 256, true, true, "http://tile.stamen.com/toner/", "z", "x", "y") {
			public String getTileUrl(int x, int y, int zoom) {
				zoom = max - zoom;
				return String.format("%s/%s/%s/%s.png", this.baseURL, zoom, x, y);
			}
		};

		
		TileFactoryInfo tfi[] = new TileFactoryInfo[] {osm, stamen} ;
		
		map.setTileFactory(new DefaultTileFactory( tfi[ prov.ordinal() ] ));
		map.setCenterPosition(new GeoPosition(lat, lon));
		map.setZoom((int)zoom);
	}

	public static String TileXYToQuadKey(int tileX, int tileY, int levelOfDetail)
    {
        StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--)
        {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0)
            {
                digit++;
            }
            if ((tileY & mask) != 0)
            {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }
	
}
