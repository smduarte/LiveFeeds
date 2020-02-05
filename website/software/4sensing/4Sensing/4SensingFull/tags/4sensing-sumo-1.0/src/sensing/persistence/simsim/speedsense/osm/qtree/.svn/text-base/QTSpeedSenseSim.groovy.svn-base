package sensing.persistence.simsim.speedsense.osm.qtree;
import sensing.persistence.simsim.SimSetup;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.simsim.speedsense.map.setup.*;
import sensing.persistence.simsim.speedsense.map.*;
import sensing.persistence.core.ServicesConfig;
import sensing.persistence.core.query.quadtree.QTQueryService;
import sensing.persistence.core.network.NetworkService;
import simsim.gui.canvas.*;
import simsim.gui.geom.*;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import java.awt.Point;
import java.awt.geom.*;
import static sensing.persistence.core.query.quadtree.QTConstants.*;

public class QTSpeedSenseSim extends OSMSpeedSenseSim {

//	public QTSpeedSenseSim(String runId, String exit) {
//		super(runId, exit)
//	}	

	Rectangle bounds;

	protected QTSpeedSenseSim() {
		super();
		display.grid = true;
		display.tree = true;
	}
	
	protected void init() {
		super.init();
		bounds = new Rectangle(0,0,0,0);
		bounds.setRect(mapModel.bounds.asRect());
		
	}

	static final Pen minGridPen = new Pen(RGB.gray, 2, 10);
	static final Pen maxGridPen = new Pen(RGB.gray, 1, 10);
	static final Pen quadDivPen = maxGridPen;
	// ------------------------------------------------------------------------------------------------------------------
	public void displayOn( Canvas c ) {
		super.displayOn(c);
		//c.sDraw(maxGridPen,screenBounds);
		if(display.grid) drawQuadDiv(c, bounds);
	}

	
	protected void drawQuadDiv(Canvas canvas, Rectangle quad, int level = 0) {
		QTQueryService.getQuadDiv(quad).each{ subQ ->
			int inRange = NetworkService.pDBVersions[NetworkService.latestPDBVersionN].range(subQ).size();
			if(inRange >= MIN_NODES_PER_QUAD) {
				LatLon min = new LatLon(subQ.y, subQ.x);
				LatLon max = new LatLon(subQ.maxY, subQ.maxX);
				Point minP = mapView.getPoint(min);
				Point maxP = mapView.getPoint(max);
				Rectangle r = new Rectangle(new Point2D.Double(minP.x,maxP.y), maxP.x-minP.x, minP.y-maxP.y);
				
				canvas.sDraw(quadDivPen, r);
				drawQuadDiv(canvas, subQ, level+1);			
			}
		};
	}
	

}
