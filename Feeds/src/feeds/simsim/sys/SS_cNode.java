package feeds.simsim.sys;

import simsim.gui.canvas.RGB;
import simsim.gui.canvas.Canvas;
import simsim.gui.geom.Rectangle;

import feeds.simsim.*;
import feeds.sys.core.*;
import feeds.sys.tasks.*;

import static simsim.core.Simulation.*;

abstract public class SS_cNode extends SS_Node {
	
	protected SS_Node server;

	protected SS_cNode(SS_sNode server) {
		super(new SS_cNodeContext(new ID(server.id.longValue() + (cNode.db.size() + 1))));
		this.server = server;

		double mpl = SS_pNode.meanLatency();
		while (address.latency(server.address) > 0.15 * mpl || address.latency(server.address) < 0.002 * mpl) {
			address = Network.createAddress(this);
			endpoint = address.endpoint;
			mpl *= 1.01 ;
		}

		super.setColor( new RGB(0.8, 0.8, 0.8) );
	}
	
	public void init() {
		super.init() ;
		
		new Task(2000.0 + 3 * rg.nextDouble() ) {
			public void run(){
				context.makeCurrent() ;
				initNode() ;
			}
		};
	}
		
	public void displayOn( Canvas canvas ) {
		canvas.sFill( super.getColor(), new Rectangle( canvas.geo2point(address.pos), 12.0, 12.0));
	}
}
