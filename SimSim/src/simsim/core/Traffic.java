package simsim.core;

import static simsim.core.Simulation.currentTime;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import simsim.gui.canvas.Canvas;

class Traffic implements Displayable {
	
	Traffic() {
		displayLivePackets = Globals.get("Traffic_DisplayLivePackets", true ) ;
		displayDeadPackets = Globals.get("Traffic_DisplayDeadPackets", false ) ;		
		displayLiveChannels = Globals.get("Traffic_DisplayLiveChannels", true ) ;
		displayDeadChannels = Globals.get("Traffic_DisplayDeadChannels", false ) ;		
		
		String deadPacketFilterMode = Globals.get("Traffic_DisplayDeadPacketsHistory", "time" ) ;

		filterDeadPackets = deadPacketFilterMode.equals("display") ;
		trackLargePackets = Globals.get("Traffic_TrackLargePackets", false ) ;

		if( displayDeadPackets ) {
			new PeriodicTask( deadPacketHistory / 5 ) {
				public void run() {
					double now = currentTime() ;
					for( Iterator<UdpPacket> i = deadUdpPackets.iterator() ; i.hasNext() ; )
						if( now - i.next().due < deadPacketHistory ) break ;
						else i.remove() ;
					
					while(deadUdpPackets.size() > deadPacketHistoryMaxSize)
						deadUdpPackets.removeLast() ;
				}
			};			
		}
		
		if( displayDeadChannels ) {
			new PeriodicTask( deadChannelHistory / 5 ) {
				public void run() {
					double now = currentTime() ;
					for( Iterator<TcpChannel> i = deadTcpChannels.iterator() ; i.hasNext() ; ) {
						TcpChannel c = i.next();
						if( c.isClosed() && now - c.timeStamp < deadChannelHistory ) break ;
						else i.remove() ;
					}
					
				}
			} ;
		}
	}
	
	private double lastDisplay = 0 ;
	public void displayOn( Canvas canvas ) {
		double now = currentTime() ;
		
		if( displayDeadPackets ) {
			for (UdpPacket i : deadUdpPackets)
				if( !filterDeadPackets || i.due >= lastDisplay )
					i.displayOn( canvas );
			for (RawPacket i : deadRawPackets)
				if( !filterDeadPackets || i.due >= lastDisplay )
					i.displayOn( canvas );
		}
		
		if( displayLivePackets ) {
			for (UdpPacket i : liveUdpPackets)
				i.displayOn( canvas );
			for (RawPacket i : liveRawPackets)
				i.displayOn( canvas );
		}
		
		if( displayDeadChannels )
			for (TcpChannel i : deadTcpChannels)
				i.displayOn( canvas );

		if( displayLiveChannels )
			for (TcpChannel i : liveTcpChannels)
				i.displayOn( canvas );

		lastDisplay = now ;
	}
	
	
	static java.util.Set<TcpChannel> liveTcpChannels = new HashSet<TcpChannel>();
	static java.util.LinkedList<TcpChannel> deadTcpChannels = new LinkedList<TcpChannel>();


	static java.util.Set<UdpPacket> liveUdpPackets = new HashSet<UdpPacket>();
	static java.util.LinkedList<UdpPacket> deadUdpPackets = new LinkedList<UdpPacket>();

	static java.util.Set<RawPacket> liveRawPackets = new HashSet<RawPacket>();
	static java.util.LinkedList<RawPacket> deadRawPackets = new LinkedList<RawPacket>();

	
	private static final double deadPacketHistory = Globals.get("Traffic_DeadPacketHistory", 5.0) ;
	private static final double deadChannelHistory = Globals.get("Traffic_DeadChannelHistory", 5.0) ;
	private static final double deadPacketHistoryMaxSize = Globals.get("Traffic_DeadPacketHistoryMaxSize", 512) ;
	
	static boolean filterDeadPackets ; 
	static boolean trackLargePackets ;
	static boolean displayLivePackets, displayDeadPackets ;
	static boolean displayLiveChannels, displayDeadChannels ;
}
