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
package de.compeople.commons.net.proxy.win32;

import java.net.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.compeople.commons.net.proxy.util.ProxyBypass;
import de.compeople.commons.net.proxy.util.ProxySelectorUtils;
import de.compeople.commons.net.winhttp.WinHttpCurrentUserIEProxyConfig;

/**
 * Helper
 */
public class WinHttpConfig {

	private List<Proxy> universalProxies = new ArrayList<Proxy>();
	private Map<String, List<Proxy>> protocolSpecificProxies = new HashMap<String, List<Proxy>>();
	private ProxyBypass proxyBypass;

	/**
	 * @param proxyConfig
	 */
	public WinHttpConfig( WinHttpCurrentUserIEProxyConfig proxyConfig ) {
		ProxySelectorUtils.fillProxyLists( proxyConfig.getProxy(), universalProxies, protocolSpecificProxies );
		proxyBypass = new ProxyBypass( proxyConfig.getProxyBypass() );
	}

	public boolean useUniversalProxy() {
		return !universalProxies.isEmpty();
	}

	public boolean useProtocolSpecificProxies() {
		return !protocolSpecificProxies.isEmpty();
	}

	public List<Proxy> getProtocolSpecificProxies( URI uri ) {
		return protocolSpecificProxies.get( uri.getScheme() );
	}

	public List<Proxy> getUniversalProxies() {
		return universalProxies;
	}

	public boolean bypassProxyFor( URI uri ) {
		return proxyBypass.bypassProxyFor( uri );
	}
}
