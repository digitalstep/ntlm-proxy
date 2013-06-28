package de.digitalstep.ntlmproxy;

import static java.util.concurrent.Executors.newCachedThreadPool;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.compeople.commons.net.proxy.CompoundProxySelectorFactory;

public class NtlmProxy {

    private static final Logger log = LoggerFactory.getLogger(NtlmProxy.class);
    private static final ExecutorService EXECUTOR = newCachedThreadPool();

    static URI enableSystemProxy(final String location) throws URISyntaxException {
        log.debug(location.toString());
        URI uri = new URI(location);
        List<Proxy> proxies = CompoundProxySelectorFactory.getProxySelector().select(uri);
        for (Proxy proxy : proxies) {
            log.debug("Found proxy for {}: {}", uri, proxy);
            InetSocketAddress addr = (InetSocketAddress) proxy.address();
            if (addr != null) {
                System.setProperty("http.proxyHost", addr.getHostName());
                System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
            }
        }
        return uri;
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.useSystemProxies", "true");
        final int port = Integer.parseInt(args[0]);
        ServerSocket server = new ServerSocket(port);
        log.info("Proxy listening on port {}", port);
        while (true) {
            Socket socket = server.accept();
            EXECUTOR.execute(new Handler(socket));
        }
    }

}
