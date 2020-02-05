package sensing.core.network

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.UUID;

import sensing.core.ServiceManager;
import sensing.core.Service;
import sensing.core.query.QueryData;
import simsim.gui.geom.Rectangle;

class NetworkService  extends Service {

	private static Map pDBVersions = [:];
	private static int latestPDBVersionN = 0;
	private static Spanner sp = new Spanner();
	static int LocationChangePeriod = 0;
	
	Peer local;
	Map  localPDBVersions = [:];
	int pDBVersionN;
	double pDBVersioningStartTs;
	
	public NetworkService(ServiceManager services) {
		super(services);
	}
	
	public static void update() {
		pDBVersions.values()*.update();
		sp.update();	
	}

	
	private static PeerDB getPeerDB(int version) {
		assert (version == latestPDBVersionN) || (version == latestPDBVersionN + 1) 
		PeerDB db;
		if(pDBVersions[version]) {
			db = pDBVersions[version];
		} else {
			//println "### New PDB Version $version"
			db = pDBVersions[version] = new PeerDB();
			sp.setPeerDB(db);
		}
		latestPDBVersionN = version > latestPDBVersionN ? version : latestPDBVersionN;
		// TODO: ref counts
		db.incrReferenceCount();
		if(pDBVersions[version-1]) {
			pDBVersions[version-1].decrReferenceCount();
			if(pDBVersions[version-1].referenceCount == 0) {
				//println "### Removing PDB version ${version-1}"
				pDBVersions.remove(version-1);
			}
		}
		return db;
	}
	
	public getpDB() {
		return localPDBVersions[pDBVersionN];	
	}
	
	public void init() {
		local = services.config.peer;
		PeerDB.registerPeer(services.config.peer);
	}

	private static LOCATION_CHANGE_DESSINC = 60 // desicronization between location change tasks (in seconds)
	
	public void start() {
		setPDBVersion(0);
		if(LocationChangePeriod) {
			services.scheduler.scheduleOnce(LocationChangePeriod + services.random.nextDouble()*LOCATION_CHANGE_DESSINC) {
				pDBVersioningStartTs = services.scheduler.currentTime();
				services.scheduler.schedule(LocationChangePeriod) {
					int currVersionN = (int) ((services.scheduler.currentTime() - pDBVersioningStartTs) / LocationChangePeriod + 1);
					syncPDB(currVersionN);
				}
			}
		}
	}
	
	
	public void syncPDB(int versionN) {
		assert versionN <= pDBVersionN + 1;
		if(versionN > pDBVersionN) {
			incrementPDBVersion();
		}
	}
	
	private void incrementPDBVersion() {
		setPDBVersion(++pDBVersionN);
		
		services.query.notifyNewPeerDB(pDBVersionN);
		services.scheduler.scheduleOnce(LOCATION_CHANGE_DESSINC) {
			localPDBVersions.remove(pDBVersionN-1);
			services.query.notifyDeletedPeerDB(pDBVersionN-1);
		}
	} 
	
	private void setPDBVersion(int verionN) {
		pDBVersionN = verionN;
		PeerDB pDB =  getPeerDB(pDBVersionN);
		localPDBVersions[pDBVersionN] = pDB;
		sp.setPeerDB(pDB);
	}
	

	

	
	// Peer accessors
	
	public static int size() {
		return PeerDB.peers.size();
	}


	public Peer randomPeer() {
		Peer p = null;
		while(!p || p.isOffline()) {
			p = PeerDB.peersList[services.random.nextInt(PeerDB.peersList.size())];
		}
		return p;
	}

	public static Collection getPeers() {
		return PeerDB.peersList;
	}

	public static Peer getPeer(UUID id) {
		return PeerDB.peers[id];
	}

	public List getLocation(UUID peerId) {
		return [pDB.peersRec[peerId].x, pDB.peersRec[peerId].y];
	}
	
	public List getLocation() {
		return getLocation(local.id);
	}
	
	// Spatial Queries
	
	public List range(Rectangle r) {
		return pDB.range(r);
	}

	public Peer nearest(double x, double y) {
		return pDB.nearest(x,y)	
	}


	public  List minQuadRange(double pointX, double pointY) {
		return pDB.minQuadRange(pointX, pointY, services.config.world);
	}
	
	// Peer filter
	
	public  getPeerFilterBounds() {
		pDB.getPeerFilterBounds(local)
	}
	
	// Spanner
	
	
	public List children(UUID rootId, Rectangle bounds) {
		sp.children(PeerDB.peers[rootId], bounds, local);
	}
	
	public List children(UUID rootId, Rectangle bounds, Peer parent) {
		sp.children(PeerDB.peers[rootId], bounds, parent);
	}
	
	public Peer parent(UUID rootId, Rectangle bounds) {
		sp.parent(PeerDB.peers[rootId], bounds, local);
	}
	
	// Comunication
	
	public send(Peer p, Object payload) {
		services.config.networkImpl.send(p, new NetworkMessage(payload, pDBVersionN));
	}
	
	public sendFailed(UUID destId, NetworkMessage m) {
		Peer dest = PeerDB.peers[destId];
		println "### Network Service - send to ${dest.nodeId} failed"
		markOffline(dest);
		onSendError(dest, m.payload);
	}
	
	public void markOffline(Peer p) {
		pDBVersions.values()*.markOffline(p);
		sp.update();
	}

	public receive(UUID srcId, NetworkMessage m) {
		syncPDB(m.pDBVersionN);
		onReceive(PeerDB.peers[srcId], m.payload);
	}

	public broadcast(Object payload) {
		// sim - send directly
		PeerDB.peersList.each{ Peer dst -> sendDirect(dst, payload) }
	}
	
	public sendDirect(Peer dst, Object payload) {
		if(!dst.isOffline()) dst.services.network.receive(local.id, new NetworkMessage(payload, pDBVersionN))
	}
	
	public void addMessageHandler(Closure clos) {
		this.metaClass.onReceive << clos;
	}
	
	public void addErrorHandler(Closure clos) {
		this.metaClass.onSendError << clos;
	}
	
}
