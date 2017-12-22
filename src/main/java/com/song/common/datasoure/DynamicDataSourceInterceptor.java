package com.song.common.datasoure;

import com.song.common.datasoure.annotation.DataSource;
import com.song.common.datasoure.utils.DataSourceHolder;
import com.song.common.utils.RandomUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.util.Properties;

/**
 * dynamic data source interceptor
 */
public class DynamicDataSourceInterceptor implements MethodInterceptor{

    private DynamicDataSourceKey dataSourceKey = null;

    private  Properties attributes = null;

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        // scan method annotation and set datasource
        if (methodInvocation.getMethod().isAnnotationPresent(DataSource.class)) {
            DataSource dataSource = methodInvocation.getMethod().getAnnotation(DataSource.class);
            DataSourceHolder.setDataSource(dataSource.value());
        } else { // set data source by method name
            if (attributes != null && attributes.size() != 0) {
                for (String name : attributes.stringPropertyNames()) {
                    if (methodInvocation.getMethod().getName().contains(name)) {
                        setReadDataSource();
                    } else {
                        setWriteDataSource();
                    }
                }

            } else {
                setWriteDataSource();
            }
        }

        return methodInvocation.proceed();

    }

    private void setReadDataSource() {
        // not null set read data source
        if (dataSourceKey != null && dataSourceKey.getReadDataSourceMap() != null && dataSourceKey.getReadDataSourceMap().size() != 0) {
            DataSourceHolder.setDataSource(RandomUtils.getRandomElement(dataSourceKey.getReadDataSourceMap().values()));
        }
    }

    private void setWriteDataSource() {
        // not null set write data source
        if (dataSourceKey != null && dataSourceKey.getWriteDataSource() != null) {
            DataSourceHolder.setDataSource(dataSourceKey.getWriteDataSource());
        }
    }

    public DynamicDataSourceKey getDataSourceKey() {
        return dataSourceKey;
    }

    public void setDataSourceKey(DynamicDataSourceKey dataSourceKey) {
        this.dataSourceKey = dataSourceKey;
    }

    public Properties getAttributes() {
        return attributes;
    }

    public void setAttributes(Properties attributes) {
        this.attributes = attributes;
    }
}
