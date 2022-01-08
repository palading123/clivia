package org.palading.clivia.httpClient;

import org.palading.clivia.httpClient.HttpHeader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author palading_cr
 * @title HttpClientResponse
 * @project clivia-gateway
 */
public interface HttpClientResponse {

    HttpHeader getHttpHeader();

    int getStatusCode() throws IOException;

    InputStream getBody() throws IOException;

    String getStatusText() throws IOException;

    void close();

}
