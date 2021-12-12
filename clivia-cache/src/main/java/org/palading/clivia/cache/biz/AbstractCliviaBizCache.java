package org.palading.clivia.cache.biz;

import org.palading.clivia.cache.CliviaStandandCacheFactory;
import org.palading.clivia.common.api.CliviaServerProperties;
import org.springframework.context.ApplicationContext;

/**
 * @author palading_cr
 * @title AbstractCliviaBizCache
 * @project clivia
 */
public abstract class AbstractCliviaBizCache {

    public ApplicationContext applicationContext() {
        CliviaStandandCacheFactory cliviaStandandCacheFactory = CliviaStandandCacheFactory.getCliviaStandandCacheFactory();
        return (ApplicationContext)cliviaStandandCacheFactory.get("springContext");
    }

    public CliviaServerProperties cliviaServerProperties() {
        ApplicationContext applicationContext = applicationContext();
        return applicationContext.getBean("cliviaServerProperties", CliviaServerProperties.class);
    }
}
