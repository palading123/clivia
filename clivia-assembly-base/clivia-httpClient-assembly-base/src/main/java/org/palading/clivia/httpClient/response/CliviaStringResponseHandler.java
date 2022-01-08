package org.palading.clivia.httpClient.response;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.palading.clivia.httpClient.HttpClientResponse;
import org.palading.clivia.httpClient.HttpHeader;
import org.palading.clivia.httpClient.response.CliviaHttpResponse;

/**
 * @author palading_cr
 * @title CliviaStringResponseHandler
 * @project clivia-gateway
 */
public class CliviaStringResponseHandler implements ResponseHandler {

    public static final int BUFFER_SIZE = 4096;

    @Override
    public CliviaHttpResponse handleResppnse(HttpClientResponse httpResponse, Class responseType) throws Exception {
        HttpHeader httpHeader = httpResponse.getHttpHeader();
        InputStream inputStream = httpResponse.getBody();
        if (inputStream == null) {
            return new CliviaHttpResponse(httpResponse.getStatusCode(), httpResponse.getStatusText(), null, httpHeader);
        }
        StringBuilder out = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(httpResponse.getBody(), "UTF-8");
        char[] buffer = new char[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = reader.read(buffer)) != -1) {
            out.append(buffer, 0, bytesRead);
        }
        return new CliviaHttpResponse(httpResponse.getStatusCode(), httpResponse.getStatusText(), out.toString(), httpHeader);
    }

    @Override
    public String getHandlerType() {
        return "java.lang.String";
    }
}
