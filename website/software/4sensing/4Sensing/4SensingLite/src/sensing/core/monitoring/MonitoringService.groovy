package sensing.core.monitoring
import sensing.core.Service;
import sensing.core.ServiceManager;
import sensing.core.network.Peer;
import sensing.core.query.Query;
import sensing.core.sensors.monitoring.WorkloadReading;
import java.awt.geom.Rectangle2D;


class MonitoringService extends Service {
	
	static Query wlQuery = null;
	static List workLoad;
	
	static Rectangle2D world;
	
	static int gridLen;
	static double gridCellWidth;
	static double gridCellHeight;
	
	
	public MonitoringService(ServiceManager services) {
		super(services)
		gridLen = services.config.monitoring.gridLen;
		if(services.config.monitoring?.workLoad) {
			workLoad = [];
			gridLen.times{
				List line = [];
				gridLen.times{line << 0};
				workLoad << line;
			}
			world = services.config.world;
			gridCellWidth = world.width/gridLen;
			gridCellHeight = world.height/gridLen;
		}
	}
	
	public static Map getGridCell(double x, double y) {
		int cellX = (int)((x-world.x)/gridCellWidth);
		int cellY = (int)((y-world.y)/gridCellHeight);

		return [boundingBox: getGridCellBounds(cellX, cellY), cellId: "${cellX}_${cellY}"]
	}
	
	public static Rectangle2D getGridCellBounds(int x, int y) {
		return new Rectangle2D.Double(x*gridCellWidth+world.x, y*gridCellHeight+world.y, gridCellWidth, gridCellHeight);
	}

	public static double getMinTotalWorkLoad() {
		return workLoad*.min().min();
	}
	
	public static double getMaxTotalWorkLoad() {
		return workLoad*.max().max();
	}
	
	
	public void start() {
		if(services.config.monitoring?.workLoad && !wlQuery) {
			wlQuery = new Query("sensing.persistence.core.monitoring.VTworkLoad", services.config.world);
			Peer root = services.network.randomPeer();
			services.scheduler.scheduleOnce(5) {
				println "MONITORING QUERY ${root.id} [${wlQuery.id}] ${wlQuery.aoi}"
				root.services.query.runQuery(wlQuery) { WorkloadAggregate w ->
					List xy = w.cellId.split("_");
					//println "MONITORING QUERY got $w: ${xy[0]}:${xy[1]}"
					int x = Integer.parseInt(xy[0]);
					int y = Integer.parseInt(xy[1]);
					workLoad[x][y] = w.avgTotalLoad;
				}
			}
		}
	}
}
