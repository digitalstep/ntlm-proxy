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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.compeople.commons.net.proxy.win32.WinProxySelectorProvider;

/**
 * The proxy selector factory delivers an os/os-version dependend proxy selector.
 */
public class CompoundProxySelectorFactory {


    private static final Logger log = LoggerFactory.getLogger(CompoundProxySelectorFactory.class);

	/**
	 * Get the os/os-version dependend delegating proxy selector.
	 *
	 * @return
	 * @throws IllegalStateException if no selector is available or selector instantiation failed.
	 */
	public static CompoundProxySelector getProxySelector() {

		CompoundProxySelector compoundProxySelector = new CompoundProxySelector();

		// First check whether there is a manual proxy setting
		ManualProxySelectorProvider.appendTo( compoundProxySelector );

		// OS specific proxy settings
		if ( File.separatorChar == '\\' ) {
			WinProxySelectorProvider.appendTo( compoundProxySelector );
		} else {
			String os = System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" );
			log.warn( "There is no special proxy selector for the current operating system '" + os + "'." );
		}

		return compoundProxySelector;
	}

}