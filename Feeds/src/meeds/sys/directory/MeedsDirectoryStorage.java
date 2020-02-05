package meeds.sys.directory;

import meeds.sys.MeedsNode;
import feeds.sys.core.ID;
import feeds.sys.directory.DirectoryStorage;

public class MeedsDirectoryStorage extends DirectoryStorage {
	 
	public static final ID TUNNEL_CHANNEL = new ID(1000L) ;
	
	public static void init() {
		try {
			DirectoryStorage.init() ;
		
			if( MeedsNode.isMnode() ) {
		           			
				createSystemChannelRecord( "/System/RegistryQueryChannel", "meeds_rqc_template", new ID(105L) ) ;
	            createSystemTemplateRecord("meeds_rqc_template", meeds.sys.registry.channels.RegistryQueryChannel.class) ;          
	
	            createSystemChannelRecord( "/System/RegistryReplicationChannel", "meeds_rrc_template", new ID(106L) ) ;
	            createSystemTemplateRecord("meeds_rrc_template", meeds.sys.registry.channels.RegistryReplicationChannel.class) ;  
	            
			} else {
				createSystemChannelRecord( "/System/Meeds/RegistryQueryChannel", "meeds_rqc_template", new ID(105L) ) ;
	            createSystemTemplateRecord("meeds_rqc_template", meeds.sys.registry.channels.RegistryQueryChannel.class) ;          
	
	            createSystemChannelRecord( "/System/Meeds/RegistryReplicationChannel", "meeds_rrc_template", new ID(106L) ) ;
	            createSystemTemplateRecord("meeds_rrc_template", meeds.sys.registry.channels.RegistryReplicationChannel.class) ;          				
			}
				            
			createSystemChannelRecord("/System/Meeds/HomingChannel", "meeds_homing", new ID(110L));
			createSystemTemplateRecord("meeds_homing", meeds.sys.homing.channel.HomingChannel.class);

			createSystemChannelRecord("/System/Meeds/ProxyChannel", "meeds_proxying", new ID(111L));
			createSystemTemplateRecord("meeds_proxying", meeds.sys.proxying.channel.ProxyChannel.class);

			createSystemChannelRecord("/System/Meeds/ProxyDiscoveryChannel", "meeds_pdc", new ID(112L)).monitorable(true);
			createSystemTemplateRecord("meeds_pdc", meeds.sys.proxying.channel.ProxyDiscoveryChannel.class);

			createSystemChannelRecord("/System/Meeds/TunnelChannel", "meeds_tunnel", TUNNEL_CHANNEL ) ;
			createSystemTemplateRecord("meeds_tunnel", meeds.sys.tunnel.TunnelChannel.class);

                    

		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
