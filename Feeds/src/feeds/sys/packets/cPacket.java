package feeds.sys.packets;

import java.io.*;

import feeds.api.*;
import feeds.sys.core.*;

public class cPacket extends Packet {
	private static final int MAX_TTL = 64;

	static final byte P_PACKET = 1;
	static final byte F_PACKET = 2;

	protected byte ttl;
	protected byte type;

	public ID channel;

	public boolean isLocal;
	public boolean isReRouted;
	private boolean packetized;

	
	@SuppressWarnings("unchecked")
	static public cPacket decode(Packet p) throws IOException {
		switch (p.peek(0)) {
		case P_PACKET:
			return new pPacket(p);
		case F_PACKET:
			return new fPacket(p);
		default:
			return new cPacket(p);
		}
	}

	protected cPacket(ID ch, int t) {
		type = (byte) t;
		channel = ch;
		ttl = MAX_TTL;
		isLocal = true;
		packetized = false;
	}

	protected cPacket(Packet p) throws IOException {
		super(p);
		isLocal = false;
		packetized = true;
		type = decoder().readByte();
		dec.readByte() ;
		ttl = --buf[1] ;
		channel = dec.readID();
	}

	protected void encode() throws IOException {
		super.encoder().writeByte(type);
		enc.writeByte(ttl);
		enc.writeID(channel);
		packetized = true;
	}

	synchronized public Packet packet() {
		try {
			if (!packetized)
				encode();
			return this;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	synchronized public int packetSize() throws FeedsException {
		return packet().size();
	}

	public ID channel() {
		return channel;
	}

	public boolean isLocal() {
		return isLocal;
	}

	

	public cPacket reRoute() {
		isLocal = false;
		isReRouted = true;
		return this;
	}

	public boolean isReRouted() {
		return isReRouted;
	}

	public void route(Router<?, ?, ?, ?> r) throws Exception {
		r.cRoute(this);
	}

	public int ttl() {
		return ttl ;
	}
	
	public boolean isExpired() {
		return ttl < 0;
	}
	
	public void ttl(int ttl) {
		this.ttl = ttl < 0 ? MAX_TTL : (byte) ttl;
		buf[1] = (byte) ttl;
	}

	public int type() {
		return type;
	}

	private static final long serialVersionUID = 1L;
}
