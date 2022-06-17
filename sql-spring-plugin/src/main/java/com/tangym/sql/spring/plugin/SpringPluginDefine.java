package com.tangym.sql.spring.plugin;

import com.google.auto.service.AutoService;

import io.manbang.gravity.plugin.Plugin;
import io.manbang.gravity.plugin.PluginDefine;
import io.manbang.gravity.plugin.Witness;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

@AutoService(PluginDefine.class)
public class SpringPluginDefine implements PluginDefine {
    @Override
    public ElementMatcher<TypeDescription> getTypeMatcher() {
        return ElementMatchers.named("org.springframework.context.support.AbstractApplicationContext");
    }

    @Override
    public Plugin[] getPlugins() {
        return Plugin.builder()
                .advice(ElementMatchers.named("prepareBeanFactory"), "com.tangym.sql.spring.plugin.RegisterBeanAdvice")
                .build();
    }

    @Override
    public Witness getWitness() {
        return Witness.classes("org.springframework.context.support.AbstractApplicationContext");
    }
}
