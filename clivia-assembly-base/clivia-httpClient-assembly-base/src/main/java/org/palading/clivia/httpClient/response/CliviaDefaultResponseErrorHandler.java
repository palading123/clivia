package org.palading.clivia.httpClient.response;

import java.io.*;
import java.nio.CharBuffer;

import org.apache.commons.lang3.ObjectUtils;
import org.palading.clivia.httpClient.CliviaHttpClientException;
import org.palading.clivia.httpClient.HttpClientResponse;
import org.palading.clivia.httpClient.HttpHeader;
import org.palading.clivia.httpClient.HttpStatus;


/**
 * @author palading_cr
 * @title CliviaDefaultResponseErrorHandler
 * @project clivia-gateway
 */
public class CliviaDefaultResponseErrorHandler implements ResponseErrorHandler {

    public static final int BUFFER_SIZE = 4096;

    private static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean hasError(HttpClientResponse response) throws IOException {
        int statusCode = response.getStatusCode();
        HttpStatus status = HttpStatus.resolve(statusCode);
        return (status != null ? hasError(status) : hasError(statusCode));
    }

    protected boolean hasError(HttpStatus statusCode) {
        return statusCode.isError();
    }

    protected boolean hasError(int unknownStatusCode) {
        HttpStatus.Series series = HttpStatus.Series.resolve(unknownStatusCode);
        return (series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(HttpClientResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getStatusCode());
        if (statusCode == null) {
            byte[] body = getByteBody(response.getBody());
           HttpHeader httpHeader = response.getHttpHeader();
            String message = getErrorMessage(response.getStatusCode(), response.getStatusText(), body, httpHeader.getCharset());
            throw new CliviaHttpClientException("clivia http status code not exists :[" + message + "]", statusCode.value(),
                httpHeader, body);

        }
        handleError(response, statusCode);
    }

    protected void handleError(HttpClientResponse response, HttpStatus statusCode) throws IOException {
        String statusText = response.getStatusText();
        HttpHeader headers = response.getHttpHeader();
        byte[] body = getByteBody(response.getBody());
        String charset = headers.getCharset();
        String message = getErrorMessage(statusCode.value(), statusText, body, charset);
        switch (statusCode.series()) {
            case CLIENT_ERROR:
                throw new CliviaHttpClientException("clivia http client error:[" + message + "]", statusCode.value(), headers,
                    body);
            case SERVER_ERROR:
                throw new CliviaHttpClientException("clivia http server error:[" + message + "]", statusCode.value(), headers,
                    body);
            default:
                throw new CliviaHttpClientException("clivia http status code not exists :[" + message + "]", statusCode.value(),
                    headers, body);
        }
    }

    private String getErrorMessage(int rawStatusCode, String statusText, byte[] responseBody, String charset) {
        String preface = rawStatusCode + " " + statusText + ": ";
        if (ObjectUtils.isEmpty(responseBody)) {
            return preface + "[no body]";
        }
        int maxChars = 200;
        try {
            if (responseBody.length < maxChars * 2) {
                return preface + "[" + new String(responseBody, charset) + "]";
            }

            Reader reader = new InputStreamReader(new ByteArrayInputStream(responseBody), charset);
            CharBuffer buffer = CharBuffer.allocate(maxChars);
            reader.read(buffer);
            reader.close();
            buffer.flip();
            return preface + "[" + buffer.toString() + "... (" + responseBody.length + " bytes)]";
        } catch (IOException ex) {
            // should never happen
            throw new IllegalStateException(ex);
        }
    }

    private byte[] getByteBody(InputStream inputStream) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return out.toByteArray();
        } catch (Exception e) {

        } finally {
            close(inputStream);
            close(out);
        }
        return new byte[0];
    }
}
