/*******************************************************************************
 * Copyright (c) 2007, 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	compeople AG (Stefan Liebig) - initial API and implementation
 *******************************************************************************/
package de.compeople.commons.net.proxy.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import de.compeople.commons.util.StringUtils;

/**
 * Helper for PAC support
 */
public final class ProxySelectorPACUtils {

	private static final Map<String, Proxy.Type> PROXY_TYPE_MAP;

	private static final String PAC_PROXY_TYPE_DIRECT = "DIRECT";
	private static final String PAC_PROXY_TYPE_PROXY = "PROXY";
	private static final String PAC_PROXY_TYPE_SOCKS = "SOCKS";

	static {
		// mapping of pacProgram proxy type names to java proxy types:
		//  'DIRECT' -> Proxy.Type.DIRECT
		//  'PROXY'  -> Proxy.Type.HTTP
		//  'SOCKS'  -> Proxy.Type.SOCKS
		final Map<String, Proxy.Type> temp = new HashMap<String, Proxy.Type>();
		temp.put( PAC_PROXY_TYPE_DIRECT, Proxy.Type.DIRECT );
		temp.put( PAC_PROXY_TYPE_PROXY, Proxy.Type.HTTP );
		temp.put( PAC_PROXY_TYPE_SOCKS, Proxy.Type.SOCKS );
		PROXY_TYPE_MAP = Collections.unmodifiableMap( temp );
	}

	private ProxySelectorPACUtils() {
	// utility
	}

	/**
	 * @param pacFindProxyForUrlResult
	 * @return
	 */
	public static List<Proxy> getProxies( String pacFindProxyForUrlResult ) {
		if ( StringUtils.isDeepEmpty( pacFindProxyForUrlResult ) ) {
			return ProxySelectorUtils.getProxyListDirectAccessOnly();
		}

		final List<Proxy> result = new ArrayList<Proxy>();
		final Scanner scanner = new Scanner( pacFindProxyForUrlResult );
		scanner.useDelimiter( ";" );
		while ( scanner.hasNext() ) {
			final String pacProxy = scanner.next().trim();
			final Proxy proxy = getProxy( pacProxy );
			if ( proxy != null ) {
				result.add( proxy );
			}
		}

		return result;
	}

	private static Proxy getProxy( String pacProxy ) {

		if ( StringUtils.isEmpty( pacProxy ) ) {
			return Proxy.NO_PROXY;
		}

		if ( !startsWithProxyType( pacProxy ) ) {
			// Assume "PROXY" type!
			pacProxy = "PROXY " + pacProxy;
		}
		Scanner scanner = new Scanner( pacProxy );
		String pacProxyType = scanner.next();
		Proxy.Type proxyType = PROXY_TYPE_MAP.get( pacProxyType );
		if ( proxyType == null || proxyType == Proxy.Type.DIRECT ) {
			return Proxy.NO_PROXY;
		} else {
			String pacHostnameAndPort = null;
			if ( scanner.hasNext() ) {
				pacHostnameAndPort = scanner.next();
			}
			String hostname = getHostname( pacHostnameAndPort );
			if ( hostname != null ) {
				int port = getPort( pacHostnameAndPort );
				SocketAddress addr = new InetSocketAddress( hostname, port );
				return new Proxy( proxyType, addr );
			} else {
				return null;
			}
		}
	}

	private static boolean startsWithProxyType( String pacProxy ) {
		for ( String proxyType : PROXY_TYPE_MAP.keySet() ) {
			if ( pacProxy.startsWith( proxyType ) ) {
				return true;
			}
		}

		return false;
	}

	static String getHostname( String pacHostnameAndPort ) {
		if ( pacHostnameAndPort != null ) {
			return pacHostnameAndPort.substring( 0, pacHostnameAndPort.indexOf( ':' ) );
		} else {
			return null;
		}
	}

	static int getPort( String pacHostnameAndPort ) {
		if ( pacHostnameAndPort != null && pacHostnameAndPort.indexOf( ':' ) > -1 ) {
			return Integer.parseInt( pacHostnameAndPort.substring( pacHostnameAndPort.indexOf( ':' ) + 1 ) );
		} else {
			return 0;
		}
	}

}
