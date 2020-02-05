package meeds.simsim.sys;

import meeds.sys.proxying.ProxyDiscoveryService;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.XY;
import feeds.simsim.sys.SS_sNode;
import feeds.sys.core.ID;

abstract public class SS_HomebaseNode extends SS_FixedNode {
	
	protected SS_HomebaseNode(SS_sNode server) {
		super( server, new SS_hNodeContext(new ID( server.id.longValue() + ( SS_FixedNode.db.size() + 1))));
		
		feeds.api.Feeds.err.println("I AM HOMEBASE...") ;
	}
			
	final Pen paint0 = new Pen( RGB.red.darker(), 1 ) ;
	final Pen paint1 = new Pen( RGB.gray.darker(), 2.0, 4.0 ) ;	
	public void displayOn( Canvas canvas ) {
		
		double radius = 0.3*2*ProxyDiscoveryService.RADIUS / canvas.geoScale();

		XY p = canvas.geo2point(address.pos);
		
		paint0.useOn( canvas.gs ) ;
		canvas.sFill( new Circle( p, 24.0 / canvas.geoScale() ) ) ;	
		paint1.useOn( canvas.gs ) ;
		canvas.sDraw( new Circle( p, radius ) ) ;	
	}
}