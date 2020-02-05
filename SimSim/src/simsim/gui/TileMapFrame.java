package simsim.gui;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

public class TileMapFrame {

	public static enum Map { CLOUDMADE, OSM, MAPQUEST, OVI } ; 
	
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
//		final TileFactoryInfo googleMaps = new TileFactoryInfo(1, max - 2, max, 256, true, true, "http://mt1.google.com/vt/lyrs=m", "x", "y", "z") {
//			public String getTileUrl(int x, int y, int zoom) {
//				zoom = max - zoom;
//				return String.format("%s&x=%s&y=%s&z=%s", baseURL, x, y, zoom);
//			}
//		};

		TileFactoryInfo cloudmade = new TileFactoryInfo(1, max - 2, max, 256, true, true, "http://b.tile.cloudmade.com/9a2ba90d32a25736a326c403beb36633/53624/256", "x", "y", "z") {
			public String getTileUrl(int x, int y, int zoom) {
				zoom = max - zoom;
				String url = this.baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
				return url;
			}
		};

		TileFactoryInfo osm = new TileFactoryInfo(1, max - 2, max, 256, true, true, "http://tile.openstreetmap.org", "x", "y", "z") {
			public String getTileUrl(int x, int y, int zoom) {
				zoom = max - zoom;
				String url = this.baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
				return url;
			}

		};

		TileFactoryInfo mapQuest = new TileFactoryInfo(1, max - 2, max, 256, true, true, "http://otile1.mqcdn.com/tiles/1.0.0/osm", "x", "y", "z") {
			public String getTileUrl(int x, int y, int zoom) {
				zoom = max - zoom;
				String url = this.baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
				return url;
			}
		};


		TileFactoryInfo ovi = new TileFactoryInfo(1, max - 2, max, 256, true, true, "http://maptile.maps.svc.ovi.com/maptiler/maptile/newest/terrain.day", "x", "y", "z") {
			public String getTileUrl(int x, int y, int zoom) {
				zoom = max - zoom;
				String url = this.baseURL + "/" + zoom + "/" + x + "/" + y + "/256/png8";
				return url;
			}
		};

		TileFactoryInfo bing = new TileFactoryInfo(1, 18, 19, 256, true, true, "http://ecn.t2.tiles.virtualearth.net/tiles/r", "x", "y", "z") {
			public String getTileUrl(int x, int y, int zoom) {
				zoom = max - zoom;
				String url = this.baseURL + TileXYToQuadKey(x,y,zoom) + "?g=761&mkt=en-us&shading=hill";
				return url;
			}
		};

		TileFactoryInfo tfi[] = new TileFactoryInfo[] {cloudmade, osm, mapQuest, ovi} ;
		
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
