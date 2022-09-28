package com.flatide.floodgate.agent;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix="floodgate")
public class Config {
    private Map<String, Object> config = new HashMap<>();

    public Map<String, Object> getConfig() {
        return this.config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public Object get(String path) {
        String[] keys = path.split("\\.");

        Object cur = this.config;
        for( int i = 0; i < keys.length; i++ ) {
            if( cur == null || cur instanceof String ) {
                return null;
            }

            String key  = keys[i];

            if( cur instanceof String ) {
            } else if( cur instanceof Map) {
                cur = ((Map) cur).get(key);
            } else if( cur instanceof List) {
                Integer num = Integer.parseInt(key);
                cur = ((List) cur).get(num);
            }
        }

        return cur;
    }
}
