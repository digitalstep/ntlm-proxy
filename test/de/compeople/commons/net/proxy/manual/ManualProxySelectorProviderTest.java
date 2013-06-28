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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import junit.framework.TestCase;
import de.compeople.commons.net.proxy.CompoundProxySelector;

/**
 * Nomen est omen!
 */
public class ManualProxySelectorProviderTest extends TestCase {

	private CompoundProxySelector compoundProxySelector;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		compoundProxySelector = new CompoundProxySelector();
	}

	/**
	 * Nomen est omen!
	 * 
	 * @throws URISyntaxException
	 */
	public void testDirect() throws URISyntaxException {
		System.setProperty( ManualProxySelectorProvider.MANUAL_PROXY_SETTINGS_PROPERTY, "DIRECT" );

		ManualProxySelectorProvider.appendTo( compoundProxySelector );

		List<Proxy> proxies = compoundProxySelector.select( new URI( "http://web.de" ) );
		assertNotNull( proxies );
		assertEquals( 1, proxies.size() );
		assertEquals( Proxy.NO_PROXY, proxies.get( 0 ) );
	}

	/**
	 * Nomen est omen!
	 * 
	 * @throws URISyntaxException
	 */
	public void testIdproxy3128() throws URISyntaxException {
		System.setProperty( ManualProxySelectorProvider.MANUAL_PROXY_SETTINGS_PROPERTY, "idproxy:3128" );

		ManualProxySelectorProvider.appendTo( compoundProxySelector );

		List<Proxy> proxies = compoundProxySelector.select( new URI( "http://web.de" ) );
		assertNotNull( proxies );
		assertEquals( 2, proxies.size() );

		Proxy idproxy3128 = new Proxy( Proxy.Type.HTTP, InetSocketAddress.createUnresolved( "idproxy", 3128 ) );

		assertEquals( idproxy3128, proxies.get( 0 ) );
		assertEquals( Proxy.NO_PROXY, proxies.get( 1 ) );
	}

}
