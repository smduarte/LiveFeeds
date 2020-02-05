package sensing.persistence.core.network;
import sensing.persistence.core.ServicesConfig;

public interface NetworkProvider {

	public void send(Peer p, NetworkMessage m);

	public List distTreeChildren(Peer root, Peer p, ServicesConfig.DistTreeType t);
	public Peer distTreeParent(Peer root, Peer p, ServicesConfig.DistTreeType t);
}
