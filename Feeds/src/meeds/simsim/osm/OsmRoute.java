package meeds.simsim.osm;

import static simsim.core.Simulation.rg;

import java.awt.geom.Path2D;
import java.util.List;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import simsim.core.Displayable;
import simsim.core.Simulation;
import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.HSB;
import simsim.gui.canvas.Pen;
import simsim.gui.geom.Line;
import simsim.gui.geom.XY;

public class OsmRoute implements Displayable {

	private final static double SPEED = 0.5 * 25000.0 / 3600;

	OsmRouteGenerator owner;
	double T0, duration;
	List<GeoPos> path;

	GeoPos lastPos = null;

	public OsmRoute(OsmRouteGenerator owner) {
		this.owner = owner;
		init();
	}

	public void init() {
		do {
			owner.updateRoute(this);
		} while (path == null);
		
		duration = length() / SPEED;
		T0 = Simulation.currentTime();
	}


	public GeoPos currenPos() {
		GeoPos p;
		double elapsed = (Simulation.currentTime() - T0) / duration;
		if (elapsed >= 1.0) {

			init();
			p = interpolate(0);
		} else
			p = interpolate(elapsed);

		lastPos = p;
		return lastPos;
	}

	GeoPos endPosition() {
		return path.get(path.size() - 1);
	}

	GeoPos interpolate(double T) {
		double t = (path.size() - 1) * T;
		int i = (int) t;
		double k = t - i;
		return T < 1 ? GeoPos.interpolate(k, path.get(i), path.get(i + 1)) : path.get(i);
	}

	double length() {
		double length = 0;
		for (int i = 0; i < path.size() - 1; i++)
			length += distance(path.get(i), path.get(i + 1));
		return length;
	}

	static public float distance(GeoPos a, GeoPos b) {
		final double R = 6371 * 1000;
		double x = (lon(b) - lon(a)) * Math.cos(0.5 * (lat(a) + lat(b)));
		double y = (lat(b) - lat(a));
		return (int) (Math.sqrt(x * x + y * y) * R + 0.5);
	}

	static private double lat(GeoPosition p) {
		return p.getLatitude() * Math.PI / 180.0;
	}

	static private double lon(GeoPosition p) {
		return p.getLongitude() * Math.PI / 180.0;
	}

	Pen pen = new Pen(new HSB(rg.nextFloat(), 0.5, 0.8, 0.85), 4.0);
	Pen pen2 = new Pen(pen.color, 1.0);

	@Override
	public void displayOn(Canvas canvas) {
	}
}
