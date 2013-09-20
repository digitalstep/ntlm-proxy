package de.digitalstep.ntlmproxy.nativelib;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Files.copy;
import static com.google.common.io.Resources.newInputStreamSupplier;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copies a dll from the jar to a settings directory and loads the native
 * library.
 */
public final class NativeLibrary {

    private static Set<NativeLibrary> loaded = new HashSet<NativeLibrary>();
    private static final Logger log = LoggerFactory.getLogger(NativeLibrary.class);
    private static File settingsDir = new File(System.getProperty("user.home"), ".proxyselector");

    private final String resourceName;

    private NativeLibrary(String resourceName) {
        this.resourceName = checkNotNull(resourceName);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NativeLibrary)) {
            return false;
        }
        return resourceName.equals(((NativeLibrary) obj).resourceName);
    }

    @Override
    public int hashCode() {
        return resourceName.hashCode();
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("resourceName", resourceName)
                .toString();
    }

    /**
     * Try to load the specified library.
     * 
     * @param resourceName
     *            the resource name of the library
     * 
     * @return {@code true} if the library has been loaded ({@code false} may
     *         indicate that the library had been added before.
     */
    public synchronized static boolean load(String resourceName) {
        NativeLibrary result = new NativeLibrary(resourceName);

        if (loaded.contains(result)) {
            log.warn("Trying to load an existing library again ({})", resourceName);
            return false;
        }

        if (!settingsDir.exists() && !settingsDir.mkdir()) {
            throw new RuntimeException("Could not create temp dir " + settingsDir);
        }
        
        try {
            File targetFile = new File(settingsDir, resourceName);
            log.info("Copying dll to {}", targetFile);
            copy(newInputStreamSupplier(NativeLibrary.class.getResource(resourceName)), targetFile);
            System.load(targetFile.getPath());
            loaded.add(result);
            return true;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
