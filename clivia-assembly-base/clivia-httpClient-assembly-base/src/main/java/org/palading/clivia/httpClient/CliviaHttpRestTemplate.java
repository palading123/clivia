package org.palading.clivia.httpClient;

import java.util.List;
import java.util.Map;
import org.palading.clivia.httpClient.CliviaAbstractHttpRestTemplate;
import org.palading.clivia.httpClient.request.CliviaHttpRequest;
import org.palading.clivia.httpClient.request.CliviaSyncHttpRequest;
import org.palading.clivia.httpClient.response.CliviaHttpResponse;

/**
 * @author palading_cr
 * @title CliviaDefaultHttpRestTemplate
 * @project clivia-gateway
 */
public class CliviaHttpRestTemplate extends CliviaAbstractHttpRestTemplate implements CliviaSyncHttpRestTemplate {

    private List<HttpInterceptor> httpInterceptors;

    private CliviaSyncHttpRequest cliviaSyncHttpRequest;

    public CliviaHttpRestTemplate(CliviaSyncHttpRequest cliviaSyncHttpRequest, List<HttpInterceptor> httpInterceptors) {
        super(httpInterceptors, cliviaSyncHttpRequest);
    }

    @Override
    public CliviaHttpResponse get(String url, HttpHeader header, HttpParam httpParam, Class responseType) throws Exception {
        return excute(new CliviaHttpRequest(header, url, httpParam, "GET", responseType));
    }

    @Override
    public CliviaHttpResponse postForm(String url, HttpHeader header, HttpParam httpParam, Map params, Class responseType)
        throws Exception {
        if (null == header) {
            header = new HttpHeader();
            header.addHeader("Content-Type", "application/x-www-form-urlencoded");
        }
        return excute(new CliviaHttpRequest(header, url, httpParam, params, "POST", responseType));
    }

    @Override
    public CliviaHttpResponse postJson(String url, HttpHeader header, HttpParam httpParam, String body, Class responseType)
        throws Exception {
        return excute(new CliviaHttpRequest(header, url, httpParam, body, "POST", responseType));
    }
}
