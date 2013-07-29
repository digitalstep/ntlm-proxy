package de.digitalstep.ntlmproxy;

import java.net.Proxy;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerListener {

    private static final Logger log = LoggerFactory.getLogger(HandlerListener.class);

    protected void onGet(URI uri, Proxy proxy) {
        log.info("Using {} for {}", proxy.address(), uri);
    }

}
