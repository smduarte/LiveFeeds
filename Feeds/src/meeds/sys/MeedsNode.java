package meeds.sys;

import static feeds.sys.core.NodeContext.context;
import static meeds.sys.core.MobilityContext.mContext;
import meeds.sys.homing.HomingReply;
import meeds.sys.homing.HomingRequest;
import meeds.sys.homing.Position;
import meeds.sys.proxying.ProxyBindingReply;
import meeds.sys.proxying.ProxyBindingRequest;
import meeds.sys.proxying.ProxyInfo;
import feeds.api.Channel;
import feeds.api.Feeds;
import feeds.sys.FeedsNode;
import feeds.sys.core.ID;
import feeds.sys.registry.RegistryItem;

public class MeedsNode extends FeedsNode {
	
	synchronized static public Channel<Void, HomingRequest, Void, HomingReply> hbc() {
		if (mContext.hbc == null) {
			mContext.hbc = Feeds.lookup("/System/Meeds/HomingChannel");
		}
		return mContext.hbc;
	}

	synchronized static public Channel<Position, ID, ID, ProxyInfo> pdc() {
		if (mContext.pdc == null) {
			mContext.pdc = Feeds.lookup("/System/Meeds/ProxyDiscoveryChannel");
		}
		return mContext.pdc;
	}

	synchronized static public Channel<Void, ProxyBindingRequest, Void, ProxyBindingReply> pxc() {
		if (mContext.pxc == null) {
			mContext.pxc = Feeds.lookup("/System/Meeds/ProxyChannel");
		}
		return mContext.pxc;
	}

	synchronized static public Channel<String, Void, Void, RegistryItem> rqc() {
		if (MeedsNode.isMnode())
			return FeedsNode.rqc();
		else {
			if (mContext.m_rqc == null) {
				mContext.m_rqc = Feeds.lookup("/System/Meeds/RegistryQueryChannel");
			}
			return mContext.m_rqc;
		}
	}

	synchronized static public Channel<String, RegistryItem, Void, Void> rrc() {
		if ( MeedsNode.isMnode())
			return FeedsNode.rrc();
		else {
			if (mContext.m_rrc == null) {
				mContext.m_rrc = Feeds.lookup("/System/Meeds/RegistryReplicationChannel");
			}
			return mContext.m_rrc;
		}
	}

	
	public static boolean isMnode() {
		return context == mContext && mContext.isMnode;
	}

}
