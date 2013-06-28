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

/**
 * Indicates an error loading a native library.
 */
public class NativeLibraryLoadException extends Exception {

    private NativeLibraryLoadException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public NativeLibraryLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public NativeLibraryLoadException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public NativeLibraryLoadException(Throwable cause) {
        super(cause);
    }

}
