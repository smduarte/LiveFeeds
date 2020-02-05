package simsim.core;

import static simsim.core.Simulation.currentTime;
import simsim.gui.canvas.Canvas;

class RawPacket extends Task {

	final EndPoint src, dst;
	final private int length;
	final private double delay;
	final private EncodedMessage payload;

	public RawPacket(EndPoint src, EndPoint dst, Message payload, double delay) {
		super(null, delay, payload.color);
		this.src = src;
		this.dst = dst;

		this.delay = delay;
		this.payload = payload.encode();
		this.length = payload.length();

		if (Traffic.displayLivePackets || (Traffic.trackLargePackets && length > udpPacketMTU))
			Traffic.liveRawPackets.add(this);
	}

	public double delay() {
		return delay;
	}

	public int length() {
		return length > 0 ? length : 0 ;
	}

	public void run() {
				
		if (Traffic.displayLivePackets || (Traffic.trackLargePackets && length > udpPacketMTU))
			Traffic.liveRawPackets.remove(this);

		if (Traffic.displayDeadPackets)
			Traffic.deadRawPackets.add(this);

		if (dst.address.online) {

			payload.decode().deliverTo(src, dst.handler);

		} else {
			if( src.address.online)
				src.handler.onSendFailure(dst, payload.decode());
			
			dst.handler.onSendFailure(dst, null);
		}
	}

	// TODO, maybe replace this with a report to the src/dst nodes with the % completed...
	static void cancelPackets(NetAddress addr) {
//		double now = currentTime();
//		for (Iterator<RawPacket> i = Traffic.liveRawPackets.iterator(); i.hasNext();) {
//			RawPacket j = i.next();
//			if (j.length > udpPacketMTU && (addr == j.src.address || addr == j.dst.address)) {
//				i.remove();
//				j.cancel();
//				double dt = (j.due - now) / j.delay;
//				j.src.address.uploadedBytes -= dt * j.length();
//				j.dst.address.downloadedBytes += (1 - dt) * j.length();
//			}
//		}
	}

	public String toString() {
		return "RawPacket from:" + src + " to " + dst;
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
}
