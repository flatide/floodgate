package com.flatide.floodgate.agent;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class AgentContext {
    private static final AgentContext instance = new AgentContext();

    Map<String, Object> items = new HashMap<>();

    public static AgentContext shared() {
        return instance;
    }

    public void addObject(String key, Object value) {
        items.put(key, value);
    }

    public Object getObject(String key) {
        return items.get(key);
    }
}
