package de.digitalstep.ntlmproxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;

import de.compeople.commons.net.proxy.CompoundProxySelectorFactory;

class Handler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    private static final List<String> stripHeadersIn = Arrays.asList("Content-Type", "Content-Length", "Proxy-Connection");
    private static final List<String> stripHeadersOut = Arrays.asList("Proxy-Authentication", "Proxy-Authorization");

    private final Socket socket;

    private HandlerListener listener;

    public Handler(Socket socket, Optional<HandlerListener> listener) {
        this.socket = socket;
        this.listener = listener.isPresent() ? listener.get() : new HandlerListener();
    }

    public void run() {
        HttpParser parser = null;
        try {
            parser = new HttpParser(socket.getInputStream());
            try {
                while (!parser.parse())
                    ;
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
                return;
            }
            URI uri = enableSystemProxy(parser.getUri());
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod(parser.getMethod());
            connection.setInstanceFollowRedirects(false);
            for (Header header : parser.getHeaders()) {
                if (!stripHeadersIn.contains(header.name())) {
                    connection.addRequestProperty(header.name(), header.value());
                }
            }

            socket.shutdownInput();

            final OutputStream out = socket.getOutputStream();

            switch (connection.getResponseCode()) {
            case 404:
                out.write("HTTP/1.0 404 Not Found\r\n".getBytes());
                out.close();
                return;
            }

            for (int index = 0; index < 1; index++) {
                Header header = new Header(connection.getHeaderFieldKey(index), connection.getHeaderField(index));
                if (!stripHeadersOut.contains(header.name())) {
                    out.write((header.toString() + "\r\n").getBytes());
                    log.debug("Wrote header {}", header);
                }
            }

            out.write("Gunnar: Test\r\n".getBytes());

            final InputStream in = connection.getInputStream();
            out.write("\r\n".getBytes());

            byte[] bytes = ByteStreams.toByteArray(in);
            ByteStreams.copy(new ByteArrayInputStream(bytes), out);
            // out.write("\r\n".getBytes());
            parser.close();
            in.close();
            out.close();
            log.debug("Output closed");
            connection.disconnect();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private URI enableSystemProxy(final String location) throws URISyntaxException {
        URI uri = new URI(location);
        Proxy proxy = CompoundProxySelectorFactory.getProxySelector().select(uri).get(0);
        listener.onGet(uri, proxy);
        InetSocketAddress addr = (InetSocketAddress) proxy.address();
        if (addr == null) {
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");
        } else {
            System.setProperty("http.proxyHost", addr.getHostName());
            System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
        }
        return uri;
    }
}