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

import java.net.URI;
import java.util.regex.Pattern;

/**
 * Encapsulates the proxy bypass list.
 *
 * <br>From MS WinHttp documentation of the WINHTTP_PROXY_INFO structure:<br>
 * <ul>
 *   <li> The proxy server list contains one or more of the following strings separated by semicolons or whitespace.
 *        <br> 
 *        ([&lt;scheme&gt;=][&lt;scheme&gt;"://"]&lt;server&gt;[":"&lt;port&gt;])</li>
 *   <li> The proxy bypass list contains one or more server names separated by semicolons or whitespace.
 *        The proxy bypass list can also contain the string "&lt;local&gt;" to indicate that all local intranet
 *         sites are bypassed. Local intranet sites are considered to be all servers that do not contain
 *         a period in their name.</li>
 * </ul>
 */
public class ProxyBypass {

	private final String proxyBypass;
	private final Pattern proxyBypassPattern;

	private final static String BYPASS_LOCAL_ADDESSES_TOKEN = "<local>";

	/**
	 * Create a ProxyBypass instance from the proxy bypass list string.
	 * 
	 * @param proxyBypass
	 */
	public ProxyBypass( String proxyBypass ) {
		this.proxyBypass = proxyBypass != null ? proxyBypass : "";

		if ( proxyBypass != null ) {
			String regExp = proxyBypass.replace( ";", "|" ).replace( ".", "\\." ).replace( "*", ".*" );
			this.proxyBypassPattern = Pattern.compile( regExp );
		} else {
			this.proxyBypassPattern = Pattern.compile( "" );
		}
	}

	/**
	 * Check whether the given uri should bypass the proxy.
	 * 
	 * @param uri
	 * @return
	 */
	public boolean bypassProxyFor( URI uri ) {
		assert uri != null;

		final String host = uri.getHost();
		if ( host != null ) {
			return ( isLocal( host ) && isBypassLocalAddresses( proxyBypass ) ) || isInBypassList( host );
		} else {
			return false;
		}
	}

	/**
	 * @param proxyBypass
	 * @param uri
	 * @return
	 */
	private boolean isInBypassList( String host ) {
		return proxyBypassPattern.matcher( host ).matches();
	}

	/**
	 * @param uri
	 * @return
	 */
	private static boolean isLocal( String host ) {
		return !host.contains( "." );
	}

	/**
	 * @param addressListString
	 * @return
	 */
	private static boolean isBypassLocalAddresses( String proxyBypass ) {
		return proxyBypass.contains( BYPASS_LOCAL_ADDESSES_TOKEN );
	}

}
