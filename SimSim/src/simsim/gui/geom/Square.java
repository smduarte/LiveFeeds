package simsim.gui.geom;


public class Square extends Rectangle {

	public Square( XY xy,  double w ) {
		super( xy, w, w ) ;
	}
		
	public Square( double x, double y, double w) {
		super( x, y, w, w ) ;
	}
	
	public XY pos() {
		return new XY(x, y) ;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
