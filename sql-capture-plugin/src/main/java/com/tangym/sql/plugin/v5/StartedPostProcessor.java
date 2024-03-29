package com.tangym.sql.plugin.v5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;
import com.tangym.sql.plugin.v5.util.DataPool;
import com.tangym.sql.plugin.v5.util.HttpUtil;
import com.tangym.sql.plugin.v5.util.SqlPostTask;
import com.tangym.sql.spring.plugin.BeanDefinitionRegistryProcessor;
import io.manbang.gravity.plugin.AgentOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * @author :       yiming.tang
 */
@AutoService(BeanDefinitionRegistryProcessor.class)
@Slf4j
public class StartedPostProcessor implements BeanDefinitionRegistryProcessor {

    @Override
    public void process(BeanDefinitionRegistry registry) {
        // todo 根据生产情况调整域名配置
        String serverUrl = "";
        if (AgentOptions.INSTANCE.isLocalDebug()) {
            serverUrl = "http://127.0.0.1:8080/detect";
        }
        String detail = HttpUtil.get(String.format("%s/sql/config/rules/capture", serverUrl));
        if (null != detail) {
            JSONObject jsonObject = JSON.parseObject(detail);
            Object capture = jsonObject.get("capture");
            JSONArray ja = JSONArray.parseArray(capture.toString());
            if (ja.size() > 0) DataPool.captureRules = ja.toJavaList(String.class);
        }

        SqlPostTask.start();
    }

}
