package meeds.simsim.sys;


import static simsim.core.Simulation.rg;

import meeds.sys.proxying.ProxyDiscoveryService;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.HSB;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Rectangle;
import simsim.gui.geom.XY;
import feeds.simsim.sys.SS_sNode;
import feeds.sys.core.ID;

abstract public class SS_ProxyNode extends SS_FixedNode {
	
	protected SS_ProxyNode( SS_sNode server) {
		super( server, new SS_ProxyNodeContext(new ID( server.id.longValue() + (SS_FixedNode.db.size() + 1))));
	}

	
	final Pen paint0 = new Pen( RGB.green.darker().darker(), 1 ) ;
	final Pen paint1 = new Pen( RGB.gray.darker(), 2.0) ;	
//	Pen pen = new Pen( new HSB(rg.nextFloat(), 0.5, 0.6, 0.35), 0.5);
//	public void displayOn( Canvas canvas ) {
//		final double radius = 0.3*2*ProxyDiscoveryService.RADIUS / canvas.geoScale();
//
//		XY p = canvas.geo2point( address.pos ) ;
//		canvas.sFill( new Pen( RGB.green.darker(), 1 ), new Rectangle( p, 12.0, 12.0) ) ;
//		canvas.sFill( pen, new Circle( p, radius ) ) ;	
//	}

	public void displayOn( Canvas canvas ) {
		
		double radius = 0.3*2*ProxyDiscoveryService.RADIUS / canvas.geoScale();

		XY p = canvas.geo2point(address.pos);
		
		paint0.useOn( canvas.gs ) ;
		canvas.sFill( new Circle( p, 32.0 / canvas.geoScale() ) ) ;	
		paint1.useOn( canvas.gs ) ;
		canvas.sDraw( new Circle( p, radius ) ) ;	
	}
	
}


