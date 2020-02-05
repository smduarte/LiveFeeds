package sensing.persistence.simsim.speedsense.osm.setup.noiselevel
import apps.Grid;
import org.openstreetmap.josm.data.osm.visitor.Visitor;

import sensing.persistence.simsim.map.MapView;
import sensing.persistence.simsim.map.osm.WireframePaintVisitor;
import sensing.persistence.simsim.speedsense.osm.OSMSpeedSenseSim;
import sensing.persistence.core.query.Query;
import org.openstreetmap.josm.data.osm.visitor.Visitor;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.*;
import org.openstreetmap.josm.data.coor.LatLon;
import sensing.persistence.simsim.speedsense.osm.*;
import sensing.persistence.simsim.speedsense.setup.SpeedSenseSetup;

import simsim.gui.canvas.*;
import simsim.gui.geom.*;
import simsim.core.Displayable;

import apps.noisemap.TNoiseLevel;

class NoiseLevelSetup extends SpeedSenseSetup {
	
	def noiseLevel = [:];
	Visitor queryPainter;
	MapView queryView;
	
	static final int QDISPL_MAXSIZE = 500;
	
	static final Pen HIGH_PEN = new Pen(new RGB(RGB.RED, 0.8));
	static final Pen MED_PEN = new Pen(new RGB(RGB.ORANGE, 0.8));
	static final Pen LOW_PEN = new Pen(new RGB(RGB.GREEN, 0.8));
	
	public NoiseLevelSetup() {
		super();
		config.RUN_TIME = 750;
	}
	
	public void init(OSMSpeedSenseSim sim) {
		super.init(sim);
		sim.Gui.addDisplayable("NoiseMap",  {Canvas c -> 
			if(q && sim.display.queryResult) {
				queryPainter.setCanvas(c);
				queryPainter.visitAll(false);
				c.sDraw(sim.queryPen, queryView.latLonBoundsToScreen(q.aoi));
				noiseLevel.each{ cellId, nlevel ->
					Pen p;
					switch(nlevel.level) {
						case TNoiseLevel.NoiseClass.HIGH: p = HIGH_PEN; break;
						case TNoiseLevel.NoiseClass.MED: p = MED_PEN; break;
						default: p = LOW_PEN;
					}
					c.sFill(p, queryView.latLonBoundsToScreen(nlevel.boundingBox));
				}
		}
		//displayQueryResultOn(c, q, queryView);
		} as Displayable, 5.0);
		sim.Gui.setFrameRectangle("Query", 820, 0, QDISPL_MAXSIZE, QDISPL_MAXSIZE);
		sim.Gui.setFrameTransform("Query", sim.sideLen, sim.sideLen, 0, false) ;
	}
	
	protected void startQuery() {
		Query q = getQuery();
		println q.aoi;
		runQuery(q) { 
			def cell = it.cellId.tokenize("_");
			println "${cell[1]}_${cell[2]}";
			if(Integer.parseInt(cell[1])<14 && Integer.parseInt(cell[2])<14) {
				noiseLevel[it.cellId] = it
				println "received ${it}"
			}		
			
		};
		initQueryResultDisplay();
	}
	
	protected Query getQuery() {
		Query q = new Query("apps.noisemap.NoiseLevel").area(minLat: sim.world.y, minLon: sim.world.x, maxLat: sim.world.y+sim.world.height, maxLon: sim.world.x+sim.world.width);	
	}
	
	protected setupCharts() {return []}
	
	protected void initQueryResultDisplay() {
		queryView = new MapView(sim.mapModel, sim.sideLen, sim.sideLen);
		if(!queryPainter) queryPainter = new WireframePaintVisitor();
		queryPainter.mapView = queryView;
	}

}
