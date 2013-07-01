package de.digitalstep.ntlmproxy;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

public class CommonsHttpClientTest {

    public static void main(String[] args) throws IOException {
        Content content = Request
                .Get("http://www.google.de")
                .viaProxy(new HttpHost("someproxy", 80))
                .execute()
                .returnContent();

        System.out.println(content);
    }

}
