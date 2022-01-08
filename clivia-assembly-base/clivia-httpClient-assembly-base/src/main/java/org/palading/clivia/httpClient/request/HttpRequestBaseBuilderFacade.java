package org.palading.clivia.httpClient.request;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;


import org.palading.clivia.httpClient.HttpHeader;
import org.palading.clivia.httpClient.HttpParam;
import org.palading.clivia.httpClient.request.CliviaHttpRequest;
import org.palading.clivia.httpClient.request.CliviaRequestEntityFactory;

/**
 * @author palading_cr
 * @title HttpRequestBaseBuilderFacade
 * @project clivia-gateway
 */
public class HttpRequestBaseBuilderFacade {

    private CliviaHttpRequest cliviaHttpRequest;

    private HttpRequestBaseFactory httpRequestBaseFactory;

    public HttpRequestBaseBuilderFacade(CliviaHttpRequest cliviaHttpRequest, HttpRequestBaseFactory httpRequestBaseFactory) {
        this.cliviaHttpRequest = cliviaHttpRequest;
        this.httpRequestBaseFactory = httpRequestBaseFactory;
    }

    public HttpRequestBase setHttpRequestBase() throws Exception {
        HttpRequestBase httpRequestBase = createRequestBase(cliviaHttpRequest);
        setHttpHeader(cliviaHttpRequest, httpRequestBase);
        setHttpEntity(cliviaHttpRequest, httpRequestBase);
        return httpRequestBase;
    }

    private void setHttpHeader(CliviaHttpRequest cliviaHttpRequestEntity, HttpRequestBase httpRequestBase) {
        HttpHeader headers = cliviaHttpRequestEntity.getHttpHeaders();
        if (null != headers && headers.getHeader().size() > 0) {
            Map<String, String> headerMap = headers.getHeader();
            headerMap.forEach((k, v) -> {
                httpRequestBaseFactory.setHeader(k, v, httpRequestBase);
            });
        }

    }

    public HttpRequestBase setHttpEntity(CliviaHttpRequest cliviaHttpRequestEntity, HttpRequestBase httpRequestBase)
        throws Exception {
        HttpHeader httpHeader = cliviaHttpRequestEntity.getHttpHeaders();
        HttpRequestEntity httpRequestEntity = getHttpRequestEntity(httpHeader.getContentType());
        HttpEntity httpEntity =
            getHttpEntity(httpRequestEntity, cliviaHttpRequestEntity.getBody(), httpHeader.getContentType(),
                httpHeader.getCharset());
        return httpRequestBaseFactory.setHttpRequestEntity(httpEntity, httpRequestBase);
    }

    /**
     * get HttpRequestEntity by contentType
     *
     * @author palading_cr
     *
     */
    private HttpRequestEntity getHttpRequestEntity(String contentType) {
        CliviaRequestEntityFactory cliviaRequestEntityFactory =
            CliviaRequestEntityFactory.getCliviaRequestEntityFactoryInstance();
        HttpRequestEntity httpRequestEntity = cliviaRequestEntityFactory.getHttpRequestEntity(contentType);
        return httpRequestEntity;
    }

    /**
     * get HttpEntity
     *
     * @author palading_cr
     *
     */
    private HttpEntity getHttpEntity(HttpRequestEntity httpRequestEntity, Object object, String contentType, String charset)
        throws Exception {
        CliviaRequestEntityFactory cliviaRequestEntityFactory =
            CliviaRequestEntityFactory.getCliviaRequestEntityFactoryInstance();
        HttpEntity httpEntity = cliviaRequestEntityFactory.getHttpEntity(httpRequestEntity, object, contentType, charset);
        return httpEntity;

    }

    private HttpRequestBase createRequestBase(CliviaHttpRequest cliviaHttpRequestEntity) {
        HttpParam httpParam = cliviaHttpRequestEntity.getHttpParam();
        String url = cliviaHttpRequestEntity.getUrl();
        if (null != httpParam && httpParam.getParam().size() > 0) {
            url += "?".concat(httpParam.getUrlParam());
        }
        return httpRequestBaseFactory.getHttpRequestBase(cliviaHttpRequestEntity.getMethod(), url);
    }

}
