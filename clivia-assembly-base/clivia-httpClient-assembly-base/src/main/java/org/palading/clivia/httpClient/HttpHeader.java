package org.palading.clivia.httpClient;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author palading_cr
 * @title HttpHeader
 * @project clivia-gateway
 */
public class HttpHeader {

    private HttpHeader httpHeader;
    private Map<String, String> header;

    /**
     * @author palading_cr
     *
     */
    public HttpHeader buildBasicHeader() {
        httpHeader = new HttpHeader();
        header = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        return this;
    }

    public String getContentType() {
        return header.getOrDefault("Content-Type", "application/json;charset=UTF-8");
    }

    public HttpHeader addHeader(String headerName, String headerVal) {
        header.put(headerName, headerVal);
        return this;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public HttpHeader setHeaders(Map<String, String> header) {
        this.header = header;
        return this;
    }

}
