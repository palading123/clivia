package org.palading.clivia.invoke.common;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
/**
 * @author palading_cr
 * @title CliviaDefaultInvokerWraperFactory
 * @project clivia
 */
public class CliviaDefaultInvokerWraperFactory extends CliviaAbstractInvokerFactory{

    @Override
    public String wraperName() {
        return "clivia_default_invoker";
    }


    @Override
    public Mono<Void> invoke(ServerWebExchange serverWebExchange, CliviaInvokerWraper cliviaInvokerWraper) {
        return cliviaInvokerWraper.invoke(serverWebExchange);
    }
}

