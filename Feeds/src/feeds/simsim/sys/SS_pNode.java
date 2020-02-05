package feeds.simsim.sys;

import static simsim.core.Simulation.Spanner;
import static simsim.core.Simulation.rg;

import java.util.ArrayList;

import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import feeds.sys.core.ID;
import feeds.sys.tasks.Task;

abstract public class SS_pNode extends SS_Node {

	protected SS_pNode() {
		super(new SS_pNodeContext(new ID(((db.size() + 1) << 55))));
		setColor(new RGB(0.6, 0.5, 0.5));
		Spanner.add(address);
	}

	@Override
	public void init() {
		super.init();

		new Task(1.0 + 3 * rg.nextDouble()) {
			@Override
			public void run() {
				context.makeCurrent();
				initNode();
			}
		};
	}

	static double meanLatency = -1;

	public static double meanLatency() {
		if (meanLatency < 0) {

			ArrayList<SS_Node> nodes = new ArrayList<SS_Node>();
			for (SS_Node i : db.nodes)
				if (i instanceof SS_pNode)
					nodes.add(i);

			double n = 0;
			meanLatency = 0;
			for (int i = 0; i < nodes.size(); i++)
				for (int j = 0; j < i; j++) {
					n++;
					meanLatency += db.nodes.get(i).address.latency(db.nodes.get(j).address);
				}
			if (n > 0)
				meanLatency /= n;
			else
				meanLatency = 0.5;
		}
		return meanLatency;
	}

	@Override
	public void displayOn(Canvas canvas) {
		// canvas.sFill( super.getColor(), new Circle( canvas.geo2point(
		// address.pos), 25.0 ) ) ;
	}
}