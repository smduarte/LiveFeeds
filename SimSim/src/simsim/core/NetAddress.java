package simsim.core;

import simsim.gui.canvas.Canvas;
import simsim.gui.canvas.RGB;
import simsim.gui.geom.XY;

abstract public class NetAddress implements Comparable<NetAddress>, Displayable {

	public XY pos; // The coordinates of the address in a plane.
	public RGB color; // The color that will be used to display the address.
	public EndPoint endpoint ; //default endpoint associated with this address, corresponding to port 0.

	public long uploadedBytes = 0;
	public long downloadedBytes = 0;

	protected boolean online = true;
	/**
	 * Creates a new NetAddress object, representing a node network address in the simulator, which are akin to the IP address of the node.
	 * 
	 * @param defaultHandler - Default handler for the messages sent to the owner node.
	 */
	protected NetAddress( MessageHandler defaultHandler) {
		this.color = RGB.GRAY ;
		this.endpoint = this.endpoint(0, defaultHandler) ;
	}
	
	/**
	 * Tells the online/offline state of the node.
	 * @return Returns the online/offline state of the node.
	 */
	public boolean isOnline() {
		return this.online ;
	}

	
	/**
	 * Computes the one-way delay incurred in sending a message to a given
	 * NetAddress destination. The delay is independent of message length.
	 * 
	 * @param destination Destination address.
	 * @return Returns the number of seconds needed to deliver a message to the given destination.
	 */
	public double latency( NetAddress destination) {
		return 0;
	}
	

	/**
	 * Used internally to close and free this NetAddress
	 */
	public void dispose() {
		online = false ;
		UdpPacket.cancelPackets( this ) ;
		RawPacket.cancelPackets( this ) ;
		
		for( EndPoint i : endpoints )
			Multicasting.leaveAllGroups(i) ;
	}
	

		
	/**
	 * Creates a new endpoint associated with this address identified by a port number.
	 * 
	 * @param port -the port associated with the new endpoint
	 * @param handler -the object responsible for handling any message sent to this endpoint.
	 * @return Returns the new endpoint.
	 */
	public EndPoint endpoint( int port, MessageHandler handler ) {
		handler = ( handler == null && endpoint != null ) ? endpoint.handler : handler ;
		ensurePortCapacity(port) ;
		return endpoints[port] = new EndPoint( this, port, handler ) ;
	}
	
	/**
	 * Returns the endpont identified by the given port number or null if none exists with the port identifier.
	 * @param port the port identifier of the endpoint being queried.
	 * @return the endpoint if it exists.
	 */
	public EndPoint endpoint( int port ) {
		for( EndPoint i : endpoints )
			if( i.port == port ) return i ;

		return null ;
	}
	
	/**
	 * Returns a string representation of this endpoint by requesting same from the message handler associated with this endpoint (usually a node).
	 * The string will be used as a label in the graphical representation of the node.
	 * @return the string representation of the endpoint.
	 */	
	public String toString() {
		return endpoint.handler.toString() ;
	}
	
	/**
	 * Disposes the current NetAddress object and returns a fresh one.
	 * Intended to simulate nodes that go offline and come back later with a new address.
	 * @return The replacement address.
	 */
	abstract public NetAddress replace() ;

	/*
	 * (non-Javadoc)
	 * 
	 * Called to draw this NetAddress in the simulator.
	 * 
	 * @see sim.gui.Displayable#display(java.awt.Graphics2D,
	 *      java.awt.Graphics2D)
	 */
	public void displayOn( Canvas canvas) {
	}
	
	public int compareTo( NetAddress other ) {
		Thread.dumpStack() ;
		return this == other ? 0 : this.hashCode() < other.hashCode() ? -1 : 1 ;
	}
	
	/**
	 * Resets the traffic statistics for this NetAddress.
	 */
	public void resetTrafficStats() {
		this.uploadedBytes = this.downloadedBytes = 0 ;
	}
	
	private void ensurePortCapacity( int port) {
		if( endpoints.length <= port ) {
			EndPoint[] tmp = endpoints ;
			endpoints = new EndPoint[port+1] ;
			System.arraycopy(tmp, 0, endpoints, 0, tmp.length) ;
		}
	}
	
	public EndPoint[] endpoints = new EndPoint[1] ;
}
