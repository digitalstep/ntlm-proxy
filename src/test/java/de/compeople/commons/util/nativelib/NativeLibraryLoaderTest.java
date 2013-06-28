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
package de.compeople.commons.util.nativelib;

import java.io.File;

import junit.framework.TestCase;

/**
 * Nomen est omen!
 */
public class NativeLibraryLoaderTest extends TestCase {

	/**
	 * Nomen est omen!
	 */
	public void testSomeLoadingBehaviourOfTheJVM() {

		if ( File.separatorChar == '\\' ) {
			String user32dll = System.getenv( "windir" ) + "\\system32\\user32.dll";
			System.load( user32dll );
			System.load( user32dll );
			System.loadLibrary( "user32" );
			System.loadLibrary( "user32" );
		} else {
			fail( "Tests for none windows environments are missing!" );
		}
		// no failure until here!!
	}

	/**
	 * Nomen est omen!
	 */
	public void testAddNativePaths() {
		NativeLibraryLoader.getNativePaths().clear();
		NativeLibraryLoader.addNativePaths( null );
		assertEquals( 0, NativeLibraryLoader.getNativePaths().size() );

		NativeLibraryLoader.getNativePaths().clear();
		NativeLibraryLoader.addNativePaths( "" );
		assertEquals( 0, NativeLibraryLoader.getNativePaths().size() );

		NativeLibraryLoader.getNativePaths().clear();
		NativeLibraryLoader.addNativePaths( ";" );
		assertEquals( 0, NativeLibraryLoader.getNativePaths().size() );

		NativeLibraryLoader.getNativePaths().clear();
		NativeLibraryLoader.addNativePaths( ";;" );
		assertEquals( 0, NativeLibraryLoader.getNativePaths().size() );

		NativeLibraryLoader.getNativePaths().clear();
		NativeLibraryLoader.addNativePaths( "a" );
		assertEquals( 1, NativeLibraryLoader.getNativePaths().size() );
		assertEquals( "a", NativeLibraryLoader.getNativePaths().get( 0 ) );

		NativeLibraryLoader.getNativePaths().clear();
		NativeLibraryLoader.addNativePaths( "a;b" );
		assertEquals( 2, NativeLibraryLoader.getNativePaths().size() );
		assertEquals( "a", NativeLibraryLoader.getNativePaths().get( 0 ) );
		assertEquals( "b", NativeLibraryLoader.getNativePaths().get( 1 ) );

		NativeLibraryLoader.getNativePaths().clear();
		NativeLibraryLoader.addNativePaths( ";a;b;" );
		assertEquals( 2, NativeLibraryLoader.getNativePaths().size() );
		assertEquals( "a", NativeLibraryLoader.getNativePaths().get( 0 ) );
		assertEquals( "b", NativeLibraryLoader.getNativePaths().get( 1 ) );

		NativeLibraryLoader.addNativePaths( "c;d" );
		assertEquals( 4, NativeLibraryLoader.getNativePaths().size() );
		assertEquals( "a", NativeLibraryLoader.getNativePaths().get( 0 ) );
		assertEquals( "b", NativeLibraryLoader.getNativePaths().get( 1 ) );
		assertEquals( "c", NativeLibraryLoader.getNativePaths().get( 2 ) );
		assertEquals( "d", NativeLibraryLoader.getNativePaths().get( 3 ) );

	}

	/**
	 * Nomen est omen!
	 */
	public void testAddNativePath() {
		NativeLibraryLoader.getNativePaths().clear();
		NativeLibraryLoader.addNativePath( null );
		assertEquals( 0, NativeLibraryLoader.getNativePaths().size() );

		NativeLibraryLoader.getNativePaths().clear();
		NativeLibraryLoader.addNativePath( "" );
		assertEquals( 0, NativeLibraryLoader.getNativePaths().size() );

		NativeLibraryLoader.getNativePaths().clear();
		NativeLibraryLoader.addNativePath( "a" );
		assertEquals( 1, NativeLibraryLoader.getNativePaths().size() );
		assertEquals( "a", NativeLibraryLoader.getNativePaths().get( 0 ) );

		NativeLibraryLoader.addNativePath( "b" );
		assertEquals( 2, NativeLibraryLoader.getNativePaths().size() );
		assertEquals( "a", NativeLibraryLoader.getNativePaths().get( 0 ) );
		assertEquals( "b", NativeLibraryLoader.getNativePaths().get( 1 ) );

	}

	/**
	 * Nomen est omen!
	 */
	public void testLoadFromResourceOnce() {
		try {
			assertNotNull( NativeLibraryLoader.load( "test", NativeLibraryLoaderTest.class, "test.dll" ) );
		} catch ( NativeLibraryLoadException nlle ) {
			fail( "Loading native lib failed with: " + nlle );
		}
	}

	/**
	 * Nomen est omen!
	 */
	public void testLoadFromResourceTwice() {
		try {
			String path1 = NativeLibraryLoader.load( "test", NativeLibraryLoaderTest.class, "test.dll" );
			String path2 = NativeLibraryLoader.load( "test", NativeLibraryLoaderTest.class, "test.dll" );
			assertNotNull( path1 );
			assertNotNull( path2 );
			assertEquals( path1, path2 );
		} catch ( NativeLibraryLoadException nlle ) {
			fail( "Loading native lib failed with: " + nlle );
		}
	}
}
