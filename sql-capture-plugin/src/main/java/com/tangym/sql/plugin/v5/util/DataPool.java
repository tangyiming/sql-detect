package com.tangym.sql.plugin.v5.util;

import com.tangym.sql.plugin.v5.model.SqlExplainInfo;
import io.manbang.gravity.plugin.AgentOptions;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author :       yiming.tang
 */
public class DataPool {

    public static String appName = AgentOptions.INSTANCE.getAppName();

    public static List<String> captureRules = Arrays.asList("select");

    public static LinkedBlockingQueue<SqlExplainInfo> linkedBlockingQueue = new LinkedBlockingQueue<>();

    public static ThreadLocal<String> sqlIdThreadLocal = new ThreadLocal<>();

}
