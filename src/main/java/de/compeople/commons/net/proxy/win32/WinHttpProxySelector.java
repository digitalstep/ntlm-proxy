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

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.base.Strings;

import de.compeople.commons.net.proxy.util.ProxyBypass;
import de.compeople.commons.net.proxy.util.ProxySelectorPACUtils;
import de.compeople.commons.net.proxy.util.ProxySelectorUtils;
import de.compeople.commons.net.winhttp.WinHttp;
import de.compeople.commons.net.winhttp.WinHttpAutoProxyOptions;
import de.compeople.commons.net.winhttp.WinHttpCurrentUserIEProxyConfig;
import de.compeople.commons.net.winhttp.WinHttpProxyInfo;

/**
 * ProxySelector that gets its settings from the
 * "internet options >> connection settings"
 */
public class WinHttpProxySelector extends ProxySelector {
    
    @SuppressWarnings("unused")
    private static final WinHttp WIN_HTTP = WinHttp.initialize();

    private WinHttpCurrentUserIEProxyConfig proxyConfig = null;
    private WinHttpConfig winHttpConfig;
    private boolean pacFailed = false;
    private boolean wpadFailed = false;
    private List<Proxy> wpadProxies = null;
    private Set<Proxy> failed = new HashSet<Proxy>();

    private final static int ERROR_WINHTTP_AUTODETECTION_FAILED = 12180;
    private static final String MY_NAME = WinHttpProxySelector.class.getSimpleName();
    private static final Logger LOGGER = Logger.getLogger(WinHttpProxySelector.class.getName());

    /**
     * Default constructor
     */
    public WinHttpProxySelector() {
    }

    @Override
    public List<Proxy> select(URI uri) {
        WinHttpCurrentUserIEProxyConfig newProxyConfig = new WinHttpCurrentUserIEProxyConfig();
        if (!WinHttp.getIEProxyConfigForCurrentUser(newProxyConfig)) {
            LOGGER
                    .warning("WinHttp.GetIEProxyConfigForCurrentUser failed with error '" + WinHttp.getLastErrorMessage() + "' #"
                            + WinHttp.getLastError() + ".");
            return ProxySelectorUtils.getEmptyProxyList();
        }

        // Let's see if we are still up-to-date.
        boolean proxyConfigChanged = !newProxyConfig.equals(proxyConfig);
        if (proxyConfigChanged) {
            LOGGER.finest("Initializing.");
            proxyConfig = newProxyConfig;
            // Retry pac and wpad
            pacFailed = false;
            wpadFailed = false;
            wpadProxies = ProxySelectorUtils.getEmptyProxyList();
        }

        List<Proxy> proxies = new ArrayList<Proxy>();

        // Explicit proxies defined?
        if (!Strings.isNullOrEmpty(proxyConfig.getProxy())) {
            // Yes, let's see if we are still up-to-date or not yet initialized.
            if (proxyConfigChanged || winHttpConfig == null) {
                winHttpConfig = new WinHttpConfig(proxyConfig);
            }

            if (!winHttpConfig.bypassProxyFor(uri)) {
                if (winHttpConfig.useProtocolSpecificProxies()) {
                    proxies.addAll(winHttpConfig.getProtocolSpecificProxies(uri));
                } else {
                    proxies.addAll(winHttpConfig.getUniversalProxies());
                }
            }
        }

        boolean isPac = proxyConfig.getAutoConfigUrl() != null;
        boolean isWpad = proxyConfig.isAutoDetect();

        if (isPac || isWpad) {
            // Create the WinHTTP session.
            int hHttpSession = WinHttp.open(MY_NAME, WinHttpProxyInfo.WINHTTP_ACCESS_TYPE_NO_PROXY, WinHttp.NO_PROXY_NAME,
                    WinHttp.NO_PROXY_BYPASS, 0);
            if (hHttpSession == 0) {
                LOGGER.warning("WinHttp.Open failed with error'" + WinHttp.getLastErrorMessage() + "' #" + WinHttp.getLastError() + ".");
            } else {
                try {
                    // PAC file?
                    if (isPac && !pacFailed) {
                        proxies.addAll(pacSelect(hHttpSession, uri));
                    }

                    // WPAD?
                    if (isWpad && !wpadFailed) {
                        if (wpadProxies == null || wpadProxies.size() == 0) {
                            wpadProxies = wpadSelect(hHttpSession, uri);
                        }
                        proxies.addAll(wpadProxies);
                    }
                } finally {
                    WinHttp.closeHandle(hHttpSession);
                }
            }
        }

        resort(proxies);
        return proxies;
    }

