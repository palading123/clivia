package org.palading.clivia.httpClient;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

/**
 * @author palading_cr
 * @title HttpHeader
 * @project clivia-gateway
 */
public class HttpHeader {

    private Map<String, String> header;

    /**
     * @author palading_cr
     *
     */
    public HttpHeader() {
        header = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
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

    public String getCharset() {
        String charset = header.get("Accept-Charset");
        if (StringUtils.isEmpty(charset)) {
            String contentType = header.get("Content-Type");
            String[] splits = contentType.split(";");
            if (null == splits || splits.length < 1) {
                return "UTF-8";
            }
            for (String content : splits) {
                if (content.startsWith("charset=")) {
                    charset = content.substring("charset=".length());
                }
            }
        }
        return StringUtils.isEmpty(charset) ? "UTF-8" : charset;
    }

}
