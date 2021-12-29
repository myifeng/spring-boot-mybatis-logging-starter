package io.github.myifeng.mybatis.logging.handler;

public interface LoggingHandler {

    boolean preHandler();

    void postHandle(String sql);

}
