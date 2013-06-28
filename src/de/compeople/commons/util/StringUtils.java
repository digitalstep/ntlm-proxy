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

/**
 * Collection of String/CharSequence utilities. 
 */
public final class StringUtils {

	private StringUtils() {
	// Utility class
	}

	/**
	 * Tests ´simple´ emptiness.
	 * A character sequence is empty either if it is null or it has a zero length.
	 * 
	 * @param sequence
	 * @return
	 */
	public static final boolean isEmpty( final CharSequence sequence ) {
		return sequence == null || sequence.length() == 0;
	}

	/**
	 * Tests whether the char sequence has content.
	 * A character sequence is given either if it is not null and it has a none zero length.
	 * 
	 * @param sequence
	 * @return
	 */
	public static final boolean isGiven( final CharSequence sequence ) {
		return sequence != null && sequence.length() != 0;
	}

	/**
	 * Tests ´deep´ emptiness.
	 * A string is empty if it is either null or its trimmed version has a zero length.
	 * 
	 * @param string
	 * @return
	 */
	public static final boolean isDeepEmpty( final String string ) {
		return string == null || string.trim().length() == 0;
	}

	/**
	 * Tests equality of the given strings.
	 * 
	 * @param sequence1 candidate 1, may be null
	 * @param sequence2 candidate 2, may be null
	 * @return
	 */
	public static final boolean equals( final CharSequence sequence1, final CharSequence sequence2 ) {
		if ( sequence1 == sequence2 ) {
			return true;
		} else if ( sequence1 == null || sequence2 == null ) {
			return false;
		} else {
			return sequence1.equals( sequence2 );
		}
	}

}
