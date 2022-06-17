package com.tangym.sql.plugin.v5.util;

/**
 * @author :       yiming.tang
 */
public class SqlFormatUtil {
    public static String formatSql(String sql) {
        String res;
        if (null != sql) {
            res = sql.replaceAll("[\r\n]", " ").replaceAll("\\s+", " ");
        } else {
            return null;
        }
        return res;
    }
}
