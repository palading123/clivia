package org.palading.clivia.httpClient;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author palading_cr
 * @title HttpQuery
 * @project clivia-gateway
 */
public class HttpParam extends TreeMap {
    private Map<String, Object> param;

    public HttpParam() {
        param = new TreeMap<>();
    }

    public HttpParam addParam(String key, Object val) {
        param.put(key, val);
        return this;
    }

    public HttpParam addAll(Map<String, Object> param) {
        param.putAll(param);
        return this;
    }

    public Object getVal(String key) {
        return param.get(key);
    }

    /**
     * map to url
     *
     * @author palading_cr
     *
     */
    public String getUrlParam() {
        int size = param.size();
        StringBuilder stringBuilder = new StringBuilder();
        if (size > 0) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                if (size > 1) {
                    stringBuilder.append("&");
                }
                size--;
            }
        }
        return stringBuilder.toString();
    }

    public Map<String, Object> getParam() {
        return param;
    }
}
