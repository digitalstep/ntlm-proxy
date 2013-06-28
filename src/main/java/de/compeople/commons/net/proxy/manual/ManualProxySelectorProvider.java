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

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.compeople.commons.net.proxy.CompoundProxySelector;
import de.compeople.commons.net.proxy.util.ProxySelectorUtils;
import de.compeople.commons.util.StringUtils;

/**
 * Provide the manual proxy settings if given.
 */
public final class ManualProxySelectorProvider {

	/**
	 * If the value of manual proxy settings property is DIRECT than the
	 * manual proxy selector will select no proxy!
	 */
	public static final String DIRECT = "DIRECT";

	/**
	 * The system property for defining a manual proxy setting.
	 */
	public final static String MANUAL_PROXY_SETTINGS_PROPERTY = "commons.proxy";


    private static final Logger log = LoggerFactory.getLogger(ManualProxySelectorProvider.class);

	/**
	 * Append a manual proxy selector to the CompoundProxySelector
	 *
	 * @param compoundProxySelector
	 */
	public static void appendTo( CompoundProxySelector compoundProxySelector ) {
		String proxyList = System.getProperty( MANUAL_PROXY_SETTINGS_PROPERTY );
		if ( StringUtils.isGiven( proxyList ) ) {
			List<Proxy> universalProxies = new ArrayList<Proxy>();
			Map<String, List<Proxy>> protocolSpecificProxies = new HashMap<String, List<Proxy>>();

			if ( !proxyList.equalsIgnoreCase( DIRECT ) ) {
				// if not �direct� requested fill the list from the spec.
				ProxySelectorUtils.fillProxyLists( proxyList, universalProxies, protocolSpecificProxies );
			}

			ManualProxySelector manualProxySelector = new ManualProxySelector( universalProxies, protocolSpecificProxies );

			compoundProxySelector.addOrReplace( 100, manualProxySelector );
		} else {
				log.debug("No manual proxy (-D{}=... )selector requested.", MANUAL_PROXY_SETTINGS_PROPERTY);
		}
	}
}
