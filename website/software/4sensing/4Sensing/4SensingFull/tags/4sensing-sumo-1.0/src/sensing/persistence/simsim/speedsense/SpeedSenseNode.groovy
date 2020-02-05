package sensing.persistence.simsim.speedsense;

import static sensing.persistence.simsim.PipelineSimulation.display;
import sensing.persistence.simsim.MobileNode;
import sensing.persistence.simsim.Node;
import sensing.persistence.simsim.msg.PipelineSimMessage;
import sensing.persistence.simsim.speedsense.SpeedSenseSim;
import sensing.persistence.core.ServicesConfig;
import sensing.persistence.core.network.NetworkMessage;
import sensing.persistence.core.network.Peer;
import sensing.persistence.core.sensors.SensorData;
import sensing.persistence.core.vtable.VTableDefinition;

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
import sensing.persistence.core.sensors.GPSReading;

import static simsim.core.Simulation.rg;
import static sensing.persistence.simsim.speedsense.SpeedSenseSim.mapModel;
import static sensing.persistence.simsim.speedsense.SpeedSenseSim.mapView;

public class SpeedSenseNode extends Node {
	CachedLatLon fixedLatLon;
	double startMinNodeDistance;
	
	public SpeedSenseNode() {
		super();
		//mNode = new MobileNode(this);
		startMinNodeDistance = 	SpeedSenseSim.setup.NODE_LOCATION_CHANGE_PERIOD ? 0 : SpeedSenseSim.setup.MIN_NODE_DISTANCE;
	}
	
	
	HashSet handledSegments = new HashSet()
	
//	public Peer sensorInput(SGPSReading r) {
//		Peer dest = super.sensorInput(r);
//		if(dest) dest.handledSegments.add(r.segmentId)
//		return dest
//	}
	
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
			double lat = SpeedSenseSim.world.y+locGenRnd.nextDouble()*SpeedSenseSim.world.height;
			double lon = SpeedSenseSim.world.x+locGenRnd.nextDouble()*SpeedSenseSim.world.width;
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
