package org.palading.clivia.request.build;

import org.palading.clivia.common.api.CliviaServerProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * @author palading_cr
 * @title ContextBuilder
 * @project clivia
 */
public interface ContextBuilder<T> {

    static final String IP_UNKNOWN = "unknown";
    static final String IP_LOCAL = "127.0.0.1";
    static final String IPV6_LOCAL = "0:0:0:0:0:0:0:1";
    static final int IP_LEN = 15;
    static final String WEB_SOCKET = "websocket";

    public T contextBuild(ServerWebExchange serverWebExchange, T t, CliviaServerProperties cliviaServerProperties);

    public boolean type(ServerWebExchange serverWebExchange);

    default String getPath(final ServerWebExchange exchange) {
        return exchange.getRequest().getURI().getPath();
    }

    default String getVersion(final ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("version")).orElse("v1.0");
    }

    default String getSign(final ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("signature")).orElse(null);
    }

    default String getAppKey(final ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("appKey")).orElse(null);
    }

    default String getGroup(final ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("group")).orElse("default");
    }

    default String getNonce(final ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("nonce")).orElse("120Lkjg#");
    }

    default boolean webSocket(final ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("Upgrade"))
            .map(header -> header.equals(WEB_SOCKET)).orElse(false);
    }

    default String getRemoteAddr(final ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String ipAddress = headers.getFirst("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = headers.getFirst("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress =
                Optional.ofNullable(request.getRemoteAddress()).map(address -> address.getAddress().getHostAddress()).orElse("");
            if (IP_LOCAL.equals(ipAddress) || IPV6_LOCAL.equals(ipAddress)) {
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    return "127.0.0.1";
                }
            }
        }
        if (ipAddress != null && ipAddress.length() > IP_LEN) {
            int index = ipAddress.indexOf(",");
            if (index > 0) {
                ipAddress = ipAddress.substring(0, index);
            }
        }
        return ipAddress;
    }

    default String buildRealPath(String serverPath, CliviaServerProperties cliviaServerProperties) {
        String serverName = cliviaServerProperties.getServerName();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(serverName)) {
            serverName = "/".concat(serverName);
            if (serverPath.startsWith(serverName)) {
                return serverPath.substring(serverName.length());
            }
        }
        return serverPath;
    }
}
