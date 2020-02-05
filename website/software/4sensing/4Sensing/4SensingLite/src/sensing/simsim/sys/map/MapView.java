package sensing.simsim.sys.map;

import java.awt.Point;
import java.awt.Shape;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.projection.Projection;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.CachedLatLon;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.DataSource;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.projection.*;

import simsim.gui.canvas.Canvas;
import simsim.gui.geom.Rectangle;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;


public class MapView {

	final MapModel mapModel;
	final ProjectionBounds pBounds;
	final Projection proj;
	final double width;
	final double height;
	
	MapRenderer renderer;
	Rectangle2D.Double viewPort;
	AffineTransform aT, iT;

	public MapView(MapModel mapModel, Projection proj, double width, double height, MapRenderer renderer) {
		super();
		this.mapModel = mapModel;
		this.pBounds = mapModel.getProjectionBounds();
		this.proj = proj;
		this.width = width;
		this.height = height;
    	this.renderer = renderer;
    	renderer.setMapView(this);
		zoomTo(getDefaultCenter(), 1);
    	renderer.setViewPort(viewPort);
	}
	
	public MapModel getModel() {
		return mapModel;
	}

    public Projection getProjection() {
        return proj;
    }
    
    public double getWidth() {
    	return width;
    }
    
    public double getHeight() {
    	return height;
    }
    
    public Rectangle2D.Double getViewPort(){
    	return viewPort;
    }
    
    public Rectangle2D.Double getScreenBounds() {
		double pW = pBounds.max.east() - pBounds.min.east();
		double pH = pBounds.max.north() - pBounds.min.north();
    	return new Rectangle2D.Double(pBounds.min.east(), pBounds.min.north(), pW, pH);
    	
    }

	
	public void zoomTo(EastNorth newCenter, int zoomLevel) {
		double pW = pBounds.max.east() - pBounds.min.east();
		double pH = pBounds.max.north() - pBounds.min.north();

		doZoomTo(new Rectangle2D.Double(newCenter.east()-pW/2/zoomLevel, newCenter.north()-pH/2/zoomLevel, pW/zoomLevel, pH/zoomLevel));	
	}
	
	public void zoomToFactor(double zFactor) {
		doZoomTo(new Rectangle2D.Double(viewPort.getCenterX()-viewPort.width/zFactor/2, viewPort.getCenterY()-viewPort.height/zFactor/2,
				viewPort.width/zFactor, viewPort.height/zFactor));
	}
	
	public void center(EastNorth newCenter) {
		System.out.println("New center: " + newCenter);
		doZoomTo(new Rectangle2D.Double(newCenter.east()-viewPort.width/2, newCenter.north()-viewPort.height/2,
				viewPort.width, viewPort.height));
	}
	
	
	public void zoomTo(Bounds b) {
		doZoomTo(getProjectedBounds(b));
	}
	
	public void zoomTo(Rectangle2D bounds) {
		EastNorth minEN = proj.latlon2eastNorth(new LatLon(bounds.getMinY(), bounds.getMinX()));
		EastNorth maxEN = proj.latlon2eastNorth(new LatLon(bounds.getMaxY(), bounds.getMaxX()));
		doZoomTo(new Rectangle2D.Double(minEN.east(), minEN.north(), maxEN.east()-minEN.east(), maxEN.north()-minEN.north()));
		
	}
	
	public double getZoomFactor() {
		double zx = (pBounds.max.east() - pBounds.min.east()) / viewPort.width;
		double zy = (pBounds.max.north() - pBounds.min.north())  / viewPort.height;
		
		return zx > zy ? zx : zy;
	}
	

	
	protected void doZoomTo(Rectangle2D.Double vp) {
		System.out.println("New viewport: " + vp.x +","+vp.y+","+vp.getMaxX()+","+vp.getMaxY());
		viewPort = vp;
		renderer.setViewPort(viewPort);
		aT = new AffineTransform();
		aT.translate(width/2, height/2);
		aT.scale(1.0, -1.0);
		aT.translate(-width/2, -height/2);
		double sx = width / viewPort.width;
		double sy = height / viewPort.height;
		double scale = sx < sy ? sx : sy;
		aT.scale(scale, scale);
		aT.translate(-viewPort.x, -viewPort.y);	
		iT = new AffineTransform(aT);
		try {
			iT.invert();
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
	}
	
	String selectedSegment;
	public void selectSegment(String segmentId) {
		this.selectedSegment = segmentId;
		
	}
	
	public String getSelectedSegment() {
		return selectedSegment;
	}
	
	public void displayOn(Canvas c) {
		renderer.setCanvas(c);
		renderer.display();
	}


    public Point getPoint(EastNorth en) {   
    	Point2D.Double pSrc; 
    	Point2D.Double pDst = new Point2D.Double();
    	if(null != en) {
    		pSrc = new Point2D.Double(en.east(), en.north());
    		aT.transform(pSrc, pDst);
    	} 
    	return new Point((int) pDst.x, (int) pDst.y);
    }
    
    public EastNorth getEastNorth(Point p) {
    	Point2D enP = getENPoint(p);
    	return new EastNorth(enP.getX(), enP.getY());
    }
    
    public Point2D getENPoint(Point p) {
    	Point2D.Double pSrc = new Point2D.Double(p.x, p.y);
    	Point2D.Double pDst = new Point2D.Double();
    	iT.transform(pSrc, pDst);
    	return pDst;
    }
    
    public Point getPoint(Node n) {
        return getPoint(n.getEastNorth());
    }

    public Point getPoint(LatLon coor) {
    	return getPoint(proj.latlon2eastNorth(coor));
    }

    public Shape getTransformedShape(Shape in) {
    	return aT.createTransformedShape(in);
    }


//    private static double R = 6371000; // m
//    
//    public double getScaledDistance(double d) {
//    	LatLon origin = mapModel.getBounds().getMin();
//    	double lat2 = Math.asin( Math.sin(origin.lat()*Math.PI /180)*Math.cos(d/R) +  Math.cos(origin.lat()*Math.PI/180)*Math.sin(d/R));
//    	LatLon dest = new LatLon(lat2*180/Math.PI, origin.lon());
//
//    	EastNorth originEN = proj.latlon2eastNorth(origin);
//    	EastNorth destEN = proj.latlon2eastNorth(dest);
//
//    	return (destEN.north()-originEN.north())/scale;
//    }

	public Rectangle latLonBoundsToScreen(Rectangle2D b) {
		LatLon min = new LatLon(b.getY(), b.getX());
		LatLon max = new LatLon(b.getMaxY(), b.getMaxX());
		Point pMin = getPoint(min);
		Point pMax = getPoint(max);
		return new Rectangle(new Point2D.Double(pMin.x, pMax.y), Math.abs(pMax.x-pMin.x), Math.abs(pMax.y - pMin.y));
	}
	
	public Rectangle2D.Double getProjectedBounds(Bounds b) {
		EastNorth minEN = proj.latlon2eastNorth(b.getMin());
		EastNorth maxEN = proj.latlon2eastNorth(b.getMax());
		return new Rectangle2D.Double(minEN.east(), minEN.north(), maxEN.east()-minEN.east(), maxEN.north()-minEN.north());
	}

    private EastNorth getDefaultCenter() {
    	return pBounds.getCenter();
    }

}
