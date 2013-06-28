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
package de.compeople.commons.util;

import junit.framework.TestCase;

/**
 * Nomen est omen!
 */
public class StringUtilsTest extends TestCase {

	/**
	 * Nomen est omen!
	 */
	public void testIsEmpty() {
		assertTrue( StringUtils.isEmpty( null ) );
		assertTrue( StringUtils.isEmpty( "" ) );
		assertFalse( StringUtils.isEmpty( " " ) );
		assertFalse( StringUtils.isEmpty( " a " ) );
	}

	/**
	 * Nomen est omen!
	 */
	public void testIsGiven() {
		assertFalse( StringUtils.isGiven( null ) );
		assertFalse( StringUtils.isGiven( "" ) );
		assertTrue( StringUtils.isGiven( " " ) );
		assertTrue( StringUtils.isGiven( " a " ) );
	}

	/**
	 * Nomen est omen!
	 */
	public void testIsDeepEmpty() {
		assertTrue( StringUtils.isDeepEmpty( null ) );
		assertTrue( StringUtils.isDeepEmpty( "" ) );
		assertTrue( StringUtils.isDeepEmpty( " " ) );
		assertFalse( StringUtils.isDeepEmpty( " a " ) );
	}

	/**
	 * Nomen est omen!
	 */
	public void testEquals() {
		assertTrue( StringUtils.equals( null, null ) );

		assertFalse( StringUtils.equals( null, "" ) );
		assertFalse( StringUtils.equals( "", null ) );

		String c = "c";
		assertTrue( StringUtils.equals( c, c ) );

		assertTrue( StringUtils.equals( "c", "c" ) );

		assertFalse( StringUtils.equals( null, "a" ) );
		assertFalse( StringUtils.equals( "a", null ) );

		assertFalse( StringUtils.equals( "b", "a" ) );
		assertFalse( StringUtils.equals( "a", "b" ) );

	}
}
