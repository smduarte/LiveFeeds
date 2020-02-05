package sensing.core.network;

public class NetworkMessage {
	final Object payload;
	int pDBVersionN;

	public NetworkMessage(Object payload, int pDBVersionN) {
		this.payload = payload;
		this.pDBVersionN = pDBVersionN;
	}
}
