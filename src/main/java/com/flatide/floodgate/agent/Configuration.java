package com.flatide.floodgate.agent;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// TODO System Config로 변경

@Component
@ConfigurationProperties("")
public class Configuration {
    private final Map<String, String> datasource = new HashMap<>();
    private final Map<String, String> meta = new HashMap<>();
    private final Map<String, String> channel = new HashMap<>();

    public Map<String, String> getDatasource() {
        return datasource;
    }
    public Map<String, String> getMeta() {
        return meta;
    }
    public Map<String, String> getChannel() {
        return channel;
    }
}
