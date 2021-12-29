package io.github.myifeng.mybatis.logging.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultLoggingHandler implements LoggingHandler{

    private static final Logger logger = LoggerFactory.getLogger(DefaultLoggingHandler.class);

    @Override
    public boolean preHandler() {
        return true;
    }

    @Override
    public void postHandle(String sql) {
        logger.info(sql);
    }
}
