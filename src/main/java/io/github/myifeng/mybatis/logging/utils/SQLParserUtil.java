package io.github.myifeng.mybatis.logging.utils;

import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

public class SQLParserUtil {

    public static String parseSql(Invocation invocation){
        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
            StatementHandler delegate = (StatementHandler) ReflectionUtil.getFieldValue(statementHandler, "delegate");
            MappedStatement mappedStatement = (MappedStatement) ReflectionUtil.getFieldValue(delegate, "mappedStatement");
            BoundSql boundSql = delegate.getBoundSql();
            return parseSql(boundSql, mappedStatement.getConfiguration());
        }
        return null;
    }

    public static String parseSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        if (!StringUtils.hasLength(sql) || configuration == null) {
            return "";
        }

        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        sql = beautifySql(sql);

        if (parameterMappings != null) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    String paramValueStr = "";
                    if (value instanceof String) {
                        paramValueStr = "'" + value + "'";
                    } else if (value instanceof Date) {
                        paramValueStr = "'" + simpleDateFormat.format(value) + "'";
                    } else {
                        paramValueStr = value + "";
                    }
                    sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(paramValueStr));
                }
            }
        }
        return sql;
    }

    private static String beautifySql(String sql) {
        sql = sql.replaceAll("[\\s\n ]+", " ");
        return sql;
    }
}
