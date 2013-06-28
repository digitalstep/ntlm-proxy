package de.digitalstep.ntlmproxy;

import static java.util.concurrent.Executors.newCachedThreadPool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

import de.compeople.commons.net.proxy.CompoundProxySelectorFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final ExecutorService EXECUTOR = newCachedThreadPool();

    private static final List<String> stripHeadersIn = Arrays.asList("Content-Type", "Content-Length", "Proxy-Connection");
    private static final List<String> stripHeadersOut = Arrays.asList("Proxy-Authentication", "Proxy-Authorization");

    static class Handler implements Runnable {
        private final Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            HttpParser parser = null;
            try {
                parser = new HttpParser(socket.getInputStream());
                try {
                    while (!parser.parse())
                        ;
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                    return;
                }
                URI uri = enableSystemProxy(parser.getUri());
                HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
                connection.setRequestMethod(parser.getMethod());
                connection.setInstanceFollowRedirects(false);
                for (NameValuePair header : parser.getHeaders()) {
                    if (!stripHeadersIn.contains(header.getName())) {
                        connection.addRequestProperty(header.getName(), header.getValue());
                    }
                }

                socket.shutdownInput();

                final OutputStream out = socket.getOutputStream();
                for (int index = 0; index < 1; index++) {
                    NameValuePair header = new NameValuePair(connection.getHeaderFieldKey(index), connection.getHeaderField(index));
                    if (!stripHeadersOut.contains(header.getName())) {
                        out.write((header.toString() + "\r\n").getBytes());
                        logger.debug("Wrote header {}", header);
                    }
                }
                out.write("Gunnar: Test\r\n".getBytes());

                final InputStream in = connection.getInputStream();
                out.write("\r\n".getBytes());

                byte[] bytes = ByteStreams.toByteArray(in);
                ByteStreams.copy(new ByteArrayInputStream(bytes), out);
//                out.write("\r\n".getBytes());
                parser.close();
                in.close();
                out.close();
                logger.debug("Output closed");
                connection.disconnect();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static URI enableSystemProxy(final String location) throws URISyntaxException {
        logger.debug(location.toString());
        URI uri = new URI(location);
        List<Proxy> proxies = CompoundProxySelectorFactory.getProxySelector().select(uri);
        for (Proxy proxy : proxies) {
            System.out.println("proxy: " + proxy);

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

        ServerSocket server = new ServerSocket(1234);
        while (true) {
            Socket socket = server.accept();
            EXECUTOR.execute(new Handler(socket));
        }
    }

}
