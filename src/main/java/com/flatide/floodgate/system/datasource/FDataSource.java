package com.flatide.floodgate.system.datasource;

import java.util.List;
import java.util.Map;

public interface FDataSource {
    boolean connect() throws Exception;

    void setName(String name);
    String getName();
    List<String> getAllKeys(String tableName, String keyColumn) throws Exception;

    boolean create(String key);
    Map<String, Object> read(String tableName, String keyColumn, String key) throws Exception;
    boolean insert(String tableName, String keyColumn, Map<String, Object> row) throws Exception;
    boolean update(String tableName, String keyColumn, Map<String, Object> row) throws Exception;
    boolean delete(String tableName, String keyColumn, String key, boolean backup) throws Exception;

    int deleteAll() throws Exception;
    void flush() throws Exception;
    void close();
}
