package org.palading.clivia.invoke.websocket;

import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.Lifecycle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * @link org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
 * @title HandshakeWebSocketService
 * @project clivia
 */
public class HandshakeWebSocketService implements WebSocketService, Lifecycle {

    protected static final Log logger = LogFactory.getLog(HandshakeWebSocketService.class);

    private static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";

    private static final Mono<Map<String, Object>> EMPTY_ATTRIBUTES = Mono.just(Collections.emptyMap());

    private static final boolean tomcatPresent;

    private static final boolean jettyPresent;

    private static final boolean undertowPresent;

    private static final boolean reactorNettyPresent;

    static {
        ClassLoader classLoader =
            org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService.class.getClassLoader();
        tomcatPresent = ClassUtils.isPresent("org.apache.tomcat.websocket.server.WsHttpUpgradeHandler", classLoader);
        jettyPresent = ClassUtils.isPresent("org.eclipse.jetty.websocket.server.WebSocketServerFactory", classLoader);
        undertowPresent = ClassUtils.isPresent("io.undertow.websockets.WebSocketProtocolHandshakeHandler", classLoader);
        reactorNettyPresent = ClassUtils.isPresent("reactor.netty.http.server.HttpServerResponse", classLoader);
    }

    private final RequestUpgradeStrategy upgradeStrategy;

    @Nullable
    private Predicate<String> sessionAttributePredicate;

    private volatile boolean running = false;

    /**
     * Default constructor automatic, classpath detection based discovery of the {@link RequestUpgradeStrategy} to use.
     */
    public HandshakeWebSocketService() {
        this(initUpgradeStrategy());
    }

    /**
     * Alternative constructor with the {@link RequestUpgradeStrategy} to use.
     * 
     * @param upgradeStrategy
     *            the strategy to use
     */
    public HandshakeWebSocketService(RequestUpgradeStrategy upgradeStrategy) {
        Assert.notNull(upgradeStrategy, "RequestUpgradeStrategy is required");
        this.upgradeStrategy = upgradeStrategy;
    }

    private static RequestUpgradeStrategy initUpgradeStrategy() {
        String className;
        if (tomcatPresent) {
            className = "TomcatRequestUpgradeStrategy";
        } else if (jettyPresent) {
            className = "JettyRequestUpgradeStrategy";
        } else if (undertowPresent) {
            className = "UndertowRequestUpgradeStrategy";
        } else if (reactorNettyPresent) {
            // As late as possible (Reactor Netty commonly used for WebClient)
            className = "ReactorNettyRequestUpgradeStrategy";
        } else {
            throw new IllegalStateException("No suitable default RequestUpgradeStrategy found");
        }

        try {
            className = "org.springframework.web.reactive.socket.server.upgrade." + className;
            Class<?> clazz =
                ClassUtils.forName(className,
                    org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService.class.getClassLoader());
            return (RequestUpgradeStrategy)ReflectionUtils.accessibleConstructor(clazz).newInstance();
        } catch (Throwable ex) {
            throw new IllegalStateException("Failed to instantiate RequestUpgradeStrategy: " + className, ex);
        }
    }

    /**
     * Return the {@link RequestUpgradeStrategy} for WebSocket requests.
     */
    public RequestUpgradeStrategy getUpgradeStrategy() {
        return this.upgradeStrategy;
    }

    /**
     * Return the configured predicate for initialization WebSocket session attributes from {@code WebSession}
     * attributes.
     *
     * @since 5.1
     */
    @Nullable
    public Predicate<String> getSessionAttributePredicate() {
        return this.sessionAttributePredicate;
    }

    /**
     * Configure a predicate to use to extract {@link org.springframework.web.server.WebSession WebSession} attributes
     * and use them to initialize the WebSocket session with.
     * <p>
     * By default this is not set in which case no attributes are passed.
     *
     * @param predicate
     *            the predicate
     * @since 5.1
     */
    public void setSessionAttributePredicate(@Nullable Predicate<String> predicate) {
        this.sessionAttributePredicate = predicate;
    }

    @Override
    public void start() {
        if (!isRunning()) {
            this.running = true;
            doStart();
        }
    }

    protected void doStart() {
        if (getUpgradeStrategy() instanceof Lifecycle) {
            ((Lifecycle)getUpgradeStrategy()).start();
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            this.running = false;
            doStop();
        }
    }

    protected void doStop() {
        if (getUpgradeStrategy() instanceof Lifecycle) {
            ((Lifecycle)getUpgradeStrategy()).stop();
        }
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public Mono<Void> handleRequest(ServerWebExchange exchange, WebSocketHandler handler) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String protocol = selectProtocol(headers, handler);
        return initAttributes(exchange).flatMap(
            attributes -> this.upgradeStrategy.upgrade(exchange, handler, protocol,
                () -> createHandshakeInfo(exchange, request, protocol, attributes)));
    }

    @Nullable
    private String selectProtocol(HttpHeaders headers, WebSocketHandler handler) {
        String protocolHeader = headers.getFirst(SEC_WEBSOCKET_PROTOCOL);
        if (protocolHeader != null) {
            List<String> supportedProtocols = handler.getSubProtocols();
            for (String protocol : StringUtils.commaDelimitedListToStringArray(protocolHeader)) {
                if (supportedProtocols.contains(protocol)) {
                    return protocol;
                }
            }
        }
        return null;
    }

    private Mono<Map<String, Object>> initAttributes(ServerWebExchange exchange) {
        if (this.sessionAttributePredicate == null) {
            return EMPTY_ATTRIBUTES;
        }
        return exchange.getSession().map(
            session -> session.getAttributes().entrySet().stream()
                .filter(entry -> this.sessionAttributePredicate.test(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private HandshakeInfo createHandshakeInfo(ServerWebExchange exchange, ServerHttpRequest request, @Nullable String protocol,
        Map<String, Object> attributes) {

        URI uri = request.getURI();
        // Copy request headers, as they might be pooled and recycled by
        // the server implementation once the handshake HTTP exchange is done.
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(request.getHeaders());
        Mono<Principal> principal = exchange.getPrincipal();
        String logPrefix = exchange.getLogPrefix();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        return new HandshakeInfo(uri, headers, principal, protocol, remoteAddress, attributes, logPrefix);
    }

}
