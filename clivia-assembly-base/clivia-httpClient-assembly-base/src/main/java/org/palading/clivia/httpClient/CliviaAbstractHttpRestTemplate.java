package org.palading.clivia.httpClient;

import org.palading.clivia.httpClient.request.CliviaHttpRequest;
import org.palading.clivia.httpClient.request.CliviaSyncHttpRequest;
import org.palading.clivia.httpClient.response.CliviaHttpResponse;

import java.util.List;



/**
 * @author palading_cr
 * @title CliviaAbstractHttpRestTemplate
 * @project clivia-gateway
 */
public abstract class CliviaAbstractHttpRestTemplate {

    private static List<HttpInterceptor> httpInterceptorList;

    private CliviaSyncHttpRequest cliviaSyncHttpRequest;

    public CliviaAbstractHttpRestTemplate(List<HttpInterceptor> httpInterceptorList, CliviaSyncHttpRequest cliviaSyncHttpRequest) {
        this.httpInterceptorList = httpInterceptorList;
        this.cliviaSyncHttpRequest = cliviaSyncHttpRequest;

    }

    /**
     * When the requested interception method returns fasle, execute the request and perform post-processing
     * 
     * @author palading_cr
     *
     */
    public CliviaHttpResponse excute(CliviaHttpRequest cliviaHttpRequest) throws Exception {
        CliviaHttpResponse cliviaHttpResponse = null;
        if (Interceptor.interceptor(cliviaHttpRequest)) {
            cliviaHttpResponse = cliviaSyncHttpRequest.execute(cliviaHttpRequest);
            Interceptor.afterInterceptor(cliviaHttpRequest, cliviaHttpResponse);
        }
        return cliviaHttpResponse;
    }

    static class Interceptor {
        private static boolean interceptor(CliviaHttpRequest cliviaHttpRequest) throws Exception {
            boolean flag = true;
            for (HttpInterceptor httpInterceptor : httpInterceptorList) {
                if (!httpInterceptor.interceptor(cliviaHttpRequest)) {
                    flag = false;
                    break;
                }
            }
            return flag;
        }

        private static void afterInterceptor(CliviaHttpRequest cliviaHttpRequest, CliviaHttpResponse cliviaHttpResponse)
            throws Exception {
            for (HttpInterceptor httpInterceptor : httpInterceptorList) {
                httpInterceptor.afterInterceptor(cliviaHttpRequest, cliviaHttpResponse);
            }
        }
    }

}
