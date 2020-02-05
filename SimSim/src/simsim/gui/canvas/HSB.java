package simsim.gui.canvas;

import java.awt.Color;


public class HSB extends RGB {


	public HSB( double h, double s, double b ) {
		super( rgba( h, s, b, 1 ) ) ;
	}
	
	public HSB( double h, double s, double b, double a ) {
		super( rgba( h, s, b, a ) ) ;
	}
	
	static float[] rgba( double h, double s, double b, double a ) {
		float[] res = new float[4] ;
		Color.getHSBColor((float)h, (float)s, (float)b).getRGBComponents(res) ;
		res[3] = (float)a ;
		return res ;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
