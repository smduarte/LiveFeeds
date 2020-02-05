package sensing.persistence.simsim.map.sumo;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import sensing.persistence.simsim.map.MapRenderer;
import sensing.persistence.simsim.map.MapView;
import sensing.persistence.simsim.map.sumo.SUMOMapModel.Edge;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;

public class WireframeRenderer implements MapRenderer {
	
	MapView mapView;
	Rectangle2D viewPort;
	SUMOMapModel mapModel;
	Canvas canvas;
	Collection<SUMOMapModel.Edge> edges;

	public void setCanvas(Canvas c) {
		this.canvas = c;
	}

	public void setMapView(MapView mapView) {
		this.mapView = mapView;
		this.mapModel = (SUMOMapModel)mapView.getModel();
	}
	
	public void setViewPort(Rectangle2D viewPort) {
		this.viewPort = viewPort;
		edges = null;
		
		Stroke stroke = new BasicStroke( (float) Math.min(6, mapView.getZoomFactor()) );
		defaultPen.stroke = stroke;
		normalPen.stroke = stroke;
		congestedPen.stroke = stroke;
		stoppedPen.stroke = stroke;
		selectedPen.stroke = stroke;
	}
	

	protected Pen defaultPen 	= new Pen( RGB.LIGHT_GRAY, 5);
	protected Pen normalPen 	= new Pen( new RGB(0x008000), 5);
	protected Pen congestedPen 	= new Pen( new RGB(0xFF8000), 5);
	protected Pen stoppedPen 	= new Pen(new RGB(0x800000), 5);
	protected Pen viewPortPen 	= new Pen( RGB.BLACK, 2);
	protected Pen selectedPen	= new Pen( RGB.CYAN);
	
	public void display() {

		if(edges == null) {
			edges = mapModel.getEdges(viewPort);
		}	
		for(SUMOMapModel.Edge  e : edges) {
			display(e);
		}
		
		String selected = mapView.getSelectedSegment();
		if(selected != null) {
			SUMOMapModel.Edge e = mapModel.getEdge(selected);
			if(e != null) {
				displaySelected(e);
			}
		}
	}
	
	protected void display(SUMOMapModel.Edge e) {
		Pen pen;
//		if(e.avgSpeed < 0) {
//			pen = defaultPen;
//		} else if(e.avgSpeed == 0) {
//			pen = stoppedPen;
//		} else if(e.avgSpeed < 0.5 * e.maxSpeed) {
//			pen = congestedPen;
//		} else {
//			pen = normalPen;
//		}
		
		if(e.avgDensity >= 50 && e.avgSpeed <= 10) {
			pen = stoppedPen;
		} else {
			pen = defaultPen;
		}
		//pen = e.hasTrafficLight ? stoppedPen : normalPen;

		display(e, pen);		
	}
	
	protected void displaySelected(SUMOMapModel.Edge e) {
		display(e, selectedPen);
	}
	
	protected void display(SUMOMapModel.Edge e, Pen pen) {
		canvas.sDraw(pen, mapView.getTransformedShape(e.shape));	
	}
}
