package feeds.sys.transports;

import java.net.*;
import java.util.*;

import feeds.sys.*;
import feeds.sys.core.*;

public final class Url {

	private ID fid;
	private int port;
	private String url;
	private String locator;
	private String protocol;
	private String hostname;
	private String extravalues;

	public Url(String url) throws IllegalArgumentException {
		try {
			parseUrl(url);
		} catch (Exception x) {
			x.printStackTrace();
			throw new IllegalArgumentException("Invalid URL: <" + url + ">");
		}
	}

	public ID fid() {
		return fid;
	}

	public String url() {
		return url;
	}

	public String locator() {
		return locator;
	}

	public int port() {
		if (hostname == null)
			hostname();
		return port;
	}

	public String hostname() {
		if (hostname == null) {
			int j = locator.indexOf(':');
			if (j > 0) {
				hostname = locator.substring(0, j);
				port = Integer.parseInt(locator.substring(j + 1));
			} else
				hostname = locator;
			if (hostname.equals("-"))
					hostname = FeedsNode.ipAddress() ;
		}
		return hostname;
	}

	public String protocol() {
		return protocol;
	}

	public String extravalues() {
		return extravalues;
	}

	public InetAddress address() throws UnknownHostException {
		return InetAddress.getByName(hostname());
	}

	public String toString() {
		return url();
	}

	//
	// Parses the URL into its components
	//
	private void parseUrl(String s) throws Exception {
		int i = 0;
		final String r[] = { "udp:", "-:0", "?", null };
		StringTokenizer t = new StringTokenizer(s + '\0', "/");
		while (i < 3 && t.hasMoreTokens())
			r[i++] = t.nextToken().replace('\0', ' ').trim();
		try {
			r[i++] = t.nextToken("\0").substring(1);
		} catch (Exception x) {
		}
		;
		locator = r[1];
		extravalues = r[3];
		protocol = r[0].replace(':', ' ').trim();
		if (r[2].equals("?"))
			fid = new ID(0L);
		else
			fid = r[2].equals("-") ? FeedsNode.id() : new ID(r[2]);

		url = protocol() + "://" + locator();
		if (extravalues() != null || !fid().toString().equals("?"))
			url += "/" + fid();
		if (extravalues() != null)
			url += "/" + extravalues();
	}

	// -------------------------------------------------------------------------------------------------//
	public int hashCode() {
		return url().hashCode();
	}

	public boolean equals(Object o) {
		return url().equals(o.toString());
	}

}
