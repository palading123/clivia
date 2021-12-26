package org.palading.clivia.httpClient;


import org.palading.clivia.httpClient.request.CliviaHttpRequest;

/**
 * @author palading_cr
 * @title HttpInterceptor
 * @project clivia-gateway
 */
public interface HttpInterceptor {

    public void interceptor(CliviaHttpRequest cliviaHttpRequest) throws Exception;

    public int order();
}
