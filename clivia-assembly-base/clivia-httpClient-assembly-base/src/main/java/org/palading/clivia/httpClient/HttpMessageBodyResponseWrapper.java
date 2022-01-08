package org.palading.clivia.httpClient;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.HttpClientUtils;

/**
 * @author palading_cr
 * @title HttpMessageBodyResponseWrapper
 * @project clivia-gateway
 */
public class HttpMessageBodyResponseWrapper implements HttpClientResponse {

    private HttpResponse httpClientResponse;

    public HttpMessageBodyResponseWrapper(HttpResponse httpClientResponse) {
        this.httpClientResponse = httpClientResponse;
    }

    @Override
    public int getStatusCode() throws IOException {
        return httpClientResponse.getStatusLine().getStatusCode();
    }

    @Override
    public InputStream getBody() throws IOException {
        return httpClientResponse.getEntity().getContent();
    }

    @Override
    public String getStatusText() throws IOException {
        return httpClientResponse.getStatusLine().getReasonPhrase();
    }

    @Override
    public void close() {
        if (null != httpClientResponse) {
            HttpClientUtils.closeQuietly(httpClientResponse);
        }
    }

    @Override
    public HttpHeader getHttpHeader() {
        HttpHeader httpHeader = null;
        Header[] headers = httpClientResponse.getAllHeaders();
        if (null != headers && headers.length > 0) {
            httpHeader = new HttpHeader();
            for (Header header : headers) {
                httpHeader.addHeader(header.getName(), header.getValue());
            }
        }
        return httpHeader;
    }
}
