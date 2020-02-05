package sensing.simsim.sys;

import static sensing.simsim.sys.PipelineSimulation.display;
import sensing.simsim.sys.msg.PipelineSimMessage;
import sensing.core.ServicesConfig;
import sensing.core.network.NetworkMessage;
import sensing.core.network.Peer;
import sensing.core.sensors.SensorData;
import sensing.core.vtable.VTableDefinition;
import sensing.core.sensors.GPSReading;

import simsim.core.EndPoint;
import simsim.core.Globals;
import simsim.gui.geom.*;
import simsim.gui.canvas.*;

import org.openstreetmap.josm.data.coor.*;
import org.openstreetmap.josm.data.osm.BBox;
import java.awt.geom.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import static simsim.core.Simulation.rg;
import static sensing.simsim.sys.Sim4Sensing.mapModel;
import static sensing.simsim.sys.Sim4Sensing.mapView;

public class Sim4SensingNode extends Node {
	CachedLatLon fixedLatLon;
	double startMinNodeDistance;
	
	public Sim4SensingNode() {
		super();
		startMinNodeDistance = 	Sim4Sensing.setup.NODE_LOCATION_CHANGE_PERIOD ? 0 : Sim4Sensing.setup.MIN_NODE_DISTANCE;
	}
	
	
	HashSet handledSegments = new HashSet()
		
	public List newPeerLocation(Closure getNearest) {
		invalidatePos();
		return getRandomPeerLocation(getNearest);
	}
	
	protected List getRandomPeerLocation(Closure getNearest) {
		CachedLatLon coor;
		boolean done = false;
		int tries = 1
		double minDistance = startMinNodeDistance;
		while(!done) {
			double lat = Sim4Sensing.world.y+locGenRnd.nextDouble()*Sim4Sensing.world.height;
			double lon = Sim4Sensing.world.x+locGenRnd.nextDouble()*Sim4Sensing.world.width;
			if(!mapModel.isInLand(lat,lon)) continue;

			coor = new CachedLatLon(lat,lon);
			Map nearest;
			double distance;
			if(minDistance == 0 || ((nearest = getNearest(coor.lon(), coor.lat())) == null) ||  ((distance = coor.greatCircleDistance(new LatLon(nearest.y, nearest.x))) >= minDistance && distance != Double.NaN)) {
				done = true;
				break;
			} else if(++tries % 99 == 0) {
				minDistance *= 0.85
			}
		}
		fixedLatLon = coor;
		return [coor.lon(), coor.lat()]
	}
	
	protected XY cachedPos;
	public XY getPos() {
		if(!cachedPos) {
			Point p = mapView.getPoint(fixedLatLon);
			cachedPos = new XY(p.x, p.y);
		}
		return cachedPos;
	}
	protected XY invalidatePos() {
		cachedPos = null;	
	}
	

	
	static final Pen segmentExtentPen = new Pen(RGB.BLUE, 1, 5);
	static final Pen filterPen = new Pen(RGB.DARK_GRAY, 2, 7);
	
	public void displayOn(Canvas c) {
		super.displayOn(c);
		if(display.tree) {
			def queryInstances = services.query.getQueryInstances();
			if(queryInstances) {
				Map qi = queryInstances[0];
	
				List pens = [treePen, leafTreePen ];
				[qi.contexts.global, qi.contexts[VTableDefinition.DATASRC]].eachWithIndex{qCtx,i->
					if(qCtx && qCtx.parent) {
						c.sDraw(pens[i], new Line(pos.x, pos.y, qCtx.parent.pos.x, qCtx.parent.pos.y));
					}
				}
	
//				if(qi.contexts.global) {
//					c.sDraw(treePen, "${qi.contexts.global.level}", pos.x+10, pos.y+10);
//				}
				// if is root && has searchArea (QTree)
				if(config.queryImplPolicy == ServicesConfig.QueryImplPolicy.QUAD_TREE && qi.contexts.global && !qi.contexts.global.parent) {
					//canvas.sDraw(queryPen, qi.global.query.aoi);
					c.sDraw(saPen, mapView.latLonBoundsToScreen(qi.contexts.global.query.searchArea));
					qi.contexts.global.quadIntercept.each{
						if(!it.isEmpty()) {
							c.sDraw(quadPen, mapView.latLonBoundsToScreen(it));
						}
					}
				}
					
			}
		}
	}
	

	public void displayDetailOn(Canvas c) {
		super.displayDetailOn(c);
		if(display.state) {
			def state = services.query.getDSWindowContent().collect{it.segmentId}
			//services.query.getState(display.stateFilter);
			if(state) {
				state.clone().unique().each { id ->
					Rectangle extent = mapModel.getSegmentExtent(id);
					c.sDraw(segmentExtentPen, mapView.latLonBoundsToScreen(extent));
					//println "[${nodeId}} SEGCOUNT: ${id}: ${state.count(id)}"
				}
			}
		}
	}
	
	public invalidateDisplay() {
		cachedPos = null;
		mNodes*.invalidateDisplay();
	}
	
	
}
