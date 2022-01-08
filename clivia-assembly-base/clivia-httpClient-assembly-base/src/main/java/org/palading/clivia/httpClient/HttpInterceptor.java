package org.palading.clivia.httpClient;

import org.palading.clivia.httpClient.request.CliviaHttpRequest;
import org.palading.clivia.httpClient.response.CliviaHttpResponse;

/**
 * @author palading_cr
 * @title HttpInterceptor
 * @project clivia-gateway
 */
public interface HttpInterceptor {

    public boolean interceptor(CliviaHttpRequest cliviaHttpRequest) throws Exception;

    public int order();

    public void afterInterceptor(CliviaHttpRequest cliviaHttpRequest, CliviaHttpResponse cliviaHttpResponse) throws Exception;
}
