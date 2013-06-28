package de.digitalstep.ntlmproxy;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.ByteStreams.toByteArray;
import static java.lang.System.out;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.List;

import de.compeople.commons.net.proxy.CompoundProxySelectorFactory;

public class ProxyTest {

    private static final String URL = "http://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-resources-plugin/2.5/maven-resources-plugin-2.5.pom";

    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.useSystemProxies", "true");
        System.out.println("detecting proxies");
        final URI uri = new URI(URL);
        enableSystemProxy(uri);

        InputStream in = uri.toURL().openStream();
        out.println(new String(toByteArray(in), UTF_8));
        in.close();
    }

    private static void enableSystemProxy(final URI uri) {
        List<Proxy> proxies = CompoundProxySelectorFactory.getProxySelector().select(uri);
        for (Proxy proxy : proxies) {
            System.out.println("proxy: " + proxy);

            InetSocketAddress addr = (InetSocketAddress) proxy.address();
            if (addr != null) {
                System.setProperty("http.proxyHost", addr.getHostName());
                System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
            }
        }
    }

}
