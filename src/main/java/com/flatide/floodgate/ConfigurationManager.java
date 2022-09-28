package com.flatide.floodgate;

import com.flatide.floodgate.agent.Config;
import com.flatide.floodgate.agent.Configuration;

public class ConfigurationManager {
    private static final ConfigurationManager instance = new ConfigurationManager();

    Config config;

    private ConfigurationManager() {
    }
    
    public static ConfigurationManager shared() {
        return instance;
    }
    
    public void setConfig(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return this.config;
    }
}
