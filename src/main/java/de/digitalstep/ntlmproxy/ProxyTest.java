package de.digitalstep.ntlmproxy;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.ByteStreams.toByteArray;
import static java.lang.System.out;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;

import de.compeople.commons.net.proxy.CompoundProxySelectorFactory;

public class ProxyTest {

    private static final URI[] URIS = new URI[] {
            URI.create("http://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-resources-plugin/2.5/maven-resources-plugin-2.5.pom"),
            URI.create("http://rtbesexc02:47081/awappw/scsw?wsdl")
    };

    private static void enableSystemProxy(final URI uri) {
        Proxy proxy = CompoundProxySelectorFactory.getProxySelector().select(uri).get(0);
        InetSocketAddress addr = (InetSocketAddress) proxy.address();
        if (addr == null) {
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");
        } else {
            System.setProperty("http.proxyHost", addr.getHostName());
            System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.useSystemProxies", "true");
        System.out.println("detecting proxies");
        for (URI uri : URIS) {
            print(uri);
        }
    }

    private static void print(final URI uri) throws IOException, MalformedURLException {
        enableSystemProxy(uri);

        InputStream in = uri.toURL().openStream();
        out.println(new String(toByteArray(in), UTF_8));
        in.close();
    }

}
