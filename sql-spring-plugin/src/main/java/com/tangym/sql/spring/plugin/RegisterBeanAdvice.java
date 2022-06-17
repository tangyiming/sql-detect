package com.tangym.sql.spring.plugin;

import io.manbang.gravity.plugin.Advice;
import io.manbang.gravity.plugin.ExecuteContext;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.ServiceLoader;

public class RegisterBeanAdvice implements Advice {
    @Override
    public void exitMethod(ExecuteContext context) {
        BeanDefinitionRegistry registry = context.getArgument();

        ClassLoader classLoader = context.getTargetClass().getClassLoader();
        ServiceLoader<BeanDefinitionRegistryProcessor> processors = ServiceLoader.load(BeanDefinitionRegistryProcessor.class, getClass().getClassLoader());
        processors.forEach(p -> {
            if (p.getWitness().saw(classLoader)) {
                p.process(registry);
            }
        });
    }

    @Override
    public boolean isSwitchable() {
        return false;
    }
}