    /**
     * Resort the proxies such that we shuffle the failed proxy to the end of
     * the list.
     * 
     * @param proxies
     */
    private void resort(List<Proxy> proxies) {
        if (!proxies.contains(Proxy.NO_PROXY)) {
            proxies.add(Proxy.NO_PROXY);
        }
        if (failed.isEmpty()) {
            return;
        }
        proxies.removeAll(failed);
        proxies.addAll(failed);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        LOGGER.finest("Connect failed for " + uri + " on " + sa + ".");
        if (uri == null) {
            return;
        }
        failed.add(ProxySelectorUtils.createProxy(uri.getScheme(), sa));
    }

    protected List<Proxy> pacSelect(int hHttpSession, URI uri) {
        // Set up the autoproxy call.
        WinHttpAutoProxyOptions autoProxyOptions = new WinHttpAutoProxyOptions();
        autoProxyOptions.setFlags(WinHttpAutoProxyOptions.WINHTTP_AUTOPROXY_CONFIG_URL);
        autoProxyOptions.setAutoConfigUrl(proxyConfig.getAutoConfigUrl());
        autoProxyOptions.setAutoLogonIfChallenged(true);
        WinHttpProxyInfo proxyInfo = new WinHttpProxyInfo();

        boolean ok = WinHttp.getProxyForUrl(hHttpSession, uri.toString(), autoProxyOptions, proxyInfo);
        if (!ok) {
            pacFailed = true;
            LOGGER.warning("WinHttp.GetProxyForUrl for pac failed with error '" + WinHttp.getLastErrorMessage() + "' #"
                    + WinHttp.getLastError() + ".");
            return ProxySelectorUtils.getEmptyProxyList();
        }
        ProxyBypass proxyBypass = new ProxyBypass(proxyInfo.getProxyBypass());
        if (proxyBypass.bypassProxyFor(uri)) {
            return ProxySelectorUtils.getProxyListDirectAccessOnly();
        } else {
            return ProxySelectorPACUtils.getProxies(proxyInfo.getProxy());
        }
    }

    protected List<Proxy> wpadSelect(int hHttpSession, URI uri) {
        // Set up the autoproxy call.
        WinHttpAutoProxyOptions autoProxyOptions = new WinHttpAutoProxyOptions();
        autoProxyOptions.setFlags(WinHttpAutoProxyOptions.WINHTTP_AUTOPROXY_AUTO_DETECT);
        autoProxyOptions.setAutoDetectFlags(WinHttpAutoProxyOptions.WINHTTP_AUTO_DETECT_TYPE_DHCP
                | WinHttpAutoProxyOptions.WINHTTP_AUTO_DETECT_TYPE_DNS_A);
        autoProxyOptions.setAutoLogonIfChallenged(true);
        WinHttpProxyInfo proxyInfo = new WinHttpProxyInfo();

        boolean ok = WinHttp.getProxyForUrl(hHttpSession, uri.toString(), autoProxyOptions, proxyInfo);
        if (!ok) {
            wpadFailed = WinHttp.getLastError() == ERROR_WINHTTP_AUTODETECTION_FAILED;
            LOGGER.warning("WinHttp.GetProxyForUrl for wpad failed with error '" + WinHttp.getLastErrorMessage() + "' #"
                    + WinHttp.getLastError() + ".");
            return ProxySelectorUtils.getEmptyProxyList();
        }
        ProxyBypass proxyBypass = new ProxyBypass(proxyInfo.getProxyBypass());
        if (proxyBypass.bypassProxyFor(uri)) {
            return ProxySelectorUtils.getProxyListDirectAccessOnly();
        } else {
            return ProxySelectorPACUtils.getProxies(proxyInfo.getProxy());
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return MY_NAME;
    }

}
