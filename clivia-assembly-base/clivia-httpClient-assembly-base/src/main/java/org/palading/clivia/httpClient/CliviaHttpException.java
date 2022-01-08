package org.palading.clivia.httpClient;

/**
 * @author palading_cr
 * @title CliviaHttpClientException
 * @project clivia-gateway
 */
public abstract class CliviaHttpException extends RuntimeException {
    private int statusCode;
    private String message;
    private byte[] body;
    private HttpHeader httpHeader;

    public CliviaHttpException(String message, int statusCode, byte[] body, HttpHeader httpHeader) {
        super(message);
        this.statusCode = statusCode;
        this.body = body;
        this.httpHeader = httpHeader;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public void setHttpHeader(HttpHeader httpHeader) {
        this.httpHeader = httpHeader;
    }
}
