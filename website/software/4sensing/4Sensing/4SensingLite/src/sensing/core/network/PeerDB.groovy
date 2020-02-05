package sensing.core.network;

//import static sensing.core.query.quadtree.QTConstants.MIN_NODES_PER_QUAD;

import simsim.gui.geom.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import edu.wlu.cs.levy.CG.*;

class PeerDB {
	
	class PeerRecord {
		Peer peer;
		double x;
		double y;
	}
	
	static List peersList = [];
	static Map peers = [:];
	static double 	peerFilterWidth;
	static double 	peerFilterHeight;
	

	KDTree<Peer> kd;
	Map peersRec = [:];
	int referenceCount;

	public static void registerPeer( Peer p ) {
		peers[p.id] = p;
		peersList << p;
	}
	
//	public static void unregisterPeer( Peer p ) {
//		peers.remove[p.id];
//		peersList.remove(p);	
//	}
	
	public void markOffline( Peer p ) {
		println "### Mark offline peer $p.nodeId"
		PeerRecord pRec;
		if((pRec = peersRec[p.id]) != null) {
			//kd.delete([pRec.x, pRec.y] as double[]);
			double[] key = new double[2];
			key[0] = pRec.x;
			key[1] = pRec.y;
			try{
				kd.delete(key);
			} catch(KeyMissingException e) {}
		}
	}

	
	public PeerDB() {
		kd = new KDTree<Peer>(2);
		def nearest = {double x, double y ->
			Peer n = nearestImpl(kd, x, y);
			if(n) {
				return [peer: n, x: peersRec[n.id].x, y: peersRec[n.id].y];
			} else {
				return null;
			}
		}
		peersList.each{ Peer p -> 
			def (x,y) = p.newPeerLocation(nearest);
			PeerRecord r = new PeerRecord();
			r.peer = p;
			r.x = x;
			r.y = y;
			if(!p.isOffline()) {
				insertKDTree(x, y, p)
			}
			peersRec[p.id] = r;
		}
	}
	
	public void incrReferenceCount() {
		referenceCount++;	
	}
	
	public void decrReferenceCount() {
		referenceCount--;	
	}
	
	public void update() {
		println "### Updating PDB"
		kd = new KDTree<Peer>(2);
		peersList.each{ Peer p -> 
			if(!p.isOffline()) { 
				insertKDTree(peersRec[p.id].x, peersRec[p.id].y, p)
			} else {
				println "### Removing peed $p.nodeId"
			}
		} //kd.insert([p.x,p.y] as double[], p)
	}
	
	private void insertKDTree(double x, double y, Peer p) {
		double[] key = new double[2];
		key[0] = x;
		key[1] = y;
		kd.insert(key, p);
	}
	
	
	public List range(Rectangle r) {
//		try{throw new Exception()} catch(e){
//			println "Stack trace"
//			e.stackTrace.findAll{se -> se.className.startsWith("sensing")}.each{se -> println se.toString()}
//		}
		return kd.range([r.x, r.y] as double[], [r.maxX, r.maxY] as double[]);
	}
	
	protected static nearestImpl(KDTree<Peer> kd, double x, double y ) {
		if(peers.size() > 0) {
			try {
				Peer nearest =  kd.nearest([x, y] as double[]);
				return nearest
			} catch(ArrayIndexOutOfBoundsException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	public Peer nearest(double x, double y) {
		nearestImpl(kd, x, y)
	}

	protected  List minQuadRange(double pointX, double pointY, Rectangle quad) {
		double startX = (pointX - quad.x > quad.width/2) ? quad.x +(quad.width)/2 : quad.x;
		double startY = (pointY - quad.y > quad.height/2) ? quad.y +(quad.height)/2 : quad.y;
		Rectangle subQuad = new Rectangle(new Point2D.Double(startX,startY), new Point2D.Double(quad.width/2, quad.height/2));
		//services.logging.log(DEBUG, this, "minQuadRange", "subquad: ${subQuad}");
		List peers = range(subQuad);
		if(peers.size() < MIN_NODES_PER_QUAD) {
			//services.logging.log(DEBUG, this, "minQuadRange", "not enought nodes, returning ${quad}");
			return [range(quad), subQuad];
		} else {
			minQuadRange(pointX, pointY,subQuad);
		}
	}
	
	// Peer Filter
	
	public List accepts(Rectangle2D bounds) {
		List accepts = [];
		peersRec.each{UUID id, PeerRecord r ->
			if(!r.peer.isOffline()) {
				Ellipse2D.Double filter = new Ellipse2D.Double(r.x - peerFilterWidth * 0.5, r.y - peerFilterHeight * 0.5, peerFilterWidth, peerFilterHeight);
				if(filter.intersects(bounds.x, bounds.y, bounds.width, bounds.height)) {
					accepts << r.peer;
				}
			}
		}
		return accepts;
	}
	
	public Rectangle2D.Double getPeerFilterBounds(Peer p) {
		PeerRecord r = peersRec[p.id];
		if(r) {
			return new Rectangle2D.Double(r.x - peerFilterWidth * 0.5, r.y - peerFilterHeight * 0.5, peerFilterWidth, peerFilterHeight);
		} else {
			return null;
		}
	}
	
}
