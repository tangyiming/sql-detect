package com.tangym.sql.server.config;

import com.github.jaemon.dinger.support.CustomMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;

@Configuration
public class SqlExplainDingConfig {
    @Bean
    public CustomMessage markDownMessage() {
        return (projectId, request) ->
                MessageFormat.format("### 【慢SQL预警】\n{0}", request.getContent());
    }
}
