package org.palading.clivia.httpClient.request;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.palading.clivia.httpClient.CliviaHttpClientException;
import org.palading.clivia.httpClient.CliviaResponseSelectLoader;
import org.palading.clivia.httpClient.HttpClientManager;
import org.palading.clivia.httpClient.HttpMessageBodyResponseWrapper;
import org.palading.clivia.httpClient.response.CliviaHttpResponse;
import org.palading.clivia.httpClient.response.ResponseErrorHandler;
import org.palading.clivia.httpClient.response.ResponseHandler;

import java.io.IOException;


/**
 * @author palading_cr
 * @title CliviaDefaultHttpRequest
 * @project clivia-gateway
 */
public class CliviaDefaultHttpRequest implements CliviaSyncHttpRequest {

    private HttpClientManager httpClientManager;

    private HttpRequestBaseFactory httpRequestBaseFactory;

    private CliviaResponseSelectLoader cliviaResponseSelectLoader;

    private ResponseErrorHandler responseErrorHandler;

    public CliviaDefaultHttpRequest(HttpRequestBaseFactory httpRequestBaseFactory,
                                    CliviaResponseSelectLoader cliviaResponseSelectLoader, ResponseErrorHandler responseErrorHandler,
                                    HttpClientManager httpClientManager) {
        this.httpRequestBaseFactory = httpRequestBaseFactory;
        this.cliviaResponseSelectLoader = cliviaResponseSelectLoader;
        this.responseErrorHandler = responseErrorHandler;
        this.httpClientManager = httpClientManager;

    }

    /**
     * excute request
     * 
     * @author palading_cr
     *
     */
    @Override
    public CliviaHttpResponse execute(CliviaHttpRequest requestHttpEntity) {
        try {
            CloseableHttpResponse closeableHttpResponse =
                httpClientManager.buildCloseableHttpClient().execute(httpRequestBase(requestHttpEntity));
            HttpMessageBodyResponseWrapper httpMessageBodyResponseWrapper =
                new HttpMessageBodyResponseWrapper(closeableHttpResponse);
            errorResponse(httpMessageBodyResponseWrapper);
            return response(httpMessageBodyResponseWrapper, requestHttpEntity);
        } catch (Exception exception) {
            if (exception instanceof CliviaHttpClientException) {
                CliviaHttpClientException cliviaHttpClientException1 = (CliviaHttpClientException)exception;
                return new CliviaHttpResponse(cliviaHttpClientException1.getStatusCode(),
                    cliviaHttpClientException1.getMessage(), cliviaHttpClientException1.getBody(),
                    cliviaHttpClientException1.getHttpHeader());
            } else {
                return new CliviaHttpResponse(500, "调用http请求发生异常", null, requestHttpEntity.getHttpHeaders());
            }
        }

    }

    /**
     * handle error response
     * 
     * @author palading_cr
     *
     */
    private void errorResponse(HttpMessageBodyResponseWrapper httpMessageBodyResponseWrapper) throws Exception {
        if (responseErrorHandler.hasError(httpMessageBodyResponseWrapper)) {
            responseErrorHandler.handleError(httpMessageBodyResponseWrapper);
        }
    }

    /**
     * create HttpRequestBase
     *
     * @author palading_cr
     *
     */
    private HttpRequestBase httpRequestBase(CliviaHttpRequest cliviaHttpRequest) throws Exception {
        HttpRequestBaseBuilderFacade httpRequestBaseBuilderFacade =
            new HttpRequestBaseBuilderFacade(cliviaHttpRequest, httpRequestBaseFactory);
        return httpRequestBaseBuilderFacade.setHttpRequestBase();
    }

    /**
     * handle response
     *
     * @author palading_cr
     *
     */
    private CliviaHttpResponse response(HttpMessageBodyResponseWrapper httpMessageBodyResponseWrapper,
                                                                       CliviaHttpRequest cliviaHttpRequest) throws Exception {
        ResponseHandler responseHandler = cliviaResponseSelectLoader.select(cliviaHttpRequest.getResponseType());
        return responseHandler.handleResppnse(httpMessageBodyResponseWrapper, (Class)cliviaHttpRequest.getResponseType());

    }

    /**
     * close client
     * 
     * @author palading_cr
     *
     */
    @Override
    public void close() throws IOException {
        httpClientManager.buildCloseableHttpClient().close();
    }
}
