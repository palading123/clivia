package org.palading.clivia.httpClient.response;

import org.palading.clivia.httpClient.HttpClientResponse;
import org.palading.clivia.httpClient.HttpHeader;
import org.palading.clivia.httpClient.ObjectMapperUtil;

import java.io.InputStream;


/**
 * @author palading_cr
 * @title CliviaJsonResponseHandler
 * @project clivia-gateway
 */
public class CliviaDefaultJsonResponseHandler<T> implements ResponseHandler<T> {

    @Override
    public <T> CliviaHttpResponse handleResppnse(HttpClientResponse httpResponse, Class<T> responseType) throws Exception {
        HttpHeader httpHeader = httpResponse.getHttpHeader();
        InputStream inputStream = httpResponse.getBody();
        if (null == inputStream) {
            return new CliviaHttpResponse(httpResponse.getStatusCode(), httpResponse.getStatusText(), null, httpHeader);
        }
        T body = ObjectMapperUtil.inputStreamToObject(inputStream, responseType);
        return new CliviaHttpResponse(httpResponse.getStatusCode(), httpResponse.getStatusText(), body, httpHeader);
    }

    @Override
    public String getHandlerType() {
        return "default_respHander";
    }
}
