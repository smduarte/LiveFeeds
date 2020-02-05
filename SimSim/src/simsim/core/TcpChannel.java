package simsim.core;

import static simsim.core.Simulation.Scheduler;
import static simsim.core.Simulation.currentTime;
import simsim.gui.canvas.Canvas;
import simsim.gui.geom.Line;
import simsim.scheduler.Token;

/**
 * This class implements blocking network operations, allowing a message to be sent and replied-to within a channel.
 * 
 * A TcpChannel is a connection between two endpoints, established when a blocking send operation is performed on a endpoint (tcpSend).
 * 
 * At the receiving node, the message handler is called to get the message processed. A reference to the channel is passed along with the message, allowing the receiving node to 
 * know the origin of the message and to send a reply via the channel. The node that established the connection will block until the read operation is possible.
 * 
 * Currently, the channel only supports a single reply. It does not allow for a continuous dialogue.
 * 
 * @author  Sérgio Duarte (smd@di.fct.unl.pt)
 *
 */
public class TcpChannel implements Displayable {
		
	public EndPoint src, dst ;

	Message msg ;
	double latency, appLat ;
	double timeStamp = currentTime() ;

	private int state ;
	private Task task ;
	volatile private Token token ;

	
	TcpChannel( EndPoint src, EndPoint dst ) {
		this( src, dst, 0) ;
	}
	
	TcpChannel( EndPoint src, EndPoint dst, double appLat ) {
		this.state = 0 ;
		this.src = src ; this.dst = dst ;
		this.latency = src.address.latency( dst.address ) ;
		this.appLat = appLat ;
		
		if( Traffic.displayLiveChannels)
			Traffic.liveTcpChannels.add( this ) ;		
	}
	
	TcpChannel tcpOpen( Message m ) {
		
		if( ! src.address.online || ! dst.address.online )
			return null ;

		this.msg = m ;
		
		task = new TcpOpenTask( 3 * latency + appLat) ;
		token = Scheduler.newToken() ;
		token.block() ;
		
		double bytes = 3 * tcpHeaderLength + (m == null ? 0 : m.length()) ; // segment + acks overhead included. 
		src.address.uploadedBytes += bytes ;
		dst.address.downloadedBytes += bytes ;					
		
		return this ;
	}

	public boolean tcpReply( Message reply ) {
		if( state != 1 ) 
			throw new RuntimeException("TcpChannel.tcpReply(). Operation Not Allowed at this stage...") ;
		
		state++ ;
		msg = reply ;	

		task = new TcpReplyTask( latency ) ;
		
		return dst.address.online && src.address.online ;
	}
	
	boolean isClosed() {
		return state > 2 ;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T tcpRead() {
		if( state != 2 ) 
			throw new RuntimeException("TcpChannel.tcpRead(). Operation Not Allowed at this stage...") ;

		double bytes = 2 * tcpHeaderLength + (msg == null ? 0 : msg.length()) ; // segment + acks overhead included. 
		TcpChannel.this.dst.address.uploadedBytes += bytes ;
		TcpChannel.this.src.address.downloadedBytes += bytes ;	

		state = 3 ;
		this.timeStamp = currentTime() ;
		close() ;
		return (T)msg ;
	}
	
	public void close() {
		state = 3 ;
		task = null ;
		
		if( Traffic.displayLiveChannels)
			Traffic.liveTcpChannels.remove( this ) ;

		if( Traffic.displayDeadChannels)
			Traffic.liveTcpChannels.add( this ) ;		
	}
	
	class TcpOpenTask extends Task {
		private double delay ;
		
		TcpOpenTask( double delay ) {
			super( delay ) ;
			this.delay = delay ;
		}

		public void run() {	
			state++ ;
			msg.deliverTo( TcpChannel.this, dst.handler ) ;
		}
			
		
		public void displayOn( Canvas canvas ) {
	    	double t = ( due - currentTime() ) / delay ;
	    	msg.displayOn( canvas, src, dst, due, t ) ;
	    }
	}

	class TcpReplyTask extends Task {
		private double delay ;

		TcpReplyTask( double delay ) {
			super( delay ) ;
			this.delay = delay ;
		}

		public void run() {	
			token.unblock() ;
		}
				
		public void displayOn( Canvas canvas ) {
			double t = ( due - currentTime() ) / delay ;
	    	msg.displayOn( canvas, dst, src, due, t ) ;
		}		
	}

	
	public void displayOn(  Canvas canvas ) {
	    canvas.sDraw( new Line( src.address.pos, dst.address.pos ) ) ;    
	    if( task != null && task.isScheduled() && msg != null && msg.isVisible() )
	    	task.displayOn( canvas ) ;
	}


	static double tcpHeaderLength = Globals.get("Net_TcpHeaderLength", 40.0 ) ;

	
}

	