package com.tangym.sql.spring.plugin;

import io.manbang.gravity.plugin.Witness;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * {@link BeanDefinitionRegistry} 拦截处理器，可以用来注册Spring的Bean定义
 */
public interface BeanDefinitionRegistryProcessor {
    /**
     * Bean注册的目击者
     *
     * @return 目击者
     */
    default Witness getWitness() {
        return Witness.always();
    }

    /**
     * 处理指定的Bean注册表，可以给它注册自己的 {@link org.springframework.beans.factory.config.BeanDefinition BeanDefinition}
     *
     * @param registry Bean定义注册表
     */
    void process(BeanDefinitionRegistry registry);
}
