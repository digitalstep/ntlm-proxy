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
 * Wrapper for Win32 WINHTTP_AUTOPROXY_OPTIONS Structure
 */
public class WinHttpAutoProxyOptions {

	// Flags for WINHTTP_AUTOPROXY_OPTIONS::dwFlags
	public static final int WINHTTP_AUTOPROXY_AUTO_DETECT = 0x00000001;
	public static final int WINHTTP_AUTOPROXY_CONFIG_URL = 0x00000002;
	public static final int WINHTTP_AUTOPROXY_RUN_INPROCESS = 0x00010000;
	public static final int WINHTTP_AUTOPROXY_RUN_OUTPROCESS_ONLY = 0x00020000;

	// Flags for WINHTTP_AUTOPROXY_OPTIONS::dwAutoDetectFlags 
	public static final int WINHTTP_AUTO_DETECT_TYPE_DHCP = 0x00000001;
	public static final int WINHTTP_AUTO_DETECT_TYPE_DNS_A = 0x00000002;

	int flags;
	int autoDetectFlags;
	String autoConfigUrl;
	String reservedPointer;
	int reservedInt;
	boolean autoLogonIfChallenged;

	/**
	 * @param autoConfigUrl the autoConfigUrl to set
	 */
	public void setAutoConfigUrl( String autoConfigUrl ) {
		this.autoConfigUrl = autoConfigUrl;
	}

	/**
	 * @param autoDetectFlags the autoDetectFlags to set
	 */
	public void setAutoDetectFlags( int autoDetectFlags ) {
		this.autoDetectFlags = autoDetectFlags;
	}

	/**
	 * @param autoLogonIfChallenged the autoLogonIfChallenged to set
	 */
	public void setAutoLogonIfChallenged( boolean autoLogonIfChallenged ) {
		this.autoLogonIfChallenged = autoLogonIfChallenged;
	}

	/**
	 * @param flags the flags to set
	 */
	public void setFlags( int flags ) {
		this.flags = flags;
	}

}
