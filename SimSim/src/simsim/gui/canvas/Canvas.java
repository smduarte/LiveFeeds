package simsim.gui.canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import simsim.gui.geom.XY;

public class Canvas {
	public Graphics2D gu, gs;
	public JXMapViewer map;

	public Canvas(Graphics2D gu, Graphics2D gs) {
		this.gu = gu;
		this.gs = gs;
	}

	public XY uMouse() {
		return uMpos;
	}

	public XY sMouse() {
		return sMpos;
	}

	public void uDraw(Shape s) {
		gu.draw(s);
	}

	public void uDraw(Pen p, Shape s) {
		p.useOn(gu);
		gu.draw(s);
	}

	public void uDraw(RGB c, Shape s) {
		gu.setColor(c);
		gu.draw(s);
	}

	public void uDraw(Color c, Shape s) {
		gu.setColor(c);
		gu.draw(s);
	}

	public void uFill(Shape s) {
		gu.fill(s);
	}

	public void uFill(Pen p, Shape s) {
		p.useOn(gu);
		gu.fill(s);
	}

	public void uFill(RGB c, Shape s) {
		gu.setColor(c);
		gu.fill(s);
	}

	public void uFill(Color c, Shape s) {
		gu.setColor(c);
		gu.fill(s);
	}

	public void sDraw(Shape s) {
		gs.draw(s);
	}

	public void sDraw(Pen p, Shape s) {
		p.useOn(gs);
		gs.draw(s);
	}

	public void sDraw(RGB c, Shape s) {
		gs.setColor(c);
		gs.draw(s);
	}

	public void sFill(Shape s) {
		gs.fill(s);
	}

	public void sFill(Pen p, Shape s) {
		p.useOn(gs);
		gs.fill(s);
	}

	public void sFill(RGB c, Shape s) {
		gs.setColor(c);
		gs.fill(s);
	}

	public Canvas sFont(double size) {
		Font x = sFonts.get(size);
		if (x == null) {
			x = gs.getFont().deriveFont((float) size);
			sFonts.put(size, x);
		}
		gs.setFont(x);
		return this;
	}

	public Font sFont() {
		return gs.getFont();
	}

	public Canvas uFont(double size) {
		Font x = uFonts.get(size);
		if (x == null) {
			x = gu.getFont().deriveFont((float) size);
			uFonts.put(size, x);
		}
		gu.setFont(x);
		return this;
	}

	public void uDraw(String s, XY xy) {
		gu.drawString(s, (float) xy.x, (float) xy.y);
	}

	public void uDraw(Pen p, String s, XY xy) {
		p.useOn(gu);
		gu.drawString(s, (float) xy.x, (float) xy.y);
	}

	public void uDraw(RGB c, String s, XY xy) {
		gu.setColor(c);
		gu.drawString(s, (float) xy.x, (float) xy.y);
	}

	public void uDraw(String s, double x, double y) {
		gu.drawString(s, (float) x, (float) y);
	}

	public void uDraw(Pen p, String s, double x, double y) {
		p.useOn(gu);
		gu.drawString(s, (float) x, (float) y);
	}

	public void uDraw(RGB c, String s, double x, double y) {
		gu.setColor(c);
		gu.drawString(s, (float) x, (float) y);
	}

	public void sDraw(String s, XY xy) {
		gs.drawString(s, (float) xy.x, (float) xy.y);
	}

	public void sDraw(Pen p, String s, XY xy) {
		p.useOn(gs);
		gs.drawString(s, (float) xy.x, (float) xy.y);
	}

	public void sDraw(RGB c, String s, XY xy) {
		gs.setColor(c);
		gs.drawString(s, (float) xy.x, (float) xy.y);
	}

	public void sDraw(String s, double x, double y) {
		gs.drawString(s, (float) x, (float) y);
	}

	public void sDraw(Pen p, String s, double x, double y) {
		p.useOn(gs);
		gs.drawString(s, (float) x, (float) y);
	}

	public void sDraw(RGB c, String s, double x, double y) {
		gs.setColor(c);
		gs.drawString(s, (float) x, (float) y);
	}

	public Canvas clone() {
		return new Canvas((Graphics2D) gu.create(), (Graphics2D) gs.create());
	}

	public void dispose() {
		gu.dispose();
		gs.dispose();
	}

	public void updateMousePosition(XY pu, XY ps) {
		uMpos = pu;
		sMpos = ps;
	}

	public XY geo2point(XY p) {
		Point2D pp = map.convertGeoPositionToPoint(new GeoPosition(p.getY(), p.getX()));
		return new XY(pp.getX(), pp.getY());
	}

	public XY geo2point(Point2D p) {
		Point2D pp = map.convertGeoPositionToPoint(new GeoPosition(p.getY(), p.getX()));
		return new XY(pp.getX(), pp.getY());
	}

	public XY geo2point(GeoPosition p) {
		Point2D pp = map.convertGeoPositionToPoint(p);
		return new XY(pp.getX(), pp.getY());
	}

	public double geoScale() {
		final double EarthRadius = 6378137;
		double latitude = map.getCenterPosition().getLatitude();
		int zoom = 20 - map.getZoom();
		latitude = Math.min(Math.max(latitude, -85), 85);
		return Math.cos(latitude * Math.PI / 180) * 2 * Math.PI * EarthRadius / (256 << zoom);
	}

	private XY uMpos = new XY(0, 0), sMpos = new XY(0, 0);
	private Map<Double, Font> uFonts = new HashMap<Double, Font>();
	private Map<Double, Font> sFonts = new HashMap<Double, Font>();
}
