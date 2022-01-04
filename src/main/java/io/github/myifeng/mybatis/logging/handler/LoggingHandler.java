package io.github.myifeng.mybatis.logging.handler;

/**
 * Mybatis logging processing interface.
 * Implement this interface to get mybatis logging.
 */
public interface LoggingHandler {

    default boolean preHandle() {
        return true;
    }

    default void postHandle(String sql) {

    }

}
