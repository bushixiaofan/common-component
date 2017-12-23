package com.song.common.datasoure;

/**
 * data source holder
 */
public class DataSourceHolder {

    // thread local data source
    private static final ThreadLocal<String> DATA_SOURCE = new ThreadLocal<String>();

    // set data source to thread local
    public static void setDataSource(String customDataSource) {
        DATA_SOURCE.set(customDataSource);
    }

    // get data source from thread local
    public static String getDataSource() {
        return DATA_SOURCE.get();
    }

    // remove data source form thread local
    public static void removeDataSource() {
        DATA_SOURCE.remove();
    }

}
