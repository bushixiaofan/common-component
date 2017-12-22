package com.song.common.datasoure;

import com.song.common.datasoure.utils.DataSourceHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * dynamic datasource
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getDataSource();
    }
}
