package com.tangym.sql.server.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 慢查规则配置
 */
public class SqlExplainRules {
    public static Set<String> KEYSET = new HashSet<>(Arrays.asList("NULL"));

    public static Set<String> TYPESET = new HashSet<>(Arrays.asList("index", "ALL"));

    public static Set<String> SCRIPTSET = new HashSet<>();
}
