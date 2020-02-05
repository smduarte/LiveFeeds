package simsim.gui.canvas;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.Graphics2D;
import java.awt.BasicStroke;


public class Pen {
	
	public RGB color ;
	public Stroke stroke ;
	
	public Pen( Color c ) {
		this( c, 1.0) ;
	}	
	
	public Pen( Color c, Stroke s ) {
		this.color = new RGB(c) ;
		this.stroke = s ;		
	}
	
	public Pen( Color c, double strokeWidth ) {
		this.color = new RGB(c) ;
		this.stroke = new BasicStroke( (float) strokeWidth ) ;
	}
	
	public Pen( Color c, double strokeWidth, final double strokeInterval ) {
		this.color = new RGB(c) ;
		if( strokeInterval > 0 )
			this.stroke = new BasicStroke( (float) strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { (float)strokeInterval }, 0f) ;
		else
			this.stroke = new BasicStroke( (float) strokeWidth ) ;
	}
	
	public Pen( Color c, double strokeWidth, final double strokeInterval, final double strokeX ) {
		this.color = new RGB(c) ;
		if( strokeInterval > 0 )
			this.stroke = new BasicStroke( (float) strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { (float)strokeInterval, (float)strokeX }, 0f) ;
		else
			this.stroke = new BasicStroke( (float) strokeWidth ) ;
	}
	public void useOn( Graphics2D g ) {
		g.setColor( color ) ;
		g.setStroke(stroke ) ;
	}
	
	public void useColorOn( Graphics2D g) {
		g.setColor( color ) ;
	}
	
	public void useStrokeOn( Graphics2D g) {
		g.setStroke( stroke ) ;
	}
}
