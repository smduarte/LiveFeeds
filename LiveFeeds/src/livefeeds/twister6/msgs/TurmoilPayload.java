package livefeeds.twister6.msgs;

import java.util.ArrayList;
import java.util.List;

import livefeeds.twister6.Event;
import livefeeds.twister6.TurmoilNode;

import simsim.gui.canvas.RGB;
import simsim.sockets.Socket;
import simsim.sockets.SocketHandler;
import simsim.sockets.SocketMessage;

@SuppressWarnings("serial")
public class TurmoilPayload extends SocketMessage {

	public Event payload;
	final public List<TurmoilNode> path = new ArrayList<TurmoilNode>() ;

	public TurmoilPayload(Event payload) {
		super(false, RGB.RED);
		this.payload = payload;
	}
	
	public int length() {
		if (length < 0)
			length = 0;
		return length;
	}

	public String toString() {
		return String.format("DataPayload<%s>", payload);
	}

	public void deliverTo(Socket sock, SocketHandler handler) {
		((TurmoilSocketHandler) handler).onReceive(sock, this);
	}
	
	public TurmoilPayload append( List<TurmoilNode> path, TurmoilNode node ) {
		this.path.addAll( path ) ;
		this.path.add( 0, node ) ;
		return this ;
	}

}
