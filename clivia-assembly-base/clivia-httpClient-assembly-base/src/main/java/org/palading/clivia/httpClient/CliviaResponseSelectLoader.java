package org.palading.clivia.httpClient;

import org.palading.clivia.httpClient.response.ResponseHandler;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * @author palading_cr
 * @title CliviaResponseSelectLoader
 * @project clivia-gateway
 */
public class CliviaResponseSelectLoader {
    private Map<String, ResponseHandler> BUFFER_STATIC_RESP_HANDLER = new ConcurrentHashMap<String, ResponseHandler>();

    public CliviaResponseSelectLoader(Map<String, ResponseHandler> responseHandlerMap) {
        initResponseHandlerCache(responseHandlerMap);
    }

    public ResponseHandler select(Type type) {
        if (null == type) {
            return BUFFER_STATIC_RESP_HANDLER.get("default_respHander");
        }
        return BUFFER_STATIC_RESP_HANDLER.getOrDefault(type.getTypeName(), BUFFER_STATIC_RESP_HANDLER.get("default_respHander"));
    }

    private void initResponseHandlerCache(Map<String, ResponseHandler> responseHandlerMap) {
        for (Map.Entry<String, ResponseHandler> item : responseHandlerMap.entrySet()) {
            ResponseHandler responseHandler = item.getValue();
            BUFFER_STATIC_RESP_HANDLER.putIfAbsent(responseHandler.getHandlerType(), responseHandler);
        }
    }
}
