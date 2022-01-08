package org.palading.clivia.httpClient.response;

import org.palading.clivia.httpClient.HttpClientResponse;

import java.io.IOException;



/**
 * @author palading_cr
 * @title ResponseErrorHandler
 * @project clivia-gateway
 */
public interface ResponseErrorHandler {

    boolean hasError(HttpClientResponse response) throws IOException;

    void handleError(HttpClientResponse response) throws IOException;

}
