package sensing.persistence.simsim.map.sumo;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.projection.Projection;

public class LinearProjection implements Projection {
	
	final Rectangle2D.Double bounds;
	final Rectangle2D.Double pBounds;
	
	public LinearProjection(Bounds bounds, ProjectionBounds pBounds) {
		this.bounds = bounds.asRect();
		this.pBounds = new Rectangle2D.Double(pBounds.min.east(), pBounds.min.north(), pBounds.max.east()-pBounds.min.east(), 
				pBounds.max.north()-pBounds.min.north());
	}

	public LatLon eastNorth2latlon(EastNorth p) {
		double lat = bounds.y + bounds.height/pBounds.height * (p.north() - pBounds.y);
		double lon = bounds.x + bounds.width/pBounds.width * (p.east() - pBounds.x);
		return new LatLon(lat, lon);
	}
	
	public EastNorth latlon2eastNorth(LatLon p) {
		double east  = pBounds.x + pBounds.width/bounds.width * (p.lon()-bounds.x);
		double north = pBounds.y + pBounds.height/bounds.height * (p.lat()-bounds.y);
		return new EastNorth(east, north);
	}

	public String getCacheDirectoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getDefaultZoomInPPD() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Bounds getWorldBoundsLatLon() {
		// TODO Auto-generated method stub
		return null;
	}



	public String toCode() {
		// TODO Auto-generated method stub
		return null;
	}

}
