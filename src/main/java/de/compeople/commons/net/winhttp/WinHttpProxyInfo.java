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

/**
 * Wrapper for Win32 WINHTTP_PROXY_INFO Structure
 */
public class WinHttpProxyInfo {

	// WinHttpOpen dwAccessType values (also for WINHTTP_PROXY_INFO::dwAccessType)
	public static final int WINHTTP_ACCESS_TYPE_DEFAULT_PROXY = 0;
	public static final int WINHTTP_ACCESS_TYPE_NO_PROXY = 1;
	public static final int WINHTTP_ACCESS_TYPE_NAMED_PROXY = 3;

	private int accessType;
	private String proxy;
	private String proxyBypass;

	/**
	 * @return the accessType
	 */
	public int getAccessType() {
		return accessType;
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

}
