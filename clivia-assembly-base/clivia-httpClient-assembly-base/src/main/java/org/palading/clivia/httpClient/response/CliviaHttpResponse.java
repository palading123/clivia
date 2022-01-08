package org.palading.clivia.httpClient.response;


import org.palading.clivia.httpClient.HttpHeader;

/**
 * @author palading_cr
 * @title CliviaHttpResponse
 * @project clivia-gateway
 */
public class CliviaHttpResponse<T> extends HttpBaseResponse {

    private HttpHeader httpHeader;

    public CliviaHttpResponse(int resCode, String resMsg, T data, HttpHeader httpHeader) {
        super(resCode, resMsg, data);
        this.httpHeader = httpHeader;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public void setHttpHeader(HttpHeader httpHeader) {
        this.httpHeader = httpHeader;
    }
}
