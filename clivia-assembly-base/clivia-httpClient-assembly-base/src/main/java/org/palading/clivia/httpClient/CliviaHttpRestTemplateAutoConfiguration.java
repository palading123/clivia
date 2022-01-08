package org.palading.clivia.httpClient;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.palading.clivia.httpClient.request.CliviaDefaultHttpRequest;
import org.palading.clivia.httpClient.request.CliviaSyncHttpRequest;
import org.palading.clivia.httpClient.request.HttpRequestBaseFactory;
import org.palading.clivia.httpClient.response.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * http restTemplate autoConfiguration
 * 
 * @author palading_cr
 * @title CliviaHttpRestTemplateAutoConfiguration
 * @project clivia-gateway
 */
@Configuration
public class CliviaHttpRestTemplateAutoConfiguration {

    /**
     * httpClient config
     *
     * @author palading_cr
     *
     */

    @Bean(name = "httpClientConfig")
    @ConditionalOnClass(HttpClientConfig.class)
    @ConfigurationProperties(prefix = "clivia.httpclient.config")
    public HttpClientConfig httpClientConfig() {
        return new HttpClientConfig();
    }

    /**
     * httpClient manager build
     *
     * @author palading_cr
     *
     */
    @Bean
    HttpClientManager httpClientManager(ObjectProvider<HttpClientConfig> httpClientConfigs) {
        return new CliviaSimpleCloseableHttpClient(httpClientConfigs.getIfAvailable());
    }

    /**
     * init jsonResponseHandler
     *
     * @author palading_cr
     *
     */
    @Bean
    CliviaDefaultJsonResponseHandler cliviaDefaultJsonResponseHandler() {
        return new CliviaDefaultJsonResponseHandler();
    }

    /**
     * init stringResponseHandler
     *
     * @author palading_cr
     *
     */
    @Bean
    CliviaStringResponseHandler cliviaStringResponseHandler() {
        return new CliviaStringResponseHandler();
    }

    /**
     * build CliviaResponseSelectLoader
     *
     * @author palading_cr
     *
     */
    @Bean
    CliviaResponseSelectLoader cliviaResponseSelectLoader(Map<String, ResponseHandler> responseHandlerMap) {
        return new CliviaResponseSelectLoader(responseHandlerMap);
    }

    /**
     * init HttpRequestBaseFactory
     *
     * @author palading_cr
     *
     */
    @Bean
    HttpRequestBaseFactory httpRequestBaseFactory() {
        return new CliviaHttpRequestBaseFactory();
    }

    /**
     * init ResponseErrorHandler
     *
     * @author palading_cr
     *
     */
    @Bean
    ResponseErrorHandler responseErrorHandler() {
        return new CliviaDefaultResponseErrorHandler();
    }

    /**
     * init CliviaSyncHttpRequest
     *
     * @author palading_cr
     *
     */
    @Bean
    CliviaSyncHttpRequest cliviaSyncHttpRequest(ObjectProvider<HttpRequestBaseFactory> httpRequestBaseFactories,
                                                ObjectProvider<CliviaResponseSelectLoader> cliviaResponseSelectLoaders,
                                                ObjectProvider<ResponseErrorHandler> responseErrorHandlers, ObjectProvider<HttpClientManager> httpClientManagers) {
        return new CliviaDefaultHttpRequest(httpRequestBaseFactories.getIfAvailable(),
            cliviaResponseSelectLoaders.getIfAvailable(), responseErrorHandlers.getIfAvailable(),
            httpClientManagers.getIfAvailable());
    }

    /**
     * HttpInterceptor
     * 
     * @author palading_cr
     *
     */
    @Bean
    HttpInterceptor httpInterceptor() {
        return new CliviaDefaultHttpInterceptor();
    }

    /**
     * init sync http template
     *
     * @author palading_cr
     *
     */
    @Bean
    CliviaSyncHttpRestTemplate cliviaHttpRestTemplate(ObjectProvider<List<HttpInterceptor>> httpInterceptors,
                                                                            ObjectProvider<CliviaSyncHttpRequest> cliviaSyncHttpRequests) {
        final List<HttpInterceptor> list =
            httpInterceptors.getIfAvailable().stream().sorted(Comparator.comparing(e -> e.order())).collect(Collectors.toList());
        return new CliviaHttpRestTemplate(cliviaSyncHttpRequests.getIfAvailable(), list);
    }
}
