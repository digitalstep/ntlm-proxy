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
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;
import de.compeople.commons.util.nativelib.NativeLibraryLoadException;

/**
 * Nomen est omen!
 */
public class TestWinHttpProxySelectorsSpeed extends TestCase {

	private final static int LOOPS = 5;

	/**
	 * Nomen est omen!
	 * @throws NativeLibraryLoadException 
	 * @throws URISyntaxException 
	 */
	public void testWinHttpProxySelectorSpeed() throws NativeLibraryLoadException, URISyntaxException {

		long begin, end, duration = 0;
		ProxySelector proxySelector = new WinHttpProxySelector();
		URI uri = new URI( "http://localhost" );
		for ( int i = 0; i < LOOPS; i++ ) {
			begin = System.nanoTime();//currentTimeMillis();
			try {
				proxySelector.select( uri );
			} catch ( Exception e ) {
				// :-(
			}
			end = System.nanoTime();//currentTimeMillis();
			duration += end - begin;
		}

		System.out.println( "WinHttpProxySelector:" );
		System.out.println( "Average time: " + (double) duration / (double) LOOPS + " nanos" );
	}

}
