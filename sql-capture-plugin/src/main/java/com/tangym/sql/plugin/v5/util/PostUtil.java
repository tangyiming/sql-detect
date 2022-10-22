package com.tangym.sql.plugin.v5.util;

import com.alibaba.fastjson.JSON;
import com.tangym.sql.plugin.v5.model.SqlExplainInfo;
import io.manbang.gravity.plugin.AgentOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * @author :       yiming.tang
 */
@Slf4j
public class PostUtil {
    static void process(SqlExplainInfo info) {
        // 根据生产情况调整域名配置
        String serverUrl = "";
        if (AgentOptions.INSTANCE.isLocalDebug()) {
            serverUrl = "http://127.0.0.1:8080/detect";
        }
        HttpUtil.post(String.format("%s/sql/add", serverUrl), JSON.toJSONString(info));
    }
}
