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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import de.compeople.commons.util.StringUtils;

/**
 * The native library preparer tries to load the specified native library into the
 * jvm. The following strategy is used:
 * <ol>
 * <li>try a System.loadLibray() with the library name</li>
 * <li>try to locate the requested library from the native library search path specified with
 *     <code>addNativePaths()</code> and or <code>addNativePath()</code></li>
 * <li>try to extract an ´embedded´ native library (stored within a jar) into a temporary location and
 *     load this copy. The temporary copy/ies will be deleted when the jvm exits.</li>    
 * </ol>
 */
public final class NativeLibraryLoader {

	private static final int COPY_BUFFER_SIZE = 10240;

	private static List<String> nativePaths = new ArrayList<String>();

	private static Set<String> loadedByLibraryName = new HashSet<String>();
	private static Set<File> loadedByFile = new HashSet<File>();
	private static Map<String, String> loadedByResource = new HashMap<String, String>();

	// The place for extracting temporary native libraries.
	private static File tempTargetDir;

	static {
		String userHome = System.getProperty( "user.home" );
		tempTargetDir = new File( new File( userHome, ".proxyselector" ), "native" );

		NativeLibraryLoader.CleanupThread cleaner = new NativeLibraryLoader.CleanupThread();

		// cleanup before (find bugs: yes, this is intended - the cleaner should just run here sequentially) 
		cleaner.run();

		// clean after by installing a shutdown hook 
		Runtime.getRuntime().addShutdownHook( cleaner );
	}

	/**
	 * Add a semicolon ";" separated list of path names to the native library search path.
	 * 
	 * @param paths
	 */
	public synchronized static void addNativePaths( String paths ) {
		if ( StringUtils.isGiven( paths ) ) {

			Scanner scanner = new Scanner( paths );
			scanner.useDelimiter( Pattern.compile( ";" ) );
			while ( scanner.hasNext() ) {
				String path = scanner.next();
				addNativePath( path );
			}
		}
	}

	/**
	 * Add a single path name to the native libary search path.
	 * 
	 * @param path
	 */
	public synchronized static void addNativePath( String path ) {
		if ( StringUtils.isGiven( path ) ) {
			nativePaths.add( path );
		}
	}

	/**
	 * Try to load the specified library.
	 * 
	 * @param libraryName the name of the library (without extension)
	 * @param baseForResource the class which is used as reference for loading the resource of the library 
	 * @param resourceName the resource name of the library
	 * @return the file path of the library if it has been extracted into a temporary location
	 *         otherwise null.
	 * @throws NativeLibraryLoadException if the library could not be loaded
	 */
	public synchronized static String load( String libraryName, Class<?> baseForResource, String resourceName ) throws NativeLibraryLoadException {
		Throwable reason = null;

		if ( StringUtils.isGiven( libraryName ) ) {
			if ( loadedByLibraryName.contains( libraryName ) ) {
				// Already done!
				return null;
			} else {
				try {
					System.loadLibrary( libraryName );
					loadedByLibraryName.add( libraryName );
					return null;
				} catch ( UnsatisfiedLinkError ule ) {
					// we try harder!
					reason = ule;
				}
			}

			String osDependentLibraryName = System.mapLibraryName( libraryName );
			for ( String path : nativePaths ) {
				File nativeFile = new File( path, osDependentLibraryName );
				if ( loadedByFile.contains( nativeFile ) ) {
					// Already done!
					return nativeFile.getPath();
				} else if ( nativeFile.exists() ) {
					try {
						System.load( nativeFile.getPath() );
						loadedByFile.add( nativeFile );
						return nativeFile.getPath();
					} catch ( UnsatisfiedLinkError ule ) {
						reason = ule;
					}
				}
			}
		}

		if ( baseForResource != null && StringUtils.isGiven( resourceName ) ) {
			String key = baseForResource.getName() + resourceName;
			String path = loadedByResource.get( key );
			if ( StringUtils.isGiven( path ) ) {
				return path;
			} else {
				InputStream inputStream = baseForResource.getResourceAsStream( resourceName );
				if ( inputStream != null ) {
					if ( tempTargetDir.exists() || tempTargetDir.mkdirs() ) {
						String[] preSuffix = getPreSuffix( resourceName );
						try {
							File targetFile = File.createTempFile( preSuffix[0], preSuffix[1], tempTargetDir );
							copy( inputStream, new FileOutputStream( targetFile ) );
							System.load( targetFile.getPath() );
							loadedByResource.put( key, targetFile.getPath() );
							return targetFile.getPath();
						} catch ( Throwable throwable ) {
							reason = throwable;
						}
					} else {
						reason = new FileNotFoundException( "Could not create user directory '" + tempTargetDir + "'for native library storage." );
					}
				} else {
					reason = new FileNotFoundException( "Could not locate resource '" + resourceName + "' from base '" + baseForResource + "'." );
				}
			}
		}

		throw new NativeLibraryLoadException( "Could not load requested native library.", reason );
	}

	/**
	 * Get the prefix and suffix for this resource name.
	 * 
	 * @param resourceName
	 * @return prefix and suffix within the string array
	 */
	private static String[] getPreSuffix( String resourceName ) {
		int lastDot = resourceName.lastIndexOf( '.' );
		if ( lastDot != -1 ) {
			return new String[] { resourceName.substring( 0, lastDot ), resourceName.substring( lastDot ) };
		} else {
			return new String[] { "nativelib", null };
		}
	}

	/**
	 * Copy input stream to output stream
	 *
	 * @param inputStream
	 * @param outputStream
	 * @throws IOException
	 */
	private static void copy( InputStream inputStream, OutputStream outputStream ) throws IOException {
		byte[] buffer = new byte[COPY_BUFFER_SIZE];
		int read;
		try {
			while ( ( read = inputStream.read( buffer, 0, COPY_BUFFER_SIZE ) ) != -1 ) {
				outputStream.write( buffer, 0, read );
			}
		} finally {
			inputStream.close();
			outputStream.close();
		}
	}

	/**
	 * Unit test helper!
	 * 
	 * @return
	 */
	static List<String> getNativePaths() {
		return nativePaths;
	}

	/**
	 * Remove as much temporary libraries as possible! 
	 */
	private static class CleanupThread extends Thread {

		/**
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				// Cleanup directory
				File[] tempNatives = tempTargetDir.listFiles();
				for ( File tempNative : tempNatives ) {
					// ignore return code, if it could not be deleted it is probably still in access!!
					if ( !tempNative.delete() ) {
						tempNative.deleteOnExit();
					}
				}
				tempTargetDir.deleteOnExit();
			} catch ( Throwable throwable ) {
				// never ever fail!!
			}
		}
	}

}
