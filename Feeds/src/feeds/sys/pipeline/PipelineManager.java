package feeds.sys.pipeline;

import java.util.Hashtable;
import java.util.Map;

import feeds.sys.core.Dispatcher;
import feeds.sys.core.ID;
import feeds.sys.core.Template;
import feeds.sys.directory.ChannelRecord;
import feeds.sys.directory.DirectoryStorage;
import feeds.sys.directory.TemplateRecord;

public class PipelineManager {
		
	//To on-demand loader ...
	synchronized protected <E, P, F, Q> Pipeline<E, P, F, Q> setupNewPipeline( ChannelRecord r ) {
		return this.setupNewPipeline(r, new PacketQueue("piq"), new PacketQueue("poq") ) ;
	}
	
	synchronized protected <E, P, F, Q> Pipeline<E, P, F, Q> setupNewPipeline( ChannelRecord r, PacketQueue piq, PacketQueue poq ) {
		TemplateRecord ct = DirectoryStorage.lookupTemplateRecord( r.template(), 30 ) ;
		if( ct != null ) {
			Template<E, P, F, Q> processor = ct.newInstance() ;
			Pipeline<E, P, F, Q> p = new Pipeline<E, P, F, Q>( r.channel(), processor, piq, poq, r.monitorSubscriptions() ) ;
			ipd.registerQueue( r.channel(), p.pkt_iq() ) ;
			pipelines.put( r.channel(), p ) ;			
			processor.init() ;
			return p ;
		}
		return null ;
	}
		
	@SuppressWarnings("unchecked")
	public Pipeline pipeline( ID channel ) {
		Pipeline<?, ?, ?, ?> p = pipelines.get( channel ) ;
		if( p == null ) {
			try {
				ChannelRecord r = DirectoryStorage.lookupChannelRecord(channel, 30) ;
				return setupNewPipeline(r) ;
			} catch( Exception x ) {
				return null ;
			}
		}
		return p ;
	}
	
	
	@SuppressWarnings("unchecked")
	synchronized public Object getStub( ChannelRecord r, Object... extraArgs ) {
		Pipeline<?, ?, ?, ?> p = pipelines.get( r.channel() ) ; 
		if( p == null && (p = setupNewPipeline( r )) == null ) return null ;
		
		Object s = p.processor.getStub( r.name(), extraArgs) ;
		if( s == null ) s = new ChannelStub( p, r.name() ) ;
		
		return s ;
	}

	public Dispatcher dispatcher() {
		return ipd ;
	}
	
	protected PacketDispatcher ipd = new PacketDispatcher() ;
	protected Map<ID, Pipeline<?,?,?,?>> pipelines = new Hashtable<ID, Pipeline<?,?,?,?>>() ;
}
