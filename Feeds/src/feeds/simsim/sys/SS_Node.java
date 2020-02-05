package feeds.simsim.sys;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import simsim.core.EndPoint;
import simsim.core.Globals;
import simsim.core.TcpChannel;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.XY;
import feeds.simsim.Node;
import feeds.sys.FeedsRegistry;
import feeds.sys.catadupa.Catadupa;
import feeds.sys.core.Dispatcher;
import feeds.sys.core.ID;
import feeds.sys.core.NodeContext;
import feeds.sys.packets.cPacket;

abstract public class SS_Node extends Node implements SS_MessageHandler {

	protected static GeoPosition mapCenter = Globals.get("MapCenter", new GeoPosition(38.728711, -9.149981));

	public static SS_NodeDB<SS_Node> db = new SS_NodeDB<SS_Node>();

	public ID id;
	public NodeContext context;
	Dispatcher dispatcher;

	public SS_Node(NodeContext ctx) {
		id = ctx.id();
		context = ctx;
		db.store(this);
	}

	public String url() {
		return "ss://-/" + id + "/";
	}

	@Override
	public void init() {
		context.init();
		dispatcher = context.plm.dispatcher();
		address.pos = new XY(mapCenter.getLongitude() + (address.pos.x-200) / 30000, mapCenter.getLatitude() + (address.pos.y-200) / 30000);
	}

	abstract public void initNode();

	@Override
	public void onReceive(EndPoint src, SS_cPacket p) {
		context.makeCurrent();
		try {
			dispatcher.dispatch(cPacket.decode(p.packet));
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	@Override
	public void onReceive(TcpChannel ch, SS_cPacket p) {
		context.makeCurrent();
		try {
			dispatcher.dispatch(cPacket.decode(p.packet));
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return id.toString();
	}

	final Pen pen0 = new Pen(RGB.black, 0);

	@Override
	public void displayOn(Canvas canvas) {

		XY p = canvas.geo2point(address.pos);
		pen0.useColorOn(canvas.gs);
		canvas.sFont(12);
		canvas.sDraw(address.toString().substring(11), p.getX(), p.getY() - 20);

		 if( cat == null ) {
			cat = FeedsRegistry.get("Catadupa") ;
		 } else {
			 
		 canvas.gs.drawString( ""+cat.db.k2n.size(), p.fX(), p.fY() + 20);
		 }
	}

	Catadupa cat;
}