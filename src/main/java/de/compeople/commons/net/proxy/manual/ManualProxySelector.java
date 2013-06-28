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
package de.compeople.commons.net.proxy.manual;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.compeople.commons.net.proxy.util.ProxySelectorUtils;

/**
 * A proxy selector for a �manual� specified proxy.
 */
public class ManualProxySelector extends ProxySelector {

    private static final Logger log = LoggerFactory.getLogger(ManualProxySelector.class);

	private List<Proxy> universalProxies;
	private Map<String, List<Proxy>> protocolSpecificProxies;


	/**
	 * Create a manual proxy selector returning a DIRECT proxy.
	 */
	public ManualProxySelector() {
		this.universalProxies = null;
		this.protocolSpecificProxies = null;
	}

	/**
	 * Create a manual proxy selector with the given universal proxies.
	 *
	 * @param universalProxies
	 */
	public ManualProxySelector( List<Proxy> universalProxies ) {
		this.universalProxies = universalProxies;
		this.protocolSpecificProxies = null;
	}

	/**
	 * Create a manual proxy selector with the given protocol specific proxies.
	 *
	 * @param protocolSpecificProxies
	 */
	public ManualProxySelector( Map<String, List<Proxy>> protocolSpecificProxies ) {
		this.universalProxies = null;
		this.protocolSpecificProxies = protocolSpecificProxies;
	}

	/**
	 * Create a manual proxy selector with the given univeral and protocol specific proxies.
	 *
	 * @param universalProxies
	 * @param protocolSpecificProxies
	 */
	public ManualProxySelector( List<Proxy> universalProxies, Map<String, List<Proxy>> protocolSpecificProxies ) {
		this.universalProxies = universalProxies;
		this.protocolSpecificProxies = protocolSpecificProxies;
	}

	/**
	 * @see java.net.ProxySelector#select(java.net.URI)
	 */
	@Override
	public List<Proxy> select( URI uri ) {
		if ( protocolSpecificProxies != null && !protocolSpecificProxies.isEmpty() ) {
			List<Proxy> proxies = protocolSpecificProxies.get( uri.getScheme() );
			return proxies != null ? proxies : ProxySelectorUtils.getProxyListDirectAccessOnly();
		} else if ( universalProxies != null && !universalProxies.isEmpty() ) {
			return universalProxies;
		} else {
			return ProxySelectorUtils.getProxyListDirectAccessOnly();
		}
	}

	/**
	 * @see java.net.ProxySelector#connectFailed(java.net.URI, java.net.SocketAddress, java.io.IOException)
	 */
	@Override
	public void connectFailed( URI uri, SocketAddress sa, IOException ioe ) {
		// nothing to do!
		log.warn( "Could not connect to '" + uri + "' via proxy '" + sa + "' because of error '" + ioe + "'." );
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder bob = new StringBuilder( "ManualProxySelector:" );
		bob.append( " universal: " ).append( universalProxies );
		bob.append( " specific: " ).append( protocolSpecificProxies );
		return bob.toString();
	}
}
