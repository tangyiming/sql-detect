package com.tangym.sql.plugin.v5;

import com.google.auto.service.AutoService;
import io.manbang.gravity.plugin.Plugin;
import io.manbang.gravity.plugin.PluginDefine;
import io.manbang.gravity.plugin.Witness;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 增强mybatis，获取sql id
 * @author :       yiming.tang
 */
@AutoService(PluginDefine.class)
public class MybatisPluginDefine implements PluginDefine {
    private static final String MAPPER_PROXY_CLASS_NAME = "org.apache.ibatis.binding.MapperProxy";

    @Override
    public ElementMatcher<TypeDescription> getTypeMatcher() {
        return named(MAPPER_PROXY_CLASS_NAME);
    }

    @Override
    public Plugin[] getPlugins() {
        return new Plugin[]{
                Plugin.advice(named("invoke"),
                       MybatisMapperProxyAdvice.class.getName())
        };
    }

    @Override
    public Witness getWitness() {
        return Witness.classes(MAPPER_PROXY_CLASS_NAME);
    }
}
