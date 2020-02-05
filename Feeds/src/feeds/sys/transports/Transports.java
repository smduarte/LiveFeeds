package feeds.sys.transports;

import java.util.HashMap;
import java.util.Map;

import feeds.api.Feeds;
import feeds.api.FeedsException;
import feeds.sys.FeedsNode;
import feeds.sys.core.Singleton;
import feeds.sys.core.Transport;
import feeds.sys.transports.liq.LiqTransportFactory;
import feeds.sys.transports.soq.SoqTransportFactory;
import feeds.sys.transports.tcp.TcpTransportFactory;
import feeds.sys.transports.tcp.nat.NatTransportFactory;
import feeds.sys.transports.udp.UdpTransportFactory;

public class Transports {

	public Transports init() throws FeedsException {
		
		factories.put("udp", Singleton.get( UdpTransportFactory.class));
		factories.put("tcp", Singleton.get( TcpTransportFactory.class));
		factories.put("nat", Singleton.get( NatTransportFactory.class));
		factories.put("soq", Singleton.get( SoqTransportFactory.class));
		factories.put("liq", Singleton.get( LiqTransportFactory.class));

		/*
		 * TODO 
		 * tm = new TransportMonitor() { public void handleBrokenTransport(
		 * Transport t, DataEnvelope de ) { System.out.println("GC'ing dead
		 * transport:" + t.url() ) ; dtc.put( t ) ; } } ; dtc = new
		 * DeadTransportsContainer() ;
		 */
		return this;
	}

	void dispose(Transport t) {
		try {
			if (t.isIncoming())
				incoming.remove( t.url());
			else
				outgoing.remove( t.url());

		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	private Transport create( Url url, String mode) {
		Transport res = null ;
		TransportFactory tf = factories.get( url.protocol());
		if( tf != null ) {
			res = tf.open( url.url(), mode) ;
			res.setPacketDispatcher( FeedsNode.plm().dispatcher() ) ;
			// t.setTransportMonitor( tm ) ;
			// TODO t.setDataEnvelopeHandler( (DataEnvelopeHandler)DeedsNode.liq() )
			// ;
		}
		return res;			
	}

	public Transport openTransport(String url, String mode) {
		//System.out.printf("OpenTransport:%s [%s]\n",url, mode) ;

		Url x = new Url( url.trim() );

		if (mode.equals("incoming")) {
			Transport t = incoming.get( x.url());
			if (t == null) {
				t = create(x, mode).open();
				incoming.put(x.url(), t);
				incoming.put(t.url(), t);
				Feeds.out.printf("Created Transport:%s <incoming>\n", t.url() ) ;
			}
			return t.open() ; 
		} else if (mode.equals("outgoing")) {
			Transport t = outgoing.get( x.url());
			if (t == null) {
				t = create(x, mode);
				outgoing.put(t.url(), t);
				Feeds.out.printf("Created Transport:%s <outgoing>\n", t.url()) ;
			}
			return t ;
		}
		throw new FeedsException("Invalid Transport mode:" + mode);
	}

	protected Map<String, Transport> incoming = new HashMap<String, Transport>() ;
	protected Map<String, Transport> outgoing = new HashMap<String, Transport>() ;
	protected Map<String, TransportFactory> factories = new HashMap<String, TransportFactory>() ;

	// private TransportMonitor tm ;
	// private DeadTransportsContainer dtc ;
}
