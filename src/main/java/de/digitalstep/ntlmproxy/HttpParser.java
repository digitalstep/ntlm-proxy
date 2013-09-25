package de.digitalstep.ntlmproxy;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser extends InputStream {

    private static final Logger log = LoggerFactory.getLogger(HttpParser.class);

    private int bodyIndex;
    private byte[] buffer = new byte[4096];
    private long contentLength;
    private String contentType;
    private Header[] headers;
    private int index;
    private final InputStream delegate;
    private String method, uri, protocol;

    public HttpParser(InputStream is) {
        this.delegate = is;
    }

    public void close() throws IOException {
        delegate.close();
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public boolean parse() throws IOException {
        index += delegate.read(buffer, index, buffer.length - index);
        String line = new String(buffer);
        log.debug(line);
        int splitAt = line.indexOf("\r\n\r\n");
        if (splitAt == -1) {
            return false;
        }
        bodyIndex = splitAt + 4;

        line = line.substring(0, splitAt);
        String[] headerLines = line.split("\r\n");
        try {
            String[] httpStuff = headerLines[0].split(" ");
            method = httpStuff[0];
            uri = httpStuff[1];
            protocol = httpStuff[2];
            log.debug(method + " " + uri + " " + protocol);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IOException("Bad HTTP header", ex);
        }
        this.headers = new Header[headerLines.length - 1];
        for (int i = 1; i < headerLines.length; i++) {
            String[] h = headerLines[i].split(": ", 2);
            if (h.length != 2) {
                throw new IOException("Bad Header:" + headerLines[i]);
            }
            final String name = h[0];
            final String value = h[1];
            if (name.equals("Content-Type")) {
                this.contentType = value;
            } else if (name.equals("Content-Length")) {
                try {
                    this.contentLength = NumberFormat.getIntegerInstance().parse(value).longValue();
                } catch (ParseException e) {
                    throw new IOException("Cannot parse Content-Length", e);
                }
            }
            this.headers[i - 1] = new Header(name, value);
        }
        return true;
    }

    public int read() throws IOException {
        if (bodyIndex < index) {
            return buffer[bodyIndex++];
        } else {
            return delegate.read();
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (bodyIndex < index) {
            int toCopy = Math.min(len, index - bodyIndex);
            System.arraycopy(buffer, bodyIndex, b, off, toCopy);
            bodyIndex += toCopy;
            return toCopy;
        }
        return delegate.read(b, off, len);
    }

    public void setBodyIndex(int i) {
        this.bodyIndex = i;
    }

}
