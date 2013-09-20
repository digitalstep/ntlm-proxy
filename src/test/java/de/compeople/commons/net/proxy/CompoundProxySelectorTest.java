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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.compeople.commons.net.proxy.manual.ManualProxySelector;

/**
 * Test CompoundProxySelector
 */
public class CompoundProxySelectorTest {

    private URI uri;
    private List<Proxy> universal;

    @Before
    public void setUp() throws Exception {
        uri = new URI("http://www.compeople.de");
        universal = new ArrayList<Proxy>();
        universal.add(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("www.w3c.org", 80)));
    }

    /**
     * Nomen est omen!
     */
    @Test
    public void testProxyListNoSelectorContainsNoProxy() {
        CompoundProxySelector selector = new CompoundProxySelector();
        List<Proxy> proxies = selector.select(uri);
        assertEquals(1, proxies.size());
        assertEquals(Proxy.NO_PROXY, proxies.get(0));
    }

    /**
     * Nomen est omen!
     */
    public void testProxyListSelectorContainsOneProxy() {
        CompoundProxySelector selector = new CompoundProxySelector();
        selector.addOrReplace(100, new ManualProxySelector(universal, new HashMap<String, List<Proxy>>()));
        List<Proxy> proxies = selector.select(uri);
        assertEquals(2, proxies.size());
        assertEquals(universal.get(0), proxies.get(0));
        assertEquals(Proxy.NO_PROXY, proxies.get(1));
    }

    /**
     * Nomen est omen!
     */
    public void testProxyListSelectorContainsOneProxyAndNoProxy() {
        CompoundProxySelector selector = new CompoundProxySelector();
        List<Proxy> universalPlus = new ArrayList<Proxy>();
        universalPlus.addAll(universal);
        universalPlus.add(Proxy.NO_PROXY);
        selector.addOrReplace(100, new ManualProxySelector(universalPlus, new HashMap<String, List<Proxy>>()));
        List<Proxy> proxies = selector.select(uri);
        assertEquals(2, proxies.size());
        assertEquals(universal.get(0), proxies.get(0));
        assertEquals(Proxy.NO_PROXY, proxies.get(1));
    }

    /**
     * Nomen est omen!
     */
    public void testProxyListSelectorContainsNoProxyAndOneProxyAndNoProxy() {
        CompoundProxySelector selector = new CompoundProxySelector();
        List<Proxy> universalPlus = new ArrayList<Proxy>();
        universalPlus.add(Proxy.NO_PROXY);
        universalPlus.addAll(universal);
        universalPlus.add(Proxy.NO_PROXY);
        selector.addOrReplace(100, new ManualProxySelector(universalPlus, new HashMap<String, List<Proxy>>()));
        List<Proxy> proxies = selector.select(uri);
        assertEquals(2, proxies.size());
        assertEquals(universal.get(0), proxies.get(1));
        assertEquals(Proxy.NO_PROXY, proxies.get(0));
    }

    /**
     * Nomen est omen!
     */
    public void testProxyListDoubleSelectorDifferentPriosContainsNoProxyAndOneProxyAndNoProxy() {
        CompoundProxySelector selector = new CompoundProxySelector();
        List<Proxy> universalPlus = new ArrayList<Proxy>();
        universalPlus.add(Proxy.NO_PROXY);
        universalPlus.addAll(universal);
        universalPlus.add(Proxy.NO_PROXY);
        selector.addOrReplace(100, new ManualProxySelector(universalPlus, new HashMap<String, List<Proxy>>()));
        selector.addOrReplace(200, new ManualProxySelector(universalPlus, new HashMap<String, List<Proxy>>()));
        List<Proxy> proxies = selector.select(uri);
        assertEquals(2, proxies.size());
        assertEquals(universal.get(0), proxies.get(1));
        assertEquals(Proxy.NO_PROXY, proxies.get(0));
    }

    /**
     * Nomen est omen!
     */
    public void testProxyListDoubleSelectorSamePriosContainsNoProxyAndOneProxyAndNoProxy() {
        CompoundProxySelector selector = new CompoundProxySelector();
        List<Proxy> universalPlus = new ArrayList<Proxy>();
        universalPlus.add(Proxy.NO_PROXY);
        universalPlus.addAll(universal);
        universalPlus.add(Proxy.NO_PROXY);
        selector.addOrReplace(100, new ManualProxySelector(universalPlus, new HashMap<String, List<Proxy>>()));
        List<Proxy> universalPlusPlus = new ArrayList<Proxy>();
        universalPlusPlus.addAll(universal);
        universalPlusPlus.add(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("www.web.de", 80)));
        selector.addOrReplace(100, new ManualProxySelector(universalPlusPlus, new HashMap<String, List<Proxy>>()));
        List<Proxy> proxies = selector.select(uri);
        assertEquals(3, proxies.size());
        assertEquals(universalPlusPlus.get(0), proxies.get(0));
        assertEquals(universalPlusPlus.get(1), proxies.get(1));
        assertEquals(Proxy.NO_PROXY, proxies.get(2));
    }

    /**
     * Nomen est omen!
     */
    public void testProxyListDoubleSelectorDifferentPriosContainsNoProxyAndOneProxyAndNoProxyAndMore() {
        CompoundProxySelector selector = new CompoundProxySelector();
        List<Proxy> universalPlus = new ArrayList<Proxy>();
        universalPlus.add(Proxy.NO_PROXY);
        universalPlus.addAll(universal);
        universalPlus.add(Proxy.NO_PROXY);
        selector.addOrReplace(100, new ManualProxySelector(universalPlus, new HashMap<String, List<Proxy>>()));
        List<Proxy> universalPlusPlus = new ArrayList<Proxy>();
        universalPlusPlus.addAll(universal);
        universalPlusPlus.add(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("www.web.de", 80)));
        selector.addOrReplace(200, new ManualProxySelector(universalPlusPlus, new HashMap<String, List<Proxy>>()));
        List<Proxy> proxies = selector.select(uri);
        assertEquals(3, proxies.size());
        assertEquals(Proxy.NO_PROXY, proxies.get(0));
        assertEquals(universal.get(0), proxies.get(1));
        assertEquals(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("www.web.de", 80)), proxies.get(2));
    }
}
