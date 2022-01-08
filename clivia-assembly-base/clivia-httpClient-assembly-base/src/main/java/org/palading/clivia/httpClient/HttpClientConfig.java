package org.palading.clivia.httpClient;

import java.io.Serializable;

/**
 * @author palading_cr
 * @title HttpConfig
 * @project clivia-gateway
 */
public class HttpClientConfig implements Serializable {
    private int maxTotal = 200;
    private int maxPerRoute = 20;
    private int connectionTimeout = -1;
    private int connectionRequestTimeout = -1;
    private int socketTimeout = -1;
    private int maxRedirects = 20;
    private boolean retry;
    private int executionCountLimit;

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public void setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public void setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public int getExecutionCountLimit() {
        return executionCountLimit;
    }

    public void setExecutionCountLimit(int executionCountLimit) {
        this.executionCountLimit = executionCountLimit;
    }
}
