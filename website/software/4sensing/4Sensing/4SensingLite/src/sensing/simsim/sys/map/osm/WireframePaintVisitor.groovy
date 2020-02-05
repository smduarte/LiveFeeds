package sensing.simsim.sys.map.osm

;

/* Based on JOSM
 * License: GPL. Copyright 2007 by Immanuel Scholz and others */

/* To enable debugging or profiling remove the double / signs */

import static org.openstreetmap.josm.tools.I18n.marktr;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.NavigatableComponent;
import org.openstreetmap.josm.data.osm.visitor.*;

import sensing.simsim.sys.map.MapRenderer;
import sensing.simsim.sys.map.MapView;
import simsim.gui.canvas.*;

/**
 * A visitor that paints a simple scheme of every primitive it visits to a
 * previous set graphic environment.
 *
 * @author imi
 */
public class WireframePaintVisitor extends AbstractVisitor implements MapRenderer {

    public final static Color darkerblue = new Color(0,0,96);
    public final static Color darkblue = new Color(0,0,128);
    public final static Color darkgreen = new Color(0,128,0);
    public final static Color teal = new Color(0,128,128);
    public final static Color lightteal= new Color(0, 255, 186);

    /**
     * The environment to paint to.
     */
    Canvas canvas;
    MapView mapView;

    public boolean inactive;

    protected static final double PHI = Math.toRadians(20);

    /**
     * Preferences
     */
    protected Color inactiveColor;
    protected Color selectedColor;
    protected Color nodeColor;
    protected Color relationColor;
    protected Color untaggedWayColor;
    protected Color incompleteColor;
    protected Color backgroundColor;
    protected Color highlightColor;
    protected boolean showDirectionArrow;
    protected boolean showRelevantDirectionsOnly;
    protected boolean showHeadArrowOnly;
    protected boolean showOrderNumber;
    protected boolean fillSelectedNode;
    protected boolean fillUnselectedNode;
    protected int selectedNodeRadius;
    protected int unselectedNodeRadius;
    protected int selectedNodeSize;
    protected int unselectedNodeSize;
    protected int defaultSegmentWidth;
    protected int virtualNodeSize;
    protected int virtualNodeSpace;
    protected int segmentNumberSpace;
    protected int taggedNodeRadius;
    protected int taggedNodeSize;

    protected Pen primaryWayPen;
    protected Pen secondaryWayPen;
    protected Pen tertiaryWayPen;
    protected Pen residentialWayPen;
    protected Pen motorwayWayPen;
    protected Pen trunkWayPen;
    protected Pen dfltWayPen;
    

    /**
     * Draw subsequent segments of same color as one Path
     */
    protected Pen currentPen;
    protected GeneralPath currentPath = new GeneralPath();

    Rectangle bbox = new Rectangle();
	
	void setMapView(MapView mapView) {
		this.mapView = mapView;
	}
	
	public void setViewPort(Rectangle2D viewPort){
		//TODO
	}

    public void getColors()
    {
        inactiveColor = Color.darkGray;
        selectedColor = Color.red;
        nodeColor = Color.yellow;
//        dfltWayColor = darkblue;
        relationColor = teal;
        untaggedWayColor = darkgreen;
        incompleteColor = darkerblue;
        backgroundColor = Color.BLACK;
        highlightColor = lightteal;

        dfltWayPen=  new Pen(Color.BLACK, 1);
        motorwayWayPen = new Pen(new Color(128,155,192), 2);
        //trunkWayPen = new Pen(new Color(127,201,127), 2);
        trunkWayPen = motorwayWayPen;
        primaryWayPen = new Pen(new Color(251,128,95), 2);
        //secondaryWayPen = new Pen(new Color(253,191,111), 2);
        secondaryWayPen = primaryWayPen;
        //tertiaryWayPen = new Pen(new Color(247,244,150), 2);
        //residentialWayPen = new Pen(new Color(240,240,240), 2);
        tertiaryWayPen = new Pen(new Color(200,200,200), 1);
        residentialWayPen = tertiaryWayPen;

        currentPen = new Pen(Color.BLACK, 1);
    }

    protected void getSettings(boolean virtual) {
        showDirectionArrow = true;
        showRelevantDirectionsOnly = true;
        showHeadArrowOnly = false;
        showOrderNumber = false;
        selectedNodeRadius = 2.5;
        selectedNodeSize = selectedNodeRadius * 2;
        unselectedNodeRadius = 1.5;
        unselectedNodeSize = unselectedNodeRadius * 2;
        taggedNodeRadius = 2.5;
        taggedNodeSize = taggedNodeRadius * 2;
        defaultSegmentWidth = 2
        fillSelectedNode = true;
        fillUnselectedNode = false;
        virtualNodeSize = virtual ? 4: 0;
        virtualNodeSpace = 70;
        segmentNumberSpace = 40;
        getColors();

        canvas.gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        canvas.gu.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    }
	
	public void display() {
		visitAll(false);	
	}

    public void visitAll(boolean virtual) {
		assert mapView;
		assert mapView.mapModel;
		assert mapView.mapModel.dataSet;
		
    	DataSet data = mapView.mapModel.dataSet;
        getSettings(virtual);

        for (final OsmPrimitive osm:data.getRoads()){
            if (!osm.isDeleted() && !mapView.mapModel.dataSet.isSelected(osm) && !osm.isFiltered() && osm.isTagged()) {
                osm.visit(this);
            }
        }
        displaySegments();


    }



