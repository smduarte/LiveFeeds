package feeds.simsim.sys;

import static simsim.core.Simulation.Network;
import static simsim.core.Simulation.rg;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import feeds.simsim.sNode;
import feeds.sys.core.ID;
import feeds.sys.tasks.Task;

abstract public class SS_sNode extends SS_Node {

	protected SS_Node server;

	protected SS_sNode(SS_pNode server) {
		super(new SS_sNodeContext(new ID(server.id.longValue() + ((sNode.db.size() + 1) << 25))));
		this.server = server;

		double mpl = SS_pNode.meanLatency();
		while (address.latency(server.address) > 0.25 * mpl || address.latency(server.address) < 0.005) {
			address = Network.createAddress(this);
			endpoint = address.endpoint;
			mpl *= 1.01;
		}
		super.setColor(new RGB(0.7, 0.7, 0.7));
	}

	@Override
	public void init() {
		super.init();

		new Task(1000.0 + 500 * rg.nextDouble()) {
			@Override
			public void run() {
				context.makeCurrent();
				initNode();
			}
		};
	}

	static ID randomID() {
		start: for (;;) {
			ID res = new ID();
			for (SS_Node i : db.nodes)
				if (i.id.equals(res))
					continue start;
			return res;
		}
	}

	@Override
	public void displayOn(Canvas canvas) {
		// canvas.sFill( super.getColor(), new Circle( canvas.geo2point(
		// address.pos), 18.0 ) ) ;
	}
}