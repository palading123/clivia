package org.palading.clivia.invoke.common;

import org.palading.clivia.cache.CliviaStandandCacheFactory;
/**
 * @author palading_cr
 * @title CliviaAbstractInvokerFactory
 * @project clivia
 */
public abstract class CliviaAbstractInvokerFactory implements CliviaInvokerWraperFactory{

    protected abstract String wraperName();

    @Override
    public CliviaInvokerWraper create() {
        CliviaStandandCacheFactory cliviaStandandCacheFactory = CliviaStandandCacheFactory.getCliviaStandandCacheFactory();
        CliviaInvokerWraper cliviaInvokerWraper = (CliviaInvokerWraper)cliviaStandandCacheFactory.get(wraperName());
        if(null != cliviaInvokerWraper){
            return cliviaInvokerWraper;
        }
        cliviaStandandCacheFactory.putIfAbsent(wraperName(),new CliviaCommonInvoker());
        return (CliviaInvokerWraper)cliviaStandandCacheFactory.get(wraperName());
    }
}
