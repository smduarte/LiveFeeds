package livefeeds.twister7.msgs;

import java.util.ArrayList;
import java.util.List;

import livefeeds.twister7.CatadupaNode;
import livefeeds.twister7.Event;
import livefeeds.twister7.Range;
import livefeeds.twister7.TurmoilNode;
import livefeeds.twister7.View;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;
import simsim.sockets.SocketMessage;

@SuppressWarnings("serial")
public class TurmoilCast extends SocketMessage {

	final public int id;
	final public int level;
	final public View view;

	public Range range;

	final public List<TurmoilNode> path = new ArrayList<TurmoilNode>() ;
	
	final public Event payload;

	public TurmoilCast( int level, Range range, View view, Event payload) {
		super(true, RGB.RED);
		this.level = level;

		this.id = -1;
		this.view = view.clone();
		this.range = range.clone();
		this.payload = payload;
	}

	public TurmoilCast(int level, int id, Range range, View view, Event payload) {
		super(true, RGB.RED);
		this.level = level;

		this.id = id;
		this.view = view.clone();
		this.range = range.clone();
		this.payload = payload;
	}

	public int length() {
		if (length < 0)
			length = 0;
		return length;
	}

	public TurmoilCast append( TurmoilNode node ) {
		this.path.add( 0, node ) ;
		return this ;
	}
	
	public TurmoilCast append( List<TurmoilNode> path, TurmoilNode node ) {
		this.path.addAll( path ) ;
		this.path.add( 0, node ) ;
		return this ;
	}
	
	public int hashCode() {
		return id < 0 ? payload.serial : id;
	}

	public boolean equals(Object other) {
		return other != null && equals((TurmoilCast) other);
	}

	public boolean equals(TurmoilCast other) {
		return id < 0 ? payload.serial == other.payload.serial : id == other.id;
	}

	public String toString() {
		return String.format("PubSubEvent<%d, %s>", payload.serial, range);
	}

	
	public void deliverTo(Socket sock, SocketHandler handler) {
		
		if (sock.src != sock.dst) {
			CatadupaNode srcNode = sock.srcNode(), dstNode = sock.dstNode();

			int bytes = 320;

			double srcUpload = bytes ;

			srcNode.state.stats.turmoilTraffic.turmoil_upload += srcUpload;
			dstNode.state.stats.turmoilTraffic.turmoil_download += srcUpload;
		}
		
		
		((TurmoilSocketHandler) handler).onReceive(sock, this);
	}
}
