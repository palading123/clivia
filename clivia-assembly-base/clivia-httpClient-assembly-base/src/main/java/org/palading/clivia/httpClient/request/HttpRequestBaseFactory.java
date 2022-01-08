package org.palading.clivia.httpClient.request;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * HttpRequestBaseFactory
 * 
 * @author palading_cr
 * @title HttpRequestBaseFactory
 * @project clivia-gateway
 */
public interface HttpRequestBaseFactory {

    /**
     * get httpRequestBase by method and uri
     *
     * @author palading_cr
     *
     */
    public HttpRequestBase getHttpRequestBase(String method, String uri);

    /**
     * set header
     *
     * @author palading_cr
     *
     */

    public HttpRequestBase setHeader(String headerName, String headerValue, HttpRequestBase httpRequestBase);

    /**
     * set entity
     *
     * @author palading_cr
     *
     */
    public HttpRequestBase setHttpRequestEntity(HttpEntity httpEntity, HttpRequestBase httpRequestBase);
}
