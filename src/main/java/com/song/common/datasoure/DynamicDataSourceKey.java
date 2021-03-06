package com.song.common.datasoure;

import com.google.common.collect.Maps;
import com.song.common.utils.RandomUtils;

import java.util.Map;

/**
 * dynamic data source key
 */
public class DynamicDataSourceKey {

    //read data sources （slaves）
    private Map<String, String> readDataSourceMap = Maps.newHashMap();

    // write data sources （master)
    private String writeDataSource = null;

    public String getReadDateSourceKey() {
        return RandomUtils.getRandomElement(readDataSourceMap.values());
    }

    public String getWriteDataSourceKey() {
        return writeDataSource;
    }

    public Map<String, String> getReadDataSourceMap() {
        return readDataSourceMap;
    }

    public void setReadDataSourceMap(Map<String, String> readDataSourceMap) {
        this.readDataSourceMap = readDataSourceMap;
    }

    public String getWriteDataSource() {
        return writeDataSource;
    }

    public void setWriteDataSource(String writeDataSource) {
        this.writeDataSource = writeDataSource;
    }
}
