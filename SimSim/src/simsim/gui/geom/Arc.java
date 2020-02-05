package simsim.gui.geom;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Arc2D.Double;

public class Arc extends Double implements Shape {


	public static enum ArcType {
		OPEN(Arc2D.OPEN), CHORD(Arc2D.CHORD), PIE(Arc2D.PIE);
		
		private int awtCode;

		ArcType(int v) {
			awtCode = v;
		}

		public int awtCode() {
			return awtCode;
		}
	};


	public Arc( XY xy, XY wh, double start, double extent, ArcType type) {
		super(xy.x, xy.y, wh.x, wh.y, start, extent, type.awtCode() );
	}

	public Arc( XY xy, double w, double h, double start, double extent, ArcType type) {
		super(xy.x, xy.y, w, h, start, extent, type.awtCode() );
	}
	
	public Arc( double x, double y, double w, double h, double start, double extent, ArcType type) {
		super(x, y, w, h, start, extent, type.awtCode() );
	}

	public Arc( Point2D xy, double w, double h, double start, double extent, ArcType type) {
		super(xy.getX(), xy.getY(), w, h, start, extent, type.awtCode() );
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
