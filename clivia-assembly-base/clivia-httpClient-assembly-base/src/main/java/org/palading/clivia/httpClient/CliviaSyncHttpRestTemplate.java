package org.palading.clivia.httpClient;

import java.util.Map;

import org.palading.clivia.httpClient.HttpHeader;
import org.palading.clivia.httpClient.HttpParam;
import org.palading.clivia.httpClient.HttpRestTemplate;
import org.palading.clivia.httpClient.response.CliviaHttpResponse;

/**
 * http restTemplate
 * 
 * @author palading_cr
 * @title CliviaHttpRestTemplate
 * @project clivia-gateway
 */
public interface CliviaSyncHttpRestTemplate<T> extends HttpRestTemplate {

    /**
     * sync http restTemplate
     *
     * @author palading_cr
     *
     */
    @Override
    default String getRestTemplateType() {
        return "clivia-sync-http-rest";
    }

    /**
     * http get method
     *
     * @author palading_cr
     *
     */
    public <T> CliviaHttpResponse<T> get(String url, HttpHeader header, HttpParam httpParam, Class<T> responseType)
        throws Exception;

    /**
     * http post form method
     *
     * @author palading_cr
     *
     */
    public <T> CliviaHttpResponse postForm(String url, HttpHeader header, HttpParam query, Map<String, String> bodyValues,
        Class<T> responseType) throws Exception;

    /**
     * http post json method
     *
     * @author palading_cr
     *
     */
    public <T> CliviaHttpResponse postJson(String url, HttpHeader header, HttpParam query, String body, Class<T> responseType)
        throws Exception;
}