    public static boolean isLargeSegment(Point p1, Point p2, int space)
    {
        int xd = p1.x-p2.x; if(xd < 0) {
            xd = -xd;
        }
        int yd = p1.y-p2.y; if(yd < 0) {
            yd = -yd;
        }
        return (xd+yd > space);
    }

    public void visitVirtual(Way w) {
        Iterator<Node> it = w.getNodes().iterator();
        if (it.hasNext()) {
            Point lastP = mapView.getPoint(it.next());
            while(it.hasNext())
            {
                Point p = mapView.getPoint(it.next());
//                if(isSegmentVisible(lastP, p) && isLargeSegment(lastP, p, virtualNodeSpace))
//                {
                    double x = (p.x+lastP.x)/2;
                    double y = (p.y+lastP.y)/2;
                    currentPath.moveTo(x-virtualNodeSize, y);
                    currentPath.lineTo(x+virtualNodeSize, y);
                    currentPath.moveTo(x, y-virtualNodeSize);
                    currentPath.lineTo(x, y+virtualNodeSize);
//                }
                lastP = p;
            }
        }
    }

    public void visit(Node n) {};

    public void visit(Relation r){};
    /**
     * Draw a darkblue line for all segments.
     * @param w The way to draw.
     */
    public void visit(Way w) {
        if (w.incomplete || w.getNodesCount() < 2)
            return;
       // if(!w.keys['highway']) return;

        Pen wayPen;

        switch(w.keys['highway']) {
        case {it.startsWith('motorway')} : wayPen = motorwayWayPen; break;
        case {it.startsWith('trunk')}	 : wayPen = trunkWayPen; break;
        case 'primary'					: wayPen = primaryWayPen; break;
        case 'secondary' 				: wayPen = secondaryWayPen; break;
        case 'tertiary' 				: wayPen = tertiaryWayPen; break;
        case 'residential' 				: wayPen = residentialWayPen; break;
        default							: wayPen = dfltWayPen;
        }


        Iterator<Node> it = w.getNodes().iterator();
        if (it.hasNext()) {
            Point lastP = mapView.getPoint(it.next());
            for (int orderNumber = 1; it.hasNext(); orderNumber++) {
                Point p = mapView.getPoint(it.next());
//                drawSegment(lastP, p, wayPen,
//                        showOnlyHeadArrowOnly ? !it.hasNext() : showThisDirectionArrow);
              drawSegment(lastP, p, wayPen,false);
                if (showOrderNumber) {
                    drawOrderNumber(lastP, p, orderNumber);
                }
                lastP = p;
            }
        }
    }

    private drawRoute(route) {
    	route.each {segment -> 
    		def(startNode, endNode, way) = segment;
    		def nodes = way.nodes;
    		nodes = nodes[nodes.indexOf(startNode)..nodes.indexOf(endNode)];
    		Point lastP = mapView.getPoint(nodes.head());
    		nodes.tail().each{ n ->
    			Point p = mapView.getPoint(n);
    			drawSegment(lastP, p, selectedColor, true);
    			lastP = p;
        	}
    	}	
    }
    



    /**
     * Draw a line with the given color.
     */
    protected void drawSegment(Point p1, Point p2, Pen pen, boolean showDirection) {
        if (pen != currentPen) {
            displaySegments(pen);
        }

        //if (isSegmentVisible(p1, p2)) {
            currentPath.moveTo(p1.x, p1.y);
            currentPath.lineTo(p2.x, p2.y);

//            if (showDirection) {
//                double t = Math.atan2(p2.y-p1.y, p2.x-p1.x) + Math.PI;
//                currentPath.lineTo((int)(p2.x + 10*Math.cos(t-PHI)), (int)(p2.y + 10*Math.sin(t-PHI)));
//                currentPath.moveTo((int)(p2.x + 10*Math.cos(t+PHI)), (int)(p2.y + 10*Math.sin(t+PHI)));
//                currentPath.lineTo(p2.x, p2.y);
//            }
       // }
    }

    protected boolean isSegmentVisible(Point p1, Point p2) {
        if ((p1.x < 0) && (p2.x < 0)) return false;
        if ((p1.y < 0) && (p2.y < 0)) return false;
        if ((p1.x > mapView.getWidth()) && (p2.x > mapView.getWidth())) return false;
        if ((p1.y > mapView.getHeight()) && (p2.y > mapView.getHeight())) return false;
        return true;
    }

    protected boolean isPolygonVisible(Polygon polygon) {
        Rectangle bounds = polygon.getBounds();
        if (bounds.width == 0 && bounds.height == 0) return false;
        if (bounds.x > mapView.getWidth()) return false;
        if (bounds.y > mapView.getHeight()) return false;
        if (bounds.x + bounds.width < 0) return false;
        if (bounds.y + bounds.height < 0) return false;
        return true;
    }



    protected void displaySegments() {
        displaySegments(null);
    }
    protected void displaySegments(Pen newPen) {
        if (currentPath != null) {
            canvas.sDraw(currentPen, currentPath);
            currentPath = new GeneralPath();
            currentPen = newPen;
        }
    }
}
