package org.palading.clivia.httpClient;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.palading.clivia.httpClient.AbstractCloseableHttpClient;
import org.palading.clivia.httpClient.HttpClientConfig;

/**
 * @author palading_cr
 * @title CliviaSimpleHttpPoolManager
 * @project clivia-gateway
 */
public class CliviaSimpleCloseableHttpClient extends AbstractCloseableHttpClient {

    private HttpClientConfig httpClientConfig;
    private CloseableHttpClient closeableHttpClient;

    public CliviaSimpleCloseableHttpClient(HttpClientConfig httpClientConfig) {
        super(httpClientConfig);
        buildCloseableHttpClient();
    }

    @Override
    public CloseableHttpClient buildCloseableHttpClient() {
        if (null != closeableHttpClient) {
            return closeableHttpClient;
        }
        return closeableHttpClient =
            HttpClientBuilder.create().setDefaultRequestConfig(buildRequestConfig()).setRetryHandler(buildRetryHandler())
                .setConnectionManager(buildConnectionManager(register())).build();
    }

    @Override
    public void close() throws IOException {
        if (null != closeableHttpClient) {
            closeableHttpClient.close();
        }
    }

    @Override
    public HttpRequestRetryHandler retryHandler() {
        return new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= httpClientConfig.getExecutionCountLimit()) {
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    return false;
                }
                if (exception instanceof SSLException) {
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                return !(request instanceof HttpEntityEnclosingRequest);
            }
        };
    }
}
