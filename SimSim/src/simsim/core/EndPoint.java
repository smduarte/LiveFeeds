package simsim.core;

import static simsim.core.Simulation.Network;
import static simsim.core.Simulation.Spanner;
import static simsim.core.Simulation.rg;
import simsim.graphs.ShortestPathsTree;

/**
 * Objects of this class represent generic transport endpoints (i.e., they are
 * somewhat equivalent to sockets).
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 * 
 */
public class EndPoint {

	public int port;
	public NetAddress address;
	public MessageHandler handler;
	boolean isMulticastEndPoint = false;
	public BandwidthManager bm = new BandwidthManager( Double.MAX_VALUE, Double.MAX_VALUE ) ;

	/**
	 * Creates a new endpoint associated with the given network address.
	 * 
	 * @param addr
	 *            The network address associated with this endpoint.
	 * @param port
	 *            The port that distinguishes ports associated with the same
	 *            network address.
	 * @param handler
	 *            The handler that will be called when a message arrives.
	 */
	protected EndPoint(NetAddress addr, int port, MessageHandler handler) {
		this.port = port;
		this.address = addr;
		this.handler = handler;
	}

	/**
	 * Set the message handler; called automatically when a message arrives.
	 * 
	 * @param handler
	 */
	public void setHandler(MessageHandler handler) {
		this.handler = handler;
	}

	public void setBandwidthManager( BandwidthManager bm) {		
		this.bm = bm ;
	}

	public void setBandwidthRates( double peak, double rate) {
		bm = new BandwidthManager(peak, rate) ;
	}
	
	/**
	 * Provides a measure of one-way latency/network distance in seconds to
	 * another endpoint.
	 * 
	 * @param other
	 *            The other remote endpoint.
	 * @return The latency/ one-way network delay between the two endpoints
	 *         measured in seconds.
	 */
	public double latency(EndPoint other) {
		return address.latency(other.address);
	}

	/**
	 * Removes this endpoint from a multicast group.
	 * 
	 * @param group
	 *            The "name" of the multicast group, usually a string.
	 */
	public void leaveGroup(Object group) {
		Multicasting.leaveGroup(group, this);
	}

	/**
	 * Adds this endpoint to a multicast groip.
	 * 
	 * @param group
	 *            The "name" of the multicast group, usually a string.
	 * @return The endpoint of the multicast group.
	 */
	public EndPoint joinGroup(Object group) {
		return Multicasting.joinGroup(group, this);
	}

	/**
	 * Sets/Clears the flag indicating if this is a endpoint associated with a
	 * multicast group.
	 * 
	 * @param val
	 *            The new value of flag.
	 */
	void setMulticastFlag(boolean val) {
		this.isMulticastEndPoint = val;
	}

	/**
	 * Sends a message to all the nodes in the simulation. Should only be used
	 * in small-scale scenarios (up to to around 200 nodes)
	 * 
	 * @param m
	 *            The message to be broadcasted.
	 */
	public void broadcast(final Message m) {
		ShortestPathsTree<NetAddress> t = Spanner.shortestPathsTree(this.address);
		for (NetAddress i : Network.addresses()) {
			double delay = t.cost(i) * (1 + Network.JITTER * rg.nextDouble());
			new UdpPacket(this, i.endpoint, m, delay);
		}
	}

	/**
	 * Sends a message in asynchronous/non-blocking mode.
	 * 
	 * @param dst
	 *            The destination endpoint.
	 * @param m
	 *            The message being sent.
	 * @return An indication of the success or failure of the operation.
	 */
	public boolean udpSend(final EndPoint dst, final Message m) {
		return udpSend(dst, m, 0);
	}

