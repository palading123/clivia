package org.palading.clivia.httpClient.request;

import java.lang.reflect.Type;
import org.palading.clivia.httpClient.HttpHeader;
import org.palading.clivia.httpClient.HttpParam;

/**
 * @author palading_cr
 * @title CliviaHttpRequestEntity
 * @project clivia-gateway
 */
public class CliviaHttpRequest {
    private HttpHeader httpHeaders;
    private String url;
    private HttpParam httpParam;
    private Object body;
    private String method;
    private Type responseType;

    public CliviaHttpRequest(HttpHeader httpHeaders, String url, HttpParam httpParam, Object body, String method,
        Type responseType) {
        this.httpHeaders = httpHeaders;
        this.url = url;
        this.body = body;
        this.httpParam = httpParam;
        this.method = method;
        this.responseType = responseType;
    }

    public CliviaHttpRequest(HttpHeader httpHeaders, String url, HttpParam httpParam, String method, Type responseType) {
        this.httpHeaders = httpHeaders;
        this.url = url;
        this.httpParam = httpParam;
        this.method = method;
        this.responseType = responseType;
    }

    public HttpHeader getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(HttpHeader httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpParam getHttpParam() {
        return httpParam;
    }

    public void setHttpParam(HttpParam httpParam) {
        this.httpParam = httpParam;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Type getResponseType() {
        return responseType;
    }

    public void setResponseType(Type responseType) {
        this.responseType = responseType;
    }
}
