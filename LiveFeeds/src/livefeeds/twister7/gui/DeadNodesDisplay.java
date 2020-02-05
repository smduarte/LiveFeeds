package livefeeds.twister7.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.TreeSet;

import livefeeds.twister7.CatadupaNode;
import livefeeds.twister7.GlobalDB;

import simsim.core.Displayable;
import simsim.gui.canvas.Canvas;

public class DeadNodesDisplay implements Displayable {

	int W, H;

	BufferedImage img = null;

	public void displayOn(Canvas canvas) {
		doOwnViewBitMap(canvas);

		canvas.gs.drawImage(img, 0, 0, 1000, 1000, null);
	}

	private void doOwnViewBitMap(Canvas canvas) {
		if (img == null) {
			int N = 1024;
			img = canvas.gu.getDeviceConfiguration().createCompatibleImage(1280, N, Transparency.OPAQUE);
		}
		Graphics2D G = (Graphics2D) img.getGraphics();

		G.setComposite(AlphaComposite.Src);
		G.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		G.fillRect(0, 0, img.getWidth(), img.getHeight());

		TreeSet<CatadupaNode> nodes = new TreeSet<CatadupaNode>(new IndexComparator());
		nodes.addAll(GlobalDB.liveNodes());

		int B = Integer.MAX_VALUE, T = Integer.MIN_VALUE ;
		for( CatadupaNode i : GlobalDB.liveNodes() )
			if( i.state.joined ) {
				T = Math.max( T, i.state.db.deadNodes.top );
				B = Math.min( B, i.state.db.deadNodes.firstCleared() );
			}

		int CW = 256;
		if( T - B > CW )
			B += T - B - CW ;
		
		B = T - CW ;
		
		int W = img.getWidth(), H = img.getHeight();

		int Y = 0;
		for (CatadupaNode i : nodes) {
			for (int j = 0; j < CW; j++) {
				boolean v = i.state.db.deadNodes.get(j + B);

				int r = v ? 0 : 255;
				int g = v ? 255 : 0;
				int b = 0;
				int a = 128;

				int X = (Y / H) * (CW + 2) + j;

				if (X < W)
					img.setRGB(X, Y % H, a << 24 | r << 16 | g << 8 | b);
				else
					break;
			}
			Y++;
		}
	}

	static class IndexComparator implements Comparator<CatadupaNode> {
		@Override
		public int compare(CatadupaNode a, CatadupaNode b) {
			return a.index - b.index;
		}

	}
}