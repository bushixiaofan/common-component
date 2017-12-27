package com.song.common.datasoure;

import com.google.common.collect.Maps;
import com.song.common.utils.RandomUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.PatternMatchUtils;

import java.util.Map;
import java.util.Set;

/**
 * dynamic data source interceptor
 */
public class DynamicDataSourceInterceptor implements MethodInterceptor{

    // 数据源key的存储控制器
    private DynamicDataSourceKey dataSourceKey = null;

    // 方法和使用数据源key的对应关系
    private Map<String, String> attributes = Maps.newHashMap();

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        // 扫描注解设置数据源
        if (methodInvocation.getMethod().isAnnotationPresent(DataSource.class)) {
            DataSource dataSource = methodInvocation.getMethod().getAnnotation(DataSource.class);
            if (dataSource.value().equalsIgnoreCase("WRITE")) {
                setWriteDataSource();
            } else {
                setReadDataSource();
            }
            DataSourceHolder.setDataSource(dataSource.value());
        } else { // 没有注解通过匹配方法名称动态设置数据源
            String key = "";
            if (attributes != null && attributes.size() != 0) {
                // 找到最匹配的方法对应的key
                String bestNameMatch = null;
                for (String name : attributes.keySet()) {
                    if (isMatch(name, methodInvocation.getMethod().getName())) {
                        bestNameMatch = name;
                    }
                }
                if (bestNameMatch != null) {
                    key = attributes.get(bestNameMatch);
                }
            }

            if ("READ".equalsIgnoreCase(key)) {
                setReadDataSource();
            } else if ("WRITE".equalsIgnoreCase(key)) {
                setWriteDataSource();
            }
        }

        return methodInvocation.proceed();

    }

    private void setReadDataSource() {
        // not null set read data source
        if (dataSourceKey != null && dataSourceKey.getReadDateSourceKey() != null) {
            // 随机选取一个从（读）库数据源
            DataSourceHolder.setDataSource(dataSourceKey.getReadDateSourceKey());
        }
    }

    private void setWriteDataSource() {
        // not null set write data source
        if (dataSourceKey != null && dataSourceKey.getWriteDataSourceKey() != null) {
            DataSourceHolder.setDataSource(dataSourceKey.getWriteDataSourceKey());
        }
    }

    private boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    public DynamicDataSourceKey getDataSourceKey() {
        return dataSourceKey;
    }

    public void setDataSourceKey(DynamicDataSourceKey dataSourceKey) {
        this.dataSourceKey = dataSourceKey;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
