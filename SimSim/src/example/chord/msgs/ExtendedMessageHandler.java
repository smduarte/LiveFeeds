package example.chord.msgs;

import simsim.core.*;

public interface ExtendedMessageHandler extends MessageHandler {
	
	public void onReceive( EndPoint src, ChordMessage m) ;	
}
