package feeds.sys.transports.tcp;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import feeds.api.Feeds;
import feeds.api.FeedsException;
import feeds.sys.FeedsNode;
import feeds.sys.core.Dispatcher;
import feeds.sys.core.ID;
import feeds.sys.core.Packet;
import feeds.sys.packets.cPacket;
import feeds.sys.transports.Url;

//-----------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------//
//-----------------------------------------------------------------------------------//
public class TcpConnection implements Runnable {

	private Url url;
	private ID other;

	private Socket socket;
	private DataInput dis;
	private DataOutputStream dos;
	private Dispatcher dispatcher;

	private boolean shuttingDown = false;

	TcpConnection(Socket socket, Url url, Dispatcher dispatcher) throws Exception {
		this.url = url;
		this.socket = socket;
		this.dispatcher = dispatcher;
		this.dos = new DataOutputStream(socket.getOutputStream());
		this.dis = new DataInputStream(socket.getInputStream());
		FeedsNode.id().minor().writeTo(dos);
		this.other = new ID(dis);

		Feeds.out.println("Connection established: " + this);
		Feeds.newThread(true, this).start();
		connections.put(other, this);
	}

	public void run() {
		try {
			for (;;) {
				Packet pkt = new Packet(dis);
				cPacket cpkt = cPacket.decode(pkt);
				 //Feeds.err.printf("Received TCP Packet: (%d bytes) : %s\n", pkt.length(), cpkt.channel ) ;
				dispatcher.dispatch(cpkt);
			}
		} catch (EOFException eofe) {
			if (!shuttingDown)
				Feeds.err.printf("Connection closed on transport [ %s ]\n", url);
		} catch (SocketException se) {
			if (!shuttingDown)
				Feeds.err.printf("Connection broken on transport [ %s <%s> ]\n", url, se.getMessage());
		} catch (Exception x) {
			x.printStackTrace() ;
			if (!shuttingDown)
				Feeds.err.printf("Unhandled exception on transport [ %s <%s> ]\n", url, x.getMessage());
		}
		close();
	}

	synchronized public void send(cPacket p) throws FeedsException {
		try {
			try {
				//Feeds.err.println("Sending TCP Packet: " + p + "(" + p.packetSize() + " bytes)") ;
				p.packet().writeTo((DataOutput) dos);
				dos.flush();
			} catch (SocketException se) {
				close();
				// TODO monitor.handleBrokenTransport( this, de ) ;
			}
		} catch (Exception x) {
			throw new FeedsException(x.getMessage());
		}
	}

	public void close() {
		try {
			notifyMonitors();
			connections.remove(other);
			shuttingDown = true;
			socket.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public String toString() {
		return String.format("tcp://(%s <-> %s) [%s <-> %s]", FeedsNode.id(), other, socket.getLocalSocketAddress(), socket.getRemoteSocketAddress());
	}

	public static TcpConnection byId(ID other) {
		for( ID i : connections.keySet() )
			if( i.major().equals(other))
				return connections.get(i);
		return null ;
	}

	private void notifyMonitors() {
		for (Monitor i : monitors)
			try {
				i.handleBrokenConnection(this);
			} catch (Exception x) {
				x.printStackTrace();
			}
	}

	public void addConnectionMonitor(Monitor mon) {
		monitors.add(mon);
	}

	public static interface Monitor {
		public void handleBrokenConnection(TcpConnection con);
	}

	private Set<Monitor> monitors = new HashSet<Monitor>();
	private static Map<ID, TcpConnection> connections = new HashMap<ID, TcpConnection>();
}