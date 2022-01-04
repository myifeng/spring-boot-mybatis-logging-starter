package io.github.myifeng.mybatis.logging;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@AutoConfigureOrder(Integer.MAX_VALUE)
@ConditionalOnClass(SqlSessionFactory.class)
@ConditionalOnBean(LoggingInterceptor.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class MybatisLoggingAutoConfiguration {

    private final LoggingInterceptor loggingInterceptor;

    private final List<SqlSessionFactory> sqlSessionFactoryList;

    public MybatisLoggingAutoConfiguration(LoggingInterceptor loggingInterceptor, List<SqlSessionFactory> sqlSessionFactoryList) {
        this.loggingInterceptor = loggingInterceptor;
        this.sqlSessionFactoryList = sqlSessionFactoryList;
    }

    @PostConstruct
    public void addPrintInterceptor() {
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(loggingInterceptor);
        }
    }

}
