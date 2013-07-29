package de.digitalstep.ntlmproxy;

import static java.util.concurrent.Executors.newCachedThreadPool;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import de.digitalstep.ntlmproxy.ui.TrayIconBuilder;

public class NtlmProxy {

    private static final Logger log = LoggerFactory.getLogger(NtlmProxy.class);
    private static final ExecutorService EXECUTOR = newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.useSystemProxies", "true");

        final Optional<HandlerListener> listener = initUI();

        final int port = Integer.parseInt(args[0]);
        ServerSocket server = new ServerSocket(port);
        log.info("Proxy listening on port {}", port);
        while (true) {
            Socket socket = server.accept();
            EXECUTOR.execute(new Handler(socket, listener));
        }
    }

    private static Optional<HandlerListener> initUI() {
        return new TrayIconBuilder().build();
    }

}
