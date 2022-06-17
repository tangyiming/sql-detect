package com.tangym.sql.plugin.v5;

import com.tangym.sql.plugin.v5.util.DataPool;
import io.manbang.gravity.plugin.Advice;
import io.manbang.gravity.plugin.ExecuteContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author :       yiming.tang
 */
@Slf4j
public class MybatisMapperProxyAdvice implements Advice {
    @Override
    public void enterMethod(ExecuteContext context) {
        Method method = context.getArgument(1);
        String mapper = method.getDeclaringClass().getName();
        String sqlId = method.getName();
        DataPool.sqlIdThreadLocal.set(mapper+"#"+sqlId);
    }

    @Override
    public void exitMethod(ExecuteContext context) {
        DataPool.sqlIdThreadLocal.remove();
    }
}
