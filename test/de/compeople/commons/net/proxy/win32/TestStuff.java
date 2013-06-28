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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JEditorPane;

import de.compeople.commons.net.proxy.CompoundProxySelector;
import de.compeople.commons.net.proxy.CompoundProxySelectorFactory;
import de.compeople.commons.net.winhttp.WinHttp;
import de.compeople.commons.util.nativelib.NativeLibraryLoadException;

/**
 * This little swing (!) app displays continously the html content of a web site.
 * While running changing of the internet options/connection settings
 * shows immediatly the effect of this changes. 
 */
public class TestStuff {

	/**
	 * @param args
	 * @throws NativeLibraryLoadException 
	 * @throws DelegatingProxySelectorFactoryException 
	 * @throws IOException 
	 */
	public static void main( String[] args ) throws NativeLibraryLoadException {

		WinHttp.initialize();
		System.out.println( "---> " + WinHttp.getLastErrorMessage() );

		CompoundProxySelector selector = CompoundProxySelectorFactory.getProxySelector();
		selector.install();
		Frame frame = new Frame();
		frame.setLayout( new BorderLayout() );
		JEditorPane pane = new JEditorPane();
		frame.add( pane, BorderLayout.CENTER );
		frame.pack();
		frame.setVisible( true );

		//		for ( int i = 0; i < 5; i++ ) {
		int counter = 0;
		while ( true ) {
			try {
				URL url = new URL( "http://www.nichts.de/" );
				pane.setPage( url );
				InputStream inputStream = url.openStream();
				int c;
				while ( ( c = inputStream.read() ) != -1 ) {
					System.out.print( (char) c );
				}
				inputStream.close();
				counter++;
			} catch ( Throwable t ) {
				System.err.println( "Oops (" + counter + "): " + t );
			}
		}
	}

}
