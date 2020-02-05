package simsim.core;

import static simsim.core.Simulation.currentTime;

import java.util.Iterator;

import simsim.gui.canvas.Canvas;

class UdpPacket extends Task {

	final EndPoint src, dst;
	final private int length;
	final private double delay;
	final private EncodedMessage payload;

	public UdpPacket(EndPoint src, EndPoint dst, Message payload, double delay) {
		super(null, delay, payload.color);
		this.src = src;
		this.dst = dst;

		this.delay = delay;
		this.payload = payload.encode();
		this.length = payload.length();

		if (Traffic.displayLivePackets || (Traffic.trackLargePackets && length > udpPacketMTU))
			Traffic.liveUdpPackets.add(this);

		src.address.uploadedBytes += length > 0 ? udpHeaderLength + payload.length : 0 ;

//		System.out.println( payload.getClass() );
//		if (checkMessageLength && payload.length < 0)
//			throw new RuntimeException(payload.getClass() + " does not set message length");
		
	}

	public double delay() {
		return delay;
	}

	public int length() {
		return length;
	}

	public void run() {
				
		if (Traffic.displayLivePackets || (Traffic.trackLargePackets && length > udpPacketMTU))
			Traffic.liveUdpPackets.remove(this);

		if (Traffic.displayDeadPackets)
			Traffic.deadUdpPackets.add(this);

		if (dst.address.online) {

			dst.address.downloadedBytes += udpHeaderLength + length;

			payload.decode().deliverTo(src, dst.handler);

		} else {
			if( src.address.online)
				src.handler.onSendFailure(dst, payload.decode());
			
			dst.handler.onSendFailure(dst, null);
		}
	}

	static void cancelPackets(NetAddress addr) {
		double now = currentTime();
		for (Iterator<UdpPacket> i = Traffic.liveUdpPackets.iterator(); i.hasNext();) {
			UdpPacket j = i.next();
			if (j.length > udpPacketMTU && (addr == j.src.address || addr == j.dst.address)) {
				i.remove();
				j.cancel();
				double dt = (j.due - now) / j.delay;
				j.src.address.uploadedBytes -= dt * j.length;
				j.dst.address.downloadedBytes += (1 - dt) * j.length;
			}
		}
	}

	public String toString() {
		return "UdpPacket from:" + src + " to " + dst;
	}

	public void displayOn(Canvas canvas) {
		Message msg = payload.decode();

		if (msg.isVisible()) {
			double t = due - currentTime();
			double p = t / delay;
			msg.displayOn(canvas, src, dst, t, p);
		}
	}

	static final int udpPacketMTU = Globals.get("Net_MTU", Integer.MAX_VALUE);
	static final double udpHeaderLength = Globals.get("Net_UdpHeaderLength", 28.0);
	static final boolean checkMessageLength = Globals.get("Traffic_CheckMessageLength", true);
}
