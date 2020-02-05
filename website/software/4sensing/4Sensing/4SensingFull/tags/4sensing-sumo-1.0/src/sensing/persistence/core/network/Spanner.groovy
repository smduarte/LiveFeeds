package sensing.persistence.core.network;

import sensing.persistence.core.network.*;
import simsim.gui.geom.Rectangle;

public class Spanner {
	public static final int BRANCHF = 6;
	
	protected Map trees = [:];
	protected PeerDB pDB;

	public void setPeerDB(PeerDB pDB) {
		this.pDB = pDB;
		update();
	}
	
	public void update() {
		trees = [:];
	}

	public children(Peer root, Rectangle bounds, Peer parent) {
		getSpannerTree(root, bounds).children[parent];
	}

	public Peer parent(Peer root, Rectangle bounds, Peer child) {
		getSpannerTree(root, bounds).parent[child];
	}

	protected getSpannerTree(Peer root, Rectangle bounds) {
		def tree;
		if((tree = trees["${root}_${bounds}"]) == null) {
			tree = generateSpannerTree(root, bounds);
			trees["${root}_${bounds}"] = tree;
		} 
		return tree;
	}

	protected Tree generateSpannerTree(Peer root, Rectangle bounds) {
		Tree t = new Tree(root);
		//def boundPeers = pDB.accepts(bounds);
		//TODO: martelada
		def boundPeers = pDB.peersList;
		if(!(root in boundPeers)) {
			boundPeers = [root, *boundPeers]
		}
		boundPeers = boundPeers.sort{peer -> peer.id}
		
		int rootIdx = boundPeers.indexOf(root);
		int currentIdx = rootIdx;
		int nextChildIdx = (currentIdx+1) % boundPeers.size();
		
		int done = 1;
		while(done < boundPeers.size()) {
			Peer c = boundPeers[currentIdx];
			Math.min(BRANCHF, boundPeers.size()-done).times {
				t.addChild(c,boundPeers[nextChildIdx]);
				nextChildIdx = (nextChildIdx + 1) % boundPeers.size();
				done++;
			}
			currentIdx  = (currentIdx + 1) % boundPeers.size();
		}
		return t;
	}
}
