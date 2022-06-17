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
        String serverUrl = "https://www.xxx.com";
        if (AgentOptions.INSTANCE.isLocalDebug()) {
            serverUrl = "http://127.0.0.1:8080";
        }
        HttpUtil.post(String.format("%s/sql/add", serverUrl), JSON.toJSONString(info));
    }
}
