package org.palading.clivia.httpClient.response;


import org.palading.clivia.httpClient.HttpClientResponse;
import org.palading.clivia.httpClient.response.CliviaHttpResponse;

/**
 * @author palading_cr
 * @title ResponseHandler
 * @project clivia-gateway
 */
public interface ResponseHandler<T> {

    public <T> CliviaHttpResponse<T> handleResppnse(HttpClientResponse httpResponse, Class<T> responseType) throws Exception;

    public String getHandlerType();
}
