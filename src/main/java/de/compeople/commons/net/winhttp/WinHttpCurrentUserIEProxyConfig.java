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
package de.compeople.commons.net.winhttp;

import com.google.common.base.Objects;

/**
 * Wrapper for Win32 WINHTTP_CURRENT_USER_IE_PROXY_CONFIG structure
 */
public class WinHttpCurrentUserIEProxyConfig {

    private boolean isAutoDetect;
    private String autoConfigUrl;
    private String proxy;
    private String proxyBypass;

    /**
     * @return the autoConfigUrl
     */
    public String getAutoConfigUrl() {
        return autoConfigUrl;
    }

    /**
     * @return the isAutoDetect
     */
    public boolean isAutoDetect() {
        return isAutoDetect;
    }

    /**
     * @return the proxy
     */
    public String getProxy() {
        return proxy;
    }

    /**
     * @return the proxyBypass
     */
    public String getProxyBypass() {
        return proxyBypass;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WinHttpCurrentUserIEProxyConfig) {
            WinHttpCurrentUserIEProxyConfig that = (WinHttpCurrentUserIEProxyConfig) obj;
            return (this.isAutoDetect == that.isAutoDetect)
                    && Objects.equal(this.autoConfigUrl, that.autoConfigUrl)
                    && Objects.equal(this.proxy, that.proxy) 
                    && Objects.equal(this.proxyBypass, that.proxyBypass);
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (autoConfigUrl + proxy).hashCode();
    }

}
