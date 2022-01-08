package org.palading.clivia.httpClient;


import org.palading.clivia.httpClient.request.CliviaHttpRequest;
import org.palading.clivia.httpClient.response.CliviaHttpResponse;

/**
 * @author palading_cr
 * @title CliviaDefaultHttpInterceptor
 * @project clivia-gateway
 */
public class CliviaDefaultHttpInterceptor implements HttpInterceptor {

    @Override
    public boolean interceptor(CliviaHttpRequest cliviaHttpRequest) throws Exception {
        return true;
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void afterInterceptor(CliviaHttpRequest cliviaHttpRequest, CliviaHttpResponse cliviaHttpResponse) throws Exception {

    }
}
