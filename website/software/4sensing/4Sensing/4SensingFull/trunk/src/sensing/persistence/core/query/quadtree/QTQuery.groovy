package sensing.persistence.core.query.quadtree;

import sensing.persistence.core.query.*;
import static sensing.persistence.core.query.quadtree.QTConstants.*;

// TODO: layer violation - rectangle implementation should be independent from simulator
import simsim.gui.geom.Rectangle;
import java.awt.geom.*;


public class QTQuery extends Query{
	Rectangle searchArea; // search area
	Rectangle quadrant;
	List ancestors = [];
	boolean asLeaf;

	public QTQuery(Query q, Rectangle world, UUID rootId) {
		super(q, rootId);
		this.quadrant = world;
		asLeaf = false;
		// calc search area
		double minQLenW = world.width / Math.pow(2, MIN_QUAD_DIV);
		double minQLenH = world.height / Math.pow(2, MIN_QUAD_DIV);
		double x1 = world.x + ((int)((q.aoi.x-world.x)/minQLenW)) * minQLenW;
		double y1 = world.y + ((int)((q.aoi.y-world.y)/minQLenH)) * minQLenH;
		double x2 = world.x + Math.ceil((q.aoi.maxX-world.x)/minQLenW) * minQLenW;
		double y2 = world.y + Math.ceil((q.aoi.maxY-world.y)/minQLenH) * minQLenH;
//		println "x1: ${((int)((q.aoi.x-world.x)/minQLenW))} x2: ${Math.ceil((q.aoi.maxX-world.x)/minQLenW)} y1: ${((int)((q.aoi.y-world.y)/minQLenH))} y2: ${Math.ceil((q.aoi.maxY-world.y)/minQLenH)}";
//		println "minQlenW: ${minQLenW} minQlenH: ${minQLenH}";
//		println "x1: ${x1} x2: ${x2} y1: ${y1} y2: ${y2}";
		searchArea = new Rectangle(new Point2D.Double(x1,y1), new Point2D.Double(x2-x1, y2-y1));
	}

	public QTQuery(QTQuery q, Rectangle quadrant, boolean asLeaf) {
		super(q);
		this.searchArea = q.searchArea;
		this.ancestors = q.ancestors.clone();
		this.quadrant = quadrant;
		this.asLeaf = asLeaf;
	}
	
	public QTQuery(QTQuery q, Rectangle quadrant) {
		this(q, quadrant, false);
	}

	
}
