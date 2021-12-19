/**
 * @author palading_cr
 * @title CliviaInvokerWraperFactory
 * @project clivia
 */
package org.palading.clivia.invoke.common;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface CliviaInvokerWraperFactory {
     CliviaInvokerWraper create();
     Mono<Void> invoke(ServerWebExchange serverWebExchange,CliviaInvokerWraper cliviaInvokerWraper);

}
