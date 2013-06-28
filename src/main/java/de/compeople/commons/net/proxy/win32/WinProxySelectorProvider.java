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

import java.net.ProxySelector;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.compeople.commons.net.proxy.CompoundProxySelector;
import de.compeople.commons.util.nativelib.NativeLibraryLoadException;

/**
 * Populate all possible win proxy selectors. 
 */
public final class WinProxySelectorProvider {

	private static final Logger LOGGER = Logger.getLogger( WinProxySelectorProvider.class.getName() );

	private WinProxySelectorProvider() {
	// utility class
	}

	/**
	 * Populate the windows specific proxy selectors to the given delegating proxy selector.
	 *  
	 * @param compoundProxySelector
	 * @return true, if there was somthing to append, otherwise false
	 */
	public static boolean appendTo( CompoundProxySelector compoundProxySelector ) {
		ProxySelector proxySelector;
		try {
			proxySelector = new WinHttpProxySelector();
			compoundProxySelector.addOrReplace( 1000, proxySelector );

			return true;
		} catch ( NativeLibraryLoadException nlle ) {
			LOGGER.log( Level.WARNING, "Could not load required native library.", nlle );
			return false;
		}
	}

}
