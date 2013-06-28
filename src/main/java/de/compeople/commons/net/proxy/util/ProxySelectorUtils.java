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
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper
 */
public final class ProxySelectorUtils {

	static private int PROXY_DEFAULT_PORT = 80;
	static private int HTTPPROXY_DEFAULT_PORT = PROXY_DEFAULT_PORT;
	static private int HTTPSPROXY_DEFAULT_PORT = 443;
	static private int SOCKSPROXY_DEFAULT_PORT = 1080;

	private static final List<Proxy> proxyListDirectAccessOnly = Collections.singletonList( Proxy.NO_PROXY );
	private static final List<Proxy> emptyProxyList = Collections.emptyList();

	private static Logger LOGGER = Logger.getLogger( ProxySelectorUtils.class.getName() );

	private ProxySelectorUtils() {
		super();
	}

	/**
	 * Answer a list of proxies allowing direct access only
	 * 
	 * @return	<code>List&lt;Proxy&gt;</code> an immutable list containing the <code>Proxy.NO_PROXY</code> element only;
	 */
	public static List<Proxy> getProxyListDirectAccessOnly() {
		return proxyListDirectAccessOnly;
	}

	/**
	 * Answer an empty immutable list of proxies
	 * 
	 * @return	an empty immutable <code>List&lt;Proxy&gt;</code>.
	 */
	public static List<Proxy> getEmptyProxyList() {
		return emptyProxyList;
	}

	/**
	 * Scan the proxy list string and fill this information in the correct list or map.
	 * <br>The proxy list contains one or more of the following strings separated by semicolons:<br> 
	 * <code><pre>
	 * ([&lt;scheme&gt;=][&lt;scheme&gt; "://" ]&lt;server&gt;[ ":" &lt;port&gt;])
	 * </pre></code>
	 * 
	 * @param proxyList
	 * @param universalProxies
	 * @param protocolSpecificProxies
	 */
	public static void fillProxyLists( String proxyList, List<Proxy> universalProxies, Map<String, List<Proxy>> protocolSpecificProxies ) {
		Scanner scanner = new Scanner( proxyList );
		scanner.useDelimiter( ";" );
		while ( scanner.hasNext() ) {
			createProxy( scanner.next(), universalProxies, protocolSpecificProxies );
		}
	}

	private static void createProxy( final String proxyDefinition, List<Proxy> universalProxies, Map<String, List<Proxy>> protocolSpecificProxies ) {
		String protocol = null;
		String host = null;
		int port = 0;

		int urlStart = 0;
		// if there is no '=' character within the proxy definition we have a proxy
		// definition that serves all protocols. In this case we MUST ignore the protocol,
		// otherwise the protocol MUST be used to determine the specific proxy settings
		if ( proxyDefinition.indexOf( "=" ) != -1 ) {
			protocol = proxyDefinition.substring( 0, proxyDefinition.indexOf( "=" ) );
			urlStart = proxyDefinition.indexOf( "=" ) + 1;
		}

		try {
			// The scheme of the uri is irrelevant. We add the http://
			// scheme to enable class URI to parse the stuff
			String augmentedURI = proxyDefinition.substring( urlStart );
			if ( augmentedURI.indexOf( "://" ) == -1 ) {
				augmentedURI = "http://" + augmentedURI;
			}
			URI uri = new URI( augmentedURI );
			host = uri.getHost();
			port = uri.getPort() > 0 ? uri.getPort() : getProxyDefaultPort( protocol );
		} catch ( Exception ex ) {
			LOGGER.log( Level.SEVERE, "not a valid proxy definition: '" + proxyDefinition + "'.", ex );
			return;
		}

		if ( host == null ) {
			LOGGER.log( Level.SEVERE, "not a valid proxy definition: '" + proxyDefinition + "'." );
			return;
		}

		if ( protocol == null ) {
			universalProxies.add( createProxy( Proxy.Type.HTTP, host, port ) );
		} else {
			addProtocolSpecificProxy( protocolSpecificProxies, protocol, createProxy( resolveProxyType( protocol ), host, port ) );
		}
	}

	private static int getProxyDefaultPort( String protocol ) {
		if ( protocol == null )
			return PROXY_DEFAULT_PORT;
		if ( "http".equalsIgnoreCase( protocol ) )
			return HTTPPROXY_DEFAULT_PORT;
		if ( "https".equalsIgnoreCase( protocol ) )
			return HTTPSPROXY_DEFAULT_PORT;
		if ( "socks".equalsIgnoreCase( protocol ) )
			return SOCKSPROXY_DEFAULT_PORT;
		if ( "socket".equalsIgnoreCase( protocol ) )
			return SOCKSPROXY_DEFAULT_PORT;

		return PROXY_DEFAULT_PORT;
	}

	private static void addProtocolSpecificProxy( Map<String, List<Proxy>> protocolSpecificProxies, String protocol, Proxy proxy ) {
		List<Proxy> list = protocolSpecificProxies.get( protocol );
		if ( list == null ) {
			list = new ArrayList<Proxy>();
			protocolSpecificProxies.put( protocol, list );
		}

		list.add( proxy );
	}

	/**
	 * @param type
	 * @param host
	 * @param port
	 * @return
	 */
	public static Proxy createProxy( Proxy.Type type, String host, int port ) {
		return new Proxy( type, InetSocketAddress.createUnresolved( host, port ) );
	}

	/**
	 * @param protocol
	 * @return
	 */
	public static Proxy.Type resolveProxyType( String protocol ) {
		// TODO: return HTTP proxy for well-known high level protocols only?
		if ( protocol.equalsIgnoreCase( "socks" ) || protocol.equalsIgnoreCase( "socket" ) ) {
			return Proxy.Type.SOCKS;
		} else {
			return Proxy.Type.HTTP;
		}
	}

	/**
	 * @param scheme
	 * @param sa
	 * @return
	 */
	public static Proxy createProxy( String scheme, SocketAddress sa ) {
		Proxy.Type type = resolveProxyType( scheme );
		return new Proxy( type, sa );
	}

}
