package com.tangym.sql.plugin.v5;

import com.google.auto.service.AutoService;
import io.manbang.gravity.plugin.Plugin;
import io.manbang.gravity.plugin.PluginDefine;
import io.manbang.gravity.plugin.Witness;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author :       yiming.tang
 */
@AutoService(PluginDefine.class)
public class Mysql5xPluginDefine implements PluginDefine {
    private static final String PREPARED_STATEMENT_CLASS_NAME = "com.mysql.jdbc.PreparedStatement";

    @Override
    public ElementMatcher<TypeDescription> getTypeMatcher() {
        return named(PREPARED_STATEMENT_CLASS_NAME);
    }

    @Override
    public Plugin[] getPlugins() {
        return new Plugin[]{
                Plugin.advice(named("execute").or(named("executeQuery"))
                                .or(named("executeUpdate"))
                                .or(named("executeLargeUpdate")),
                        PreparedStatement5xAdvice.class.getName())
        };
    }

    @Override
    public Witness getWitness() {
        return Witness.classes(PREPARED_STATEMENT_CLASS_NAME, "com.mysql.jdbc.MySQLConnection");
    }

}
