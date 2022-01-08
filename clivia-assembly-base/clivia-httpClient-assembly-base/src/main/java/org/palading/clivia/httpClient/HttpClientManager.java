package org.palading.clivia.httpClient;

import java.io.IOException;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * @author palading_cr
 * @title HttpClientPoolManager
 * @project clivia-gateway
 */
public interface HttpClientManager extends Cloneable {

    RequestConfig buildRequestConfig();

    Registry<ConnectionSocketFactory> register();

    HttpClientConnectionManager buildConnectionManager(Registry<ConnectionSocketFactory> registry);

    HttpRequestRetryHandler buildRetryHandler();

    CloseableHttpClient buildCloseableHttpClient();

    void close() throws IOException;
}
