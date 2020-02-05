package meeds.sys.core;

import feeds.api.* ;

import feeds.sys.core.*;
import feeds.sys.registry.*;

import meeds.sys.homing.*;
import meeds.sys.proxying.*;

public abstract class MobilityContext extends NodeContext {
	 
	protected MobilityContext() {
		this( new ID() ) ;
	}

	protected MobilityContext( ID v ) {
		super( v ) ;
		mContext = this ;
		
		isMnode = true ;
		type = NodeType.mNODE ;
		isServer = isSnode = isPnode = false ;		
	}
		
	@Override
	public void makeCurrent() {
    	context = mContext = this ;
    }
	
	public boolean isMnode ;

	public Channel<String, Void, Void, RegistryItem> m_rqc;
    public Channel<String, RegistryItem, Void, Void> m_rrc;
    
	public Channel<Position, ID, ID, ProxyInfo> pdc;
	public Channel<Void, HomingRequest, Void, HomingReply> hbc;
	public Channel<Void, ProxyBindingRequest, Void, ProxyBindingReply> pxc;
 
	public static MobilityContext mContext ;
}
