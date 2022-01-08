package org.palading.clivia.httpClient;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * AbstractCloseableHttpClient
 * 
 * @author palading_cr
 * @title AbstractCloseableHttpClient
 * @project clivia-gateway
 */
public abstract class AbstractCloseableHttpClient implements HttpClientManager {

    HttpClientConfig httpClientConfig;

    public AbstractCloseableHttpClient(HttpClientConfig httpClientConfig) {
        this.httpClientConfig = httpClientConfig;
    }

    /**
     * build Registry
     *
     * @author palading_cr
     *
     */
    @Override
    public Registry<ConnectionSocketFactory> register() {
        return RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
    }

    /**
     * build httpClientConnectionManager by registry
     *
     * @author palading_cr
     *
     */
    @Override
    public HttpClientConnectionManager buildConnectionManager(Registry<ConnectionSocketFactory> registry) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(httpClientConfig.getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(httpClientConfig.getMaxPerRoute());
        return connectionManager;
    }

    /**
     * build retryHandler
     *
     * @author palading_cr
     *
     */
    @Override
    public HttpRequestRetryHandler buildRetryHandler() {
        if (httpClientConfig.isRetry()) {
            retryHandler();
        }
        return null;
    }

    /**
     * build requestConfig
     *
     * @author palading_cr
     *
     */
    @Override
    public RequestConfig buildRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(httpClientConfig.getConnectionTimeout())
            .setConnectionRequestTimeout(httpClientConfig.getConnectionRequestTimeout())
            .setSocketTimeout(httpClientConfig.getSocketTimeout()).setMaxRedirects(httpClientConfig.getMaxRedirects()).build();
    }

    /**
     * get retryHandler
     *
     * @author palading_cr
     *
     */
    public abstract HttpRequestRetryHandler retryHandler();
}
