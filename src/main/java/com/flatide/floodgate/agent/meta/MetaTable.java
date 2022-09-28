package com.flatide.floodgate.agent.meta;

import java.util.LinkedHashMap;
import java.util.Map;

public class MetaTable {
    Map<String, Map<String, Object>> rows = new LinkedHashMap<>();


    public int size() {
        return this.rows.size();
    }

    public Map<String, Object> get(String key) {
        return this.rows.get(key);
    }

    public void put(String key, Map<String, Object> row) {
        this.rows.put(key, row);
    }

    public void remove(String key) {
        this.rows.remove(key);
    }

    public Map<String, Map<String, Object>> getRows() {
        return this.rows;
    }
}

