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
package de.compeople.commons.net.proxy;

import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.compeople.commons.net.proxy.ManualProxySelector;

/**
 * Nomen est omen!
 */
public class ManualProxySelectorTest {

	private Proxy proxy1 = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( "test1", 8080 ) );
	private Proxy proxy2 = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( "test2", 8080 ) );
	private Proxy proxy3 = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( "test3", 8080 ) );
	private Proxy proxy4 = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( "test4", 8080 ) );

	/**
	 * Nomen est omen!
	 * @throws URISyntaxException
	 */
	public void testDirect() throws URISyntaxException {
		ManualProxySelector selector = new ManualProxySelector();
		URI uri = new URI( "http://web.de" );
		List<Proxy> proxies = selector.select( uri );
		assertEquals( 1, proxies.size() );
		assertEquals( Proxy.NO_PROXY, proxies.get( 0 ) );
	}

	/**
	 * Nomen est omen!
	 * @throws URISyntaxException
	 */
	public void testUniversal() throws URISyntaxException {
		List<Proxy> universal = new ArrayList<Proxy>();
		universal.add( proxy1 );
		universal.add( proxy2 );
		ManualProxySelector selector = new ManualProxySelector( universal );
		URI uri = new URI( "http://web.de" );
		List<Proxy> proxies = selector.select( uri );
		assertEquals( 2, proxies.size() );
		assertEquals( universal.get( 0 ), proxies.get( 0 ) );
		assertEquals( universal.get( 1 ), proxies.get( 1 ) );
	}

	/**
	 * Nomen est omen!
	 * @throws URISyntaxException
	 */
	public void testSpecific() throws URISyntaxException {
		List<Proxy> specificProxies = new ArrayList<Proxy>();
		specificProxies.add( proxy1 );
		specificProxies.add( proxy2 );
		Map<String, List<Proxy>> specific = new HashMap<String, List<Proxy>>();
		specific.put( "http", specificProxies );
		ManualProxySelector selector = new ManualProxySelector( specific );
		URI uri = new URI( "http://web.de" );
		List<Proxy> proxies = selector.select( uri );
		assertEquals( 2, proxies.size() );
		assertEquals( specificProxies.get( 0 ), proxies.get( 0 ) );
		assertEquals( specificProxies.get( 1 ), proxies.get( 1 ) );

		uri = new URI( "socks://web.de" );
		proxies = selector.select( uri );
		assertEquals( 1, proxies.size() );
	}

	/**
	 * Nomen est omen!
	 * @throws URISyntaxException
	 */
	public void testUniversalAndSpecific1() throws URISyntaxException {
		List<Proxy> universalProxies = new ArrayList<Proxy>();
		universalProxies.add( proxy1 );
		universalProxies.add( proxy2 );

		List<Proxy> specificProxies = new ArrayList<Proxy>();
		specificProxies.add( proxy3 );
		specificProxies.add( proxy4 );
		Map<String, List<Proxy>> specific = new HashMap<String, List<Proxy>>();
		specific.put( "http", specificProxies );
		ManualProxySelector selector = new ManualProxySelector( universalProxies, specific );
		URI uri = new URI( "http://web.de" );
		List<Proxy> proxies = selector.select( uri );
		assertEquals( 2, proxies.size() );
		assertEquals( specificProxies.get( 0 ), proxies.get( 0 ) );
		assertEquals( specificProxies.get( 1 ), proxies.get( 1 ) );

		uri = new URI( "socks://web.de" );
		proxies = selector.select( uri );
		assertEquals( 1, proxies.size() );
	}

	/**
	 * Nomen est omen!
	 * @throws URISyntaxException
	 */
	public void testUniversalAndSpecific2a() throws URISyntaxException {
		List<Proxy> universalProxies = new ArrayList<Proxy>();
		universalProxies.add( proxy1 );
		universalProxies.add( proxy2 );

		Map<String, List<Proxy>> specific = new HashMap<String, List<Proxy>>();
		ManualProxySelector selector = new ManualProxySelector( universalProxies, specific );
		URI uri = new URI( "http://web.de" );
		List<Proxy> proxies = selector.select( uri );
		assertEquals( 2, proxies.size() );
		assertEquals( universalProxies.get( 0 ), proxies.get( 0 ) );
		assertEquals( universalProxies.get( 1 ), proxies.get( 1 ) );
	}

	/**
	 * Nomen est omen!
	 * @throws URISyntaxException
	 */
	public void testUniversalAndSpecific2b() throws URISyntaxException {
		List<Proxy> universalProxies = new ArrayList<Proxy>();
		universalProxies.add( proxy1 );
		universalProxies.add( proxy2 );

		ManualProxySelector selector = new ManualProxySelector( universalProxies, null );
		URI uri = new URI( "http://web.de" );
		List<Proxy> proxies = selector.select( uri );
		assertEquals( 2, proxies.size() );
		assertEquals( universalProxies.get( 0 ), proxies.get( 0 ) );
		assertEquals( universalProxies.get( 1 ), proxies.get( 1 ) );
	}

	/**
	 * Nomen est omen!
	 * @throws URISyntaxException
	 */
	public void testUniversalAndSpecific3a() throws URISyntaxException {
		List<Proxy> universalProxies = new ArrayList<Proxy>();

		ManualProxySelector selector = new ManualProxySelector( universalProxies, null );
		URI uri = new URI( "http://web.de" );
		List<Proxy> proxies = selector.select( uri );
		assertEquals( 1, proxies.size() );
		assertEquals( Proxy.NO_PROXY, proxies.get( 0 ) );
	}

	/**
	 * Nomen est omen!
	 * @throws URISyntaxException
	 */
	public void testUniversalAndSpecific3b() throws URISyntaxException {
		List<Proxy> universalProxies = new ArrayList<Proxy>();
		Map<String, List<Proxy>> specific = new HashMap<String, List<Proxy>>();

		ManualProxySelector selector = new ManualProxySelector( universalProxies, specific );
		URI uri = new URI( "http://web.de" );
		List<Proxy> proxies = selector.select( uri );
		assertEquals( 1, proxies.size() );
		assertEquals( Proxy.NO_PROXY, proxies.get( 0 ) );
	}

	/**
	 * Nomen est omen!
	 * @throws URISyntaxException
	 */
	public void testUniversalAndSpecific3c() throws URISyntaxException {
		ManualProxySelector selector = new ManualProxySelector( null, null );
		URI uri = new URI( "http://web.de" );
		List<Proxy> proxies = selector.select( uri );
		assertEquals( 1, proxies.size() );
		assertEquals( Proxy.NO_PROXY, proxies.get( 0 ) );
	}
}
