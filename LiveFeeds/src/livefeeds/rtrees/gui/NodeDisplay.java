package livefeeds.rtrees.gui;

import static livefeeds.rtrees.config.Config.Config;
import livefeeds.rtrees.CatadupaNode;
import livefeeds.rtrees.View;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.Pen;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.Circle;
import simsim.gui.geom.Line;
import simsim.gui.geom.XY;

public class NodeDisplay implements Displayable {
	
	private static final double RADIUS = 400.0;

	XY pos;
	CatadupaNode owner;

	public NodeDisplay(CatadupaNode owner) {
		this.owner = owner;
		owner.state.pos = pos();
	}

	private XY pos() {
		double R = RADIUS;
		double a = owner.key * 2 * Math.PI / (1L << Config.NODE_KEY_LENGTH) - Math.PI;
		return pos = new XY(500 + R * Math.sin(a), 500 + R * Math.cos(a));
	}

	final Pen pen0 = new Pen(RGB.BLACK, 3);
	final Pen pen1 = new Pen(RGB.GREEN, 2);
	final Pen pen2 = new Pen(RGB.RED, 2);
	final Pen pen3 = new Pen(RGB.BLUE, 2);

	public void displayOn(Canvas canvas) {

		if (owner.state.joined) {
			int holes = owner.state.db.view.holes( View.GV);
			pen0.useOn(canvas.gs);
			double t = -20 * holes / RADIUS;
			canvas.sDraw(new Line(pos.x, pos.y, pos.x + t * (pos.x - 500), pos.y + t * (pos.y - 500)));
		}

		{
			double runningTime = owner.upTime();
			pen1.useOn(canvas.gs);
			double t = (runningTime / 360) / RADIUS;
			canvas.sDraw(new Line(pos.x, pos.y, pos.x + t * (pos.x - 500), pos.y + t * (pos.y - 500)));
		}

		{
			Line l = new Line(pos, new XY(500, 500));
			for (int i = 0; i < Integer.bitCount( owner.state.db.neededFilterPieces) ; i++) {
				XY p = l.extrapolate(1 + 0.05 * i);
				canvas.sFill(pen2, new Circle(p, 5));
			}
		}
	}

}