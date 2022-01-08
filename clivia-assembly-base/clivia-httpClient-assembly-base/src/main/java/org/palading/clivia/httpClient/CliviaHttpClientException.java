package org.palading.clivia.httpClient;

/**
 * @author palading_cr
 * @title CliviaHttpClientException
 * @project clivia-gateway
 */
public class CliviaHttpClientException extends CliviaHttpException {

    public CliviaHttpClientException(String message, int statusCode, HttpHeader httpHeader, byte[] body) {
        super(message, statusCode, body, httpHeader);
    }

}
