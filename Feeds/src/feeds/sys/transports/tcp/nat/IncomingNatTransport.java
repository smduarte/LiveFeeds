package feeds.sys.transports.tcp.nat;

import feeds.api.*;
import feeds.sys.core.*;
import feeds.sys.transports.*;

/**
 * 
 * @author Sérgio Duarte (smd@di.fct.unl.pt)
 */
public class IncomingNatTransport extends BasicTransport {

	public IncomingNatTransport(String urlString) {
		super(urlString, "incoming");
	}

	public Transport open() throws FeedsException {
		return this;
	}

	public void close() throws FeedsException {
	}
}