	/**
	 * Sends a message in asynchronous/non-blocking mode, without accounting for tcp/ip overhead.
	 * 
	 * @param dst
	 *            The destination endpoint.
	 * @param m
	 *            The message being sent.
	 * @return An indication of the success or failure of the operation.
	 */
	public boolean rawSend(final EndPoint dst, final Message m) {
		return rawSend(dst, m, 0);
	}

	
	/**
	 * Sends a message in asynchronous/non-blocking mode.
	 * 
	 * @param dst
	 *            The destination endpoint.
	 * @param m
	 *            The message being sent.
	 * @param l
	 *            Application-level additional latency...
	 * 
	 * @return An indication of the success or failure of the operation.
	 */
	public boolean udpSend(final EndPoint dst, final Message m, double appDelay) {
		if (dst == null)
			return false;

		if (!address.online)
			throw new RuntimeException("Node Offline... No more message exchange allowed...");

		if (!dst.address.online) {
			new Task(0) {
				public void run() {
					handler.onSendFailure(dst, m);
//					dst.handler.onSendFailure(dst, m);
				}
			};
			return false;
		}

		if (dst.address == this.address)
			m.deliverTo(this, dst.handler);
		else if (dst.isMulticastEndPoint)
			dst.address.endpoints[port].handler.onReceive(this, m);
		else {
			double delay = dst.latency(this) * (1 + Network.JITTER * rg.nextDouble());			
			int length = m.length() ;
			double uploadDelay = length < 32 ? 0 : bm.accountTransfer( length );
			new UdpPacket(this, dst, m, delay + appDelay + uploadDelay);
		}
		return true;
	}

	/**
	 * Sends a message in asynchronous/non-blocking mode, without accounting tcp/ip header overhead.
	 * 
	 * @param dst
	 *            The destination endpoint.
	 * @param m
	 *            The message being sent.
	 * @param l
	 *            Application-level additional latency...
	 * 
	 * @return An indication of the success or failure of the operation.
	 */
	public boolean rawSend(final EndPoint dst, final Message m, double appDelay) {
		if (dst == null)
			return false;

		if (!address.online)
			throw new RuntimeException("Node Offline... No more message exchange allowed...");

		if (!dst.address.online) {
			new Task(0) {
				public void run() {
					handler.onSendFailure(dst, m);
//					dst.handler.onSendFailure(dst, m);
				}
			};
			return false;
		}

		if (dst.address == this.address)
			m.deliverTo(this, dst.handler);
		else if (dst.isMulticastEndPoint)
			dst.address.endpoints[port].handler.onReceive(this, m);
		else {
			double delay = dst.latency(this) * (1 + Network.JITTER * rg.nextDouble());			
			int length = m.length() ;
			double uploadDelay = length < 32 ? 0 : bm.accountTransfer( length );
			new RawPacket(this, dst, m, delay + appDelay + uploadDelay);
		}
		return true;
	}

	
	/**
	 * Sends a message in synchronous/blocking mode.
	 * 
	 * @param dst
	 *            The destination endpoint.
	 * @param m
	 *            The message being sent.
	 * @return Reference to the connection channel. Null if the destination node
	 *         is offline.
	 */
	public TcpChannel tcpSend(final EndPoint dst, final Message m) {
		if (dst == null)
			return null;
		else if (!dst.address.online) {
			new Task(0) {
				public void run() {
					handler.onSendFailure(dst, m);
//					dst.handler.onSendFailure(dst, m);
				}
			};
			return null;
		}
		return new TcpChannel(this, dst).tcpOpen(m);
	}

	/**
	 * Sends a message in synchronous/blocking mode.
	 * 
	 * @param dst
	 *            The destination endpoint.
	 * @param m
	 *            The message being sent.
	 * 
	 * @param appLat
	 *            Additional delay requested by the application...
	 * @return Reference to the connection channel. Null if the destination node
	 *         is offline.
	 */
	public TcpChannel tcpSend(final EndPoint dst, final Message m, double appLat) {
		if (dst == null)
			return null;
		else if (!dst.address.online) {
			new Task(0) {
				public void run() {
					handler.onSendFailure(dst, m);
				}
			};
			return null;
		}
		return new TcpChannel(this, dst, appLat).tcpOpen(m);
	}

	public String toString() {
		return handler != null ? handler.toString() : "?";
	}
}
