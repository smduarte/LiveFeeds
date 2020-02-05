package meeds.sys.pipeline;

import feeds.api.*;
import feeds.sys.core.*;
import feeds.sys.pipeline.*;
import feeds.sys.directory.*;

import meeds.sys.*;
import static meeds.sys.directory.MeedsDirectoryStorage.*;

public class PipelineManager extends feeds.sys.pipeline.PipelineManager {
		
	@SuppressWarnings("unchecked")
	protected <E, P, F, Q> Pipeline<E, P, F, Q> setupNewPipeline( ChannelRecord r, PacketQueue piq, PacketQueue poq ) {
		Pipeline<E, P, F, Q> p ;
		if( piq != null && poq != null ) 
			p = super.setupNewPipeline(r, piq, poq ) ;
		else
			p = super.setupNewPipeline(r) ;
		
		if( p != null ) {
			if( MeedsNode.isMnode() && r.tunneling() )
				p.setDefaultRouter( tunnelRouter() ) ;
			else
				p.setDefaultRouter( new DefaultRouter( p.processor() ) ) ;			
		}
		return p ;		
	}
	
	@SuppressWarnings("unchecked")
	protected Router tunnelRouter() {
		if( tunnelRouter == null ) {
			Feeds.lookup("/System/Meeds/TunnelChannel") ;
			Pipeline<?,?,?,?> p = pipeline( TUNNEL_CHANNEL ) ;
			tunnelRouter = p.processor() ;
		}
		return tunnelRouter ;
	}

	private Router<?,?,?,?> tunnelRouter = null ;
}
