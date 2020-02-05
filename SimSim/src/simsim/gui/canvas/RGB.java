package simsim.gui.canvas;

import java.awt.Color;


public class RGB extends java.awt.Color {

	public static final RGB RED = new RGB( Color.red ) ;
	public static final RGB BLUE = new RGB( Color.blue ) ;
	public static final RGB GREEN = new RGB( Color.green ) ;
	public static final RGB BLACK = new RGB( Color.black ) ;
	public static final RGB CYAN = new RGB( Color.cyan ) ;
	public static final RGB PINK = new RGB( Color.pink ) ;
	public static final RGB ORANGE = new RGB( Color.orange ) ;	
	public static final RGB YELLOW = new RGB( Color.yellow ) ;
	public static final RGB MAGENTA = new RGB( Color.magenta ) ;

	public static final RGB GRAY = new RGB( Color.gray ) ;
	public static final RGB WHITE = new RGB( Color.white ) ;
	public static final RGB DARK_GRAY = new RGB( Color.darkGray ) ;
	public static final RGB LIGHT_GRAY = new RGB( Color.lightGray ) ;

	public RGB( int rgb ) {
		super( rgb ) ;
	}
		
	public RGB( Color c ) {
		super( c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() ) ;
	}
	
	public RGB( Color c, double alpha ) {
		super( c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha*255) ) ;
	}

	public RGB( double r, double g, double b ) {
		this( r, g, b, 1) ;
	}
	
	public RGB( double r, double g, double b, double a ) {
		super( (float)r, (float)g, (float)b, (float)a) ;
	}

	protected RGB( float[] rgba ) {
		super( rgba[0], rgba[1], rgba[2], rgba[3] ) ;
	}
	
	
	public RGB brighter() {
		return new RGB( super.brighter() ) ;
	}
	
	public RGB darker() {
		return new RGB( super.darker() ) ;
	}

	public RGB scale(double f) {
		double r = f * getRed() / 255.0, g = f * getGreen() / 255.0, b = f * getBlue() / 255.0 ;
		return new RGB(  Math.min(1, r), Math.min(1, g), Math.min(1, b)) ;
	}

//	public RGB paler() {
//		float[] x = Color.RGBtoHSB( getRed(), getGreen(), getBlue(), null);
//		return new HSB( x[0], 0.5, 0.9 );
//	}
//	
//	public RGB darker() {
//		float[] x = Color.RGBtoHSB( getRed(), getGreen(), getBlue(), null);
//		return new HSB( x[0], 0.5, 0.75 );
//	}
	
	private static final long serialVersionUID = 1L;
}
