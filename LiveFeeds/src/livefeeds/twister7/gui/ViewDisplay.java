package livefeeds.twister7.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import livefeeds.twister7.CatadupaNode;
import livefeeds.twister7.GlobalDB;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;

public class ViewDisplay implements Displayable {

	int W, H;

	BufferedImage img = null;

	public void displayOn(Canvas canvas) {
		doOwnViewBitMap(canvas);

		canvas.gs.drawImage(img, 0, 0, 1000, 1000, null);
	}

	private void doOwnViewBitMap(Canvas canvas) {
		if (img == null) {
			int N = 768;
			img = canvas.gu.getDeviceConfiguration().createCompatibleImage(768, N, Transparency.OPAQUE);
		}
		Graphics2D G = (Graphics2D) img.getGraphics();

		G.setComposite(AlphaComposite.Src);
		G.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		G.fillRect(0, 0, img.getWidth(), img.getHeight());

		
		int CW = 64 ;
		int W = img.getWidth(), H = img.getHeight() ;

		SortedSet<CatadupaNode> nodes = new TreeSet<CatadupaNode>( new Sorter() ) ;
		for( CatadupaNode i : GlobalDB.liveNodes() )
			if( i != null && i.state.db.loadedEndpoints )
				nodes.add(i) ;
		
		
		int B = Integer.MAX_VALUE ;
		for( CatadupaNode i : nodes ) 
			B = Math.min( B, i.state.db.view.stamps.base ) ;
		
		
//		System.out.println( nodes.first() + "  " + nodes.first().state.db.view ) ;
		
		int Y = 0 ;
		for( CatadupaNode i : nodes ) {
			for( int j = 0 ; j < CW ; j++) {
				boolean v = i.state.db.view.stamps.get( B + j );
//				int k = GlobalDB.g_index - CW + j ;
// 				boolean v = i.state.db.knownNodes.get( GlobalDB.g_index - CW + j );

				int r = v ? 0 : 255;
				int g = v ? 255 : 0;
				int b =  0 ;
				int a = 128;

				int X = (Y/H) * (CW+2) + j ;
				
				if( X < W )
					img.setRGB(X, Y % H, a << 24 | r << 16 | g << 8 | b);
				else
					break ;
			}
			Y++ ;
		}
	}
	
	
	class Sorter implements Comparator<CatadupaNode> {

		public int compare(CatadupaNode a, CatadupaNode b) {
			int diff = a.state.db.view.stamps.base - b.state.db.view.stamps.base ;
			return diff != 0 ? diff : a.index - b.index ;
		}		
	}
}