package meeds.simsim.sys;

import static simsim.core.Simulation.rg;

import java.awt.geom.Point2D;

import meeds.simsim.HomebaseNode;
import meeds.simsim.MeedsSimulator;
import meeds.simsim.osm.GeoPos;
import meeds.simsim.osm.OsmRoute;
import meeds.sys.homing.Position;
import meeds.sys.homing.containers.Homebase;
import meeds.sys.homing.containers.Location;
import meeds.sys.proxying.containers.Proxy;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Line;
import simsim.gui.geom.XY;
import feeds.simsim.sys.SS_Node;
import feeds.sys.core.Container;
import feeds.sys.core.ContainerListener;
import feeds.sys.core.ID;
import feeds.sys.core.NodeType;
import feeds.sys.registry.NodeRegistry;
import feeds.sys.tasks.PeriodicTask;
import feeds.sys.tasks.Task;

import static simsim.core.Simulation.Gui;
abstract public class SS_MobileNode extends SS_Node {

	protected SS_Node homebase;

	OsmRoute route;

	protected SS_MobileNode() {
		super(new SS_MobileNodeContext(new ID()));

		homebase = HomebaseNode.db.nodes.randomElement();

		super.setColor(RGB.BLACK);
	}

	@Override
	public void init() {
		super.init();

		new Task(5000.0 + 3 * rg.nextDouble()) {
			@Override
			public void run() {
				context.makeCurrent();
				initNode();
				route = MeedsSimulator.osm.getRoute();

				new PeriodicTask(1) {
					@Override
					public void run() {
						GeoPos pos = route.currenPos();
						address.pos = new XY(pos.getLongitude(), pos.getLatitude());
					}
				};

				final Location.Updater locu = Container.byClass(Location.class);

				new PeriodicTask(30) {
					@Override
					public void run() {
						locu.set(new Position(address.pos.x, address.pos.y));
					}
				};

				Container.monitor(Location.class, new ContainerListener<Location>() {
					@Override
					public void handleContainerUpdate(Location c) {
						sampledPos = new XY(c.pos().xy.x, c.pos().xy.y);
					}
				});

				Container.monitor(Homebase.class, new ContainerListener<Homebase>() {
					@Override
					public void handleContainerUpdate(Homebase hb) {
						try {
							HB_Node = SS_Node.db.get(hb.sortedTransports().get(0).dst());
						} catch (Exception x) {
							HB_Node = null;
						}
					}
				});
				Container.monitor(Proxy.class, new ContainerListener<Proxy>() {
					@Override
					public void handleContainerUpdate(Proxy p) {
						try {
							PX_Node = SS_Node.db.get(p.closestProxy().dst());
						} catch (Exception x) {
							PX_Node = null;
						}
					}
				});

			}
		};

	}

	@Override
	public void displayOn(Canvas canvas) {
		if (route == null)
			return;

		if(index == 0 && canvas.map == MeedsSimulator.carMap.map ) {
				MeedsSimulator.carMap.map.setCenterPosition( new GeoPos( address.pos.y, address.pos.x)) ;				
		}
		
		if (HB_Node != null && false) {
			canvas.sDraw(new Pen(new RGB(1, 0, 0, 0.4), 4, 10), new Line(canvas.geo2point(address.pos), canvas.geo2point(HB_Node.address.pos)));
		}

		if (PX_Node != null) {
			canvas.sDraw(new Pen(new RGB(0.2, 0.0, 0.2, 0.2), 8), new Line(canvas.geo2point(address.pos), canvas.geo2point(PX_Node.address.pos)));
		}

		route.displayOn(canvas);
		canvas.sFill(RGB.BLUE, new Circle(canvas.geo2point(address.pos), 10.0));
		if( sampledPos != null) {
			canvas.sDraw(new Pen(RGB.gray,2,2), new Circle(canvas.geo2point(sampledPos), 10.0));
		}
	}

	private SS_Node HB_Node, PX_Node;
	private XY sampledPos = new XY(0, 0);
}

class SS_MobileNodeContext extends SS_MobilityContext {

	public SS_MobileNodeContext(ID v) {
		super(v, NodeType.mNODE);
		reg = new NodeRegistry().init();
		isMnode = true;
		isCnode = false;
	}
}
