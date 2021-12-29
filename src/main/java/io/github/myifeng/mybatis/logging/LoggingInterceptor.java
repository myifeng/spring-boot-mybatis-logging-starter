package io.github.myifeng.mybatis.logging;

import io.github.myifeng.mybatis.logging.process.LoggingProcess;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.util.Properties;

@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class LoggingInterceptor implements Interceptor, Ordered {

    @Resource
    LoggingProcess process;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        record(invocation);
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

    private void record(Invocation invocation) {
        try {
            process.process(invocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}