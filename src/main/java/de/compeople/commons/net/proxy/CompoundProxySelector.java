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

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.compeople.commons.net.proxy.util.ProxySelectorUtils;

/**
 * A proxy selector that collects proxies from various other proxy selectors and
 * uses all of their proxies for selection.
 */
public class CompoundProxySelector extends ProxySelector {

    private TreeMap<Integer, ProxySelector> proxySelectors = new TreeMap<Integer, ProxySelector>();
    private ProxySelector savedProxySelector = null;
    private Map<Proxy, List<ProxySelector>> proxyToProxySelectors = new HashMap<Proxy, List<ProxySelector>>();

    private static final Logger log = LoggerFactory.getLogger(CompoundProxySelector.class);

    /**
	 *
	 */
    public CompoundProxySelector() {
        super();
        savedProxySelector = ProxySelector.getDefault();
        if (savedProxySelector != null) {
            // plus the original default selector at the end
            addOrReplace(Integer.MAX_VALUE, savedProxySelector);
        }
    }

    /**
     * @param priority
     * @param proxySelector
     * @return
     */
    public ProxySelector addOrReplace(int priority, ProxySelector proxySelector) {
        return proxySelectors.put(priority, proxySelector);
    }

    /**
     * Installs this compound proxy selector.
     */
    public void install() {
        if (log.isInfoEnabled()) {
            log.info("Installing compound proxy selector with:");
            for (Map.Entry<Integer, ProxySelector> entry : proxySelectors.entrySet()) {
                log.info(" - prio " + entry.getKey() + " " + entry.getValue());
            }
        }
        ProxySelector.setDefault(this);
    }

    /**
     * Deinstall this proxy selector and install the original proxy selector.
     */
    public void deinstall() {
        if (savedProxySelector != null) {
            ProxySelector.setDefault(savedProxySelector);
        } else {
            try {
                Class<?> c = Class.forName("sun.net.spi.DefaultProxySelector");
                if (c != null && ProxySelector.class.isAssignableFrom(c)) {
                    ProxySelector.setDefault((ProxySelector) c.newInstance());
                }
            } catch (Throwable t) {
                ProxySelector.setDefault(null);
            }
        }
    }

    /**
     * @see java.net.ProxySelector#select(java.net.URI)
     */
    @Override
    public List<Proxy> select(URI uri) {

        if (uri == null) {
            throw new IllegalArgumentException("uri must not be null");
        }
        List<Proxy> proxies = new ArrayList<Proxy>();
        proxyToProxySelectors.clear();

        for (ProxySelector selector : proxySelectors.values()) {
            log.debug("Checking selector: {}", selector.toString());

            List<Proxy> result;
            if (uri.getScheme() == null || uri.getHost() == null) {
                result = selector.select(URI.create("http://" + uri.toString()));
            } else {
                result = selector.select(uri);
            }
            associateProxiesWithSelector(selector, result);
            if (!append(proxies, result)) {
                log.debug("Skipping proxy selector '{}'.", selector);
            }
        }

        // plus if necessary the no-proxy
        if (proxies.isEmpty() || !proxies.contains(Proxy.NO_PROXY)) {
            proxies.add(Proxy.NO_PROXY);
        }

        return proxies;
    }

    private void associateProxiesWithSelector(ProxySelector selector, List<Proxy> proxies) {
        for (Proxy proxy : proxies) {
            List<ProxySelector> selectors = proxyToProxySelectors.get(proxy);
            if (selectors == null) {
                selectors = new ArrayList<ProxySelector>();
                proxyToProxySelectors.put(proxy, selectors);
            }
            selectors.add(selector);
        }
    }

    /**
     * @param result
     * @param append
     * @return true something has been added; ortherwise false
     */
    private boolean append(List<Proxy> result, List<Proxy> append) {
        boolean added = false;
        for (Proxy proxy : append) {
            if (!result.contains(proxy)) {
                result.add(proxy);
                added = true;
            }
        }
        return added;
    }

    /**
     * @see java.net.ProxySelector#connectFailed(java.net.URI,
     *      java.net.SocketAddress, java.io.IOException)
     */
    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        log.warn("proxy '" + sa + "' not available for url '" + uri + "'.", ioe);
        List<ProxySelector> selectors = proxyToProxySelectors.get(ProxySelectorUtils.createProxy(uri.getScheme(), sa));
        if (selectors != null) {
            for (ProxySelector selector : selectors) {
                selector.connectFailed(uri, sa, ioe);
            }
        }
    }

}
