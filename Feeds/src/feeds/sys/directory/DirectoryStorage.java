package feeds.sys.directory;

import static feeds.sys.FeedsRegistry.HARDSTATE;
import static feeds.sys.FeedsRegistry.SOFTSTATE;
import static feeds.sys.catadupa.Catadupa.CATADUPA_CHANNEL_ID;
import feeds.api.Feeds;
import feeds.api.FeedsException;
import feeds.sys.FeedsNode;
import feeds.sys.FeedsRegistry;
import feeds.sys.core.ID;
import feeds.sys.registry.RegistryItem;

public class DirectoryStorage {
    	    
	public static String reverseLookup( ID channel ) {
		return lookupChannelRecord( channel,  10 ).name() ;
	}
	
    static Object lookupDirectoryRecord( String name, int timeout ) throws FeedsException {
        Object o = FeedsRegistry.get( "/Local/Directory/" + name ) ;
        if( o != null) return o ;
        return FeedsRegistry.get( "/Global/Directory/" + name, false, timeout ) ;
    }
    
    
    public static ChannelRecord lookupChannelRecord( ID channel, int timeout ) throws FeedsException {
        double delay = 1 ;
        double deadline = Feeds.time() + timeout ;
        do {
        	for( RegistryItem i : FeedsRegistry.values("/Local/Directory/Channels", SOFTSTATE) ) {
                ChannelRecord r = i.value() ;
                if( channel.equals( r.channel() ) ) return r ;
            }

        	for( RegistryItem i : FeedsRegistry.values("/Global/Directory/Channels", SOFTSTATE) ) {
                ChannelRecord r = i.value() ;
                if( channel.equals( r.channel() ) ) return r ;
            }

        	for( RegistryItem i : FeedsRegistry.values("/Global/Directory/Channels", HARDSTATE) ) {
                ChannelRecord r = i.value() ;
                if( channel.equals( r.channel() ) ) return r ;
            }
        	FeedsNode.rqc().publish( "/Global/Directory/Channels/Ids/" + channel, null ) ;            
            Feeds.sleep( delay ) ;
            delay *= 2 ;
            
        } while( Feeds.time() < deadline ) ;
        throw new FeedsException("No answer after " + timeout + " seconds while searching registry for " + channel) ;
    }
    
    static public ChannelRecord lookupChannelRecord( String name, int timeout ) throws FeedsException {
        return ( ChannelRecord ) lookupDirectoryRecord( "Channels" + name, timeout ) ;
    }
    
    static public TemplateRecord lookupTemplateRecord( String name, int timeout ) throws FeedsException {
        return ( TemplateRecord )lookupDirectoryRecord( "Templates/" + name, timeout ) ;
    }
    
    static void createChannelRecord( String template, String name, int lifeSpan ) throws FeedsException {
        try {
        	ChannelRecord ecr = lookupChannelRecord( name, lifeSpan == 0 ? 1 : 20 ) ;
            Feeds.out.println( "Clone found:" + ecr ) ;
            if( ecr != null ) {
                if( template.equals( ecr.template() ) ) return ;
                else throw new FeedsException("Name/template mis-match.") ;
            }
        } catch( Exception x ) {
        	Feeds.out.printf("Channel \"%s\" unknown. Creating new record...\n", name) ;
        }
        try {
        	lookupTemplateRecord( template, 20 ) ;
        } catch( Exception x ) {
        	throw new FeedsException( String.format("\"%s\" not found...", template)) ;
        }
        ChannelRecord record = new ChannelRecord( template, name ) ;
        RegistryItem ri0 = new RegistryItem("/Global/Directory/Channels" + name, record, 0, lifeSpan, null );  
        FeedsRegistry.putItem( ri0, HARDSTATE ) ;
        RegistryItem ri1 = new RegistryItem("/Global/Directory/Channels/Ids/" + record.channel(), record, 0, lifeSpan, null );  
        FeedsRegistry.putItem( ri1, HARDSTATE ) ;
        
        if( lifeSpan != 0 ) {
        	FeedsNode.rrc().publish( ri0.key(), ri0 ) ;
        	FeedsNode.rrc().publish( ri1.key(), ri1 ) ;
        }
    }
    
    protected static ChannelRecord createSystemChannelRecord( String name, String template, ID channel ) {
    	channel = channel == null ? new ID() : channel ;
        String key = "/Local/Directory/Channels/" + (name.startsWith("/") ? name.substring(1) : name) ;
        FeedsRegistry.put( key, new ChannelRecord( template, name, channel), SOFTSTATE ) ;
        return FeedsRegistry.get(key) ;
    }
    
    protected static void createSystemTemplateRecord( String template, Class<?> className ) {
    	String key = "/Local/Directory/Templates/" + template ;
        FeedsRegistry.put( key, new TemplateRecord( template, className.getCanonicalName() ), SOFTSTATE ) ;
    }
    
    public static void init() {
        try {
            createSystemChannelRecord( "/System/BindingChannel", "bc_template", new ID(1L) ) ;
            createSystemTemplateRecord("bc_template", feeds.sys.binding.channels.BindingChannel.class) ;
            
            createSystemChannelRecord( "/System/HelloChannel", "hc_template", new ID(2L) ) ;
            createSystemTemplateRecord("hc_template", feeds.sys.backbone.channels.HelloChannel.class) ;

            createSystemChannelRecord( "/System/LinkStateChannel", "lc_template", new ID(3L) ) ;
            createSystemTemplateRecord("lc_template", feeds.sys.backbone.channels.LinkStateChannel.class) ;

            createSystemChannelRecord( "/System/MembershipChannel", "mc_catadupa", CATADUPA_CHANNEL_ID ) ;
            createSystemTemplateRecord("mc_catadupa", feeds.sys.catadupa.CatadupaChannel.class) ;

            createSystemChannelRecord( "/System/RegistryQueryChannel", "rqc_template", new ID(5L) ) ;
            createSystemTemplateRecord("rqc_template", feeds.sys.registry.channels.RegistryQueryChannel.class) ;          

            createSystemChannelRecord( "/System/RegistryReplicationChannel", "rrc_template", new ID(6L) ) ;
            createSystemTemplateRecord("rrc_template", feeds.sys.registry.channels.RegistryReplicationChannel.class) ;          

            createSystemTemplateRecord("catadupa", feeds.sys.catadupa.template.CatadupaTemplate.class) ;
            createSystemTemplateRecord("centradupa", feeds.sys.templates.centradupa.CentradupaTemplate.class) ;
                    
            createSystemTemplateRecord("catadupa_anycast", feeds.sys.catadupa.template.anycast.CatadupaAnycast.class) ;
            createSystemTemplateRecord("centradupa_anycast", feeds.sys.templates.centradupa.anycast.CentradupaAnycastTemplate.class) ;
 
            createSystemTemplateRecord("LocalLoop", feeds.sys.templates.localloop.AsynchronousLocalLoop.class) ;
            createSystemTemplateRecord("SynchronousLocalLoop", feeds.sys.templates.localloop.SynchronousLocalLoop.class) ;
            createSystemTemplateRecord("AsynchronousLocalLoop", feeds.sys.templates.localloop.AsynchronousLocalLoop.class) ;
            
        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
    }
    
    static {
    	init() ;
    }
}

