package sensing.persistence.core.query;

import java.util.UUID;
//TODO: layer violation - rectangle implementation should be independent from simulator
import simsim.gui.geom.Rectangle;
import java.awt.geom.*;
import sensing.persistence.core.pipeline.Tuple;

public class Query {
	enum QueryType {CONTINUOUS, SNAP_SHOT};
	
	final String id;
	UUID rootId;
	final String vtableName;
	Rectangle aoi; // area of interest
	final QueryType type;
	UUID srcId;
	int version;
	int level = 0;
	
	public Query(String vtableName, Rectangle aoi) {
		this.id = UUID.randomUUID().toString();
		this.vtableName = vtableName;
		this.aoi = aoi;
	}

	public Query(String vtableName) {
		this(vtableName, null);
	}
	
	public Query(Query q) {
		this(q, q.id, q.rootId);
	}
	
	public Query(Query q, UUID rootId) {
		this(q, q.id, rootId);
	}
	
	protected Query(Query q, String queryId, UUID rootId) {
		this.id = queryId;
		this.rootId = rootId;
		this.srcId = q.srcId;
		this.vtableName = q.vtableName;
		this.aoi = q.aoi;	
	}

	public area(args) {
		if(args.minX != null) {
			aoi = new Rectangle(new Point2D.Double(args.minX, args.minY), args.maxX-args.minX, args.maxY-args.minY);
		} else if(args.minLat != null){
			aoi = new Rectangle(new Point2D.Double(args.minLon, args.minLat), args.maxLon-args.minLon, args.maxLat-args.minLat);
		}
		return this;
	}

	public boolean eval(Tuple input) {
		if(input.boundingBox) {
			return aoi.contains(input.boundingBox);
		}
		if(input.lat && input.lon) {
			return aoi.contains(new Point2D.Double(input.lon, input.lat));
		}
	}

}
