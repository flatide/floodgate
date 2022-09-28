package com.flatide.floodgate.system.datasource;

import java.util.List;
import java.util.Map;

public class FDataSourceDefault implements FDataSource {
    String name;

    public FDataSourceDefault() {
        this.name = "Default";
    }

    public FDataSourceDefault(String name) {
        this.name = name;
    }

    @Override
    public boolean connect() throws Exception {
        return false;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAllKeys(String tableName, String keyColumn) throws Exception {
        return null;
    }

    @Override
    public boolean create(String key) {
        return false;
    }

    @Override
    public Map<String, Object> read(String tableName, String keyColumn, String key) throws Exception {
        return null;
    }

    @Override
    public boolean insert(String tableName, String keyColumn, Map<String, Object> row) throws Exception {
        return false;
    }

    @Override
    public boolean update(String tableName, String keyColumn, Map<String, Object> row) throws Exception {
        return false;
    }

    @Override
    public boolean delete(String tableName, String keyColumn, String key, boolean backup) throws Exception {
        return false;
    }

    @Override
    public int deleteAll() {
        return 0;
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }
}
