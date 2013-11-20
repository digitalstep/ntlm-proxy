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
package de.digitalstep.ntlmproxy.nativelib;

import de.compeople.commons.net.winhttp.WinHttpAutoProxyOptions;
import de.compeople.commons.net.winhttp.WinHttpCurrentUserIEProxyConfig;
import de.compeople.commons.net.winhttp.WinHttpProxyInfo;

/**
 * This is the Win32 WinHttp wrapper. (Not yet complete, but offers what we
 * currently need!)
 */
public final class WinHttp extends NativeLibrary {

    /**
     * WinHttpOpen prettifiers for optional proxy name parameter
     */
    public static final String NO_PROXY_NAME = null;

    /**
     * WinHttpOpen prettifiers for optional proxy bypass parameter
     */
    public static final String NO_PROXY_BYPASS = null;

    private WinHttp() {
        super("/jWinHttp.dll");
    }

    /**
     * Initialize the wrapper.
     * @return 
     */
    public static WinHttp initialize() {
        WinHttp winHttp = new WinHttp();
        winHttp.load();
        return winHttp;
    }

    /**
     * WinHttpOpen - see Microsoft SDK Documentation
     * 
     * @param userAgent
     * @param accessType
     * @param proxyName
     * @param proxyBypass
     * @param flags
     * @return
     */
    public static native int open(String userAgent, int accessType, String proxyName, String proxyBypass, int flags);

    /**
     * WinHttpCloseHandle - see Microsoft SDK Documentation
     * 
     * @param hInternet
     * @return
     */
    public static native boolean closeHandle(int hInternet);

    /**
     * WinHttpGetIEProxyConfigForCurrentUser - see Microsoft SDK Documentation
     * 
     * @param proxyConfig
     * @return
     */
    public static native boolean getIEProxyConfigForCurrentUser(WinHttpCurrentUserIEProxyConfig proxyConfig);

    /**
     * WinHttpGetProxyForUrl - see Microsoft SDK Documentation
     * 
     * @param hSession
     * @param url
     * @param autoProxyOptions
     * @param proxyInfo
     * @return
     */
    public static native boolean getProxyForUrl(int hSession, String url, WinHttpAutoProxyOptions autoProxyOptions,
            WinHttpProxyInfo proxyInfo);

    /**
     * GetLastError - see Microsoft SDK Documentation
     * 
     * @return
     */
    public static native int getLastError();

    /**
     * GetLastErrorMessage - formats the last error
     * 
     * @return
     */
    public static native String getLastErrorMessage();

}
