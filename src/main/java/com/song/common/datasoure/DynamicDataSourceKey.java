package com.song.common.datasoure;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * dynamic data source key
 */
public class DynamicDataSourceKey {

    //read data sources
    private Map<String, String> readDataSourceMap = Maps.newHashMap();

    // write data sources;

    private String writeDataSource = null;

    public DynamicDataSourceKey() {
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
