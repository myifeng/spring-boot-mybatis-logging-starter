package io.github.myifeng.mybatis.logging;

import io.github.myifeng.mybatis.logging.handler.LoggingHandler;
import io.github.myifeng.mybatis.logging.utils.SQLParserUtil;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

@Component
@ConditionalOnBean(LoggingHandler.class)
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class LoggingInterceptor implements Interceptor, Ordered {

    private final List<LoggingHandler> handlers;

    public LoggingInterceptor(List<LoggingHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            String sql = SQLParserUtil.parseSql(invocation);
            for(LoggingHandler handler: handlers){
                if (handler.preHandle()) {
                    handler.postHandle(sql);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof RoutingStatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}