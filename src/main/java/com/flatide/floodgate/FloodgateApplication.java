package com.flatide.floodgate;

import com.flatide.floodgate.agent.Config;
import com.flatide.floodgate.agent.logging.LoggingManager;
import com.flatide.floodgate.agent.meta.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class FloodgateApplication {
    @Autowired
    private Config config;

    static byte[] holder = null;

    @PostConstruct
    public void init() {
        try {
            ConfigurationManager.shared().setConfig(config);

            //MetaManager.shared().setConfig(configuration);

            //String metaSourceType = configuration.getChannel().get("meta.datasource");
            String metaDatasource = (String) config.get("channel.meta.datasource");
            MetaManager.shared().changeSource(metaDatasource, false);

            //String logDatasource = configuration.getChannel().get("log.datasource");
            String logDatasource = (String) config.get("channel.log.datasource");
            LoggingManager.shared().changeSource(logDatasource, false);

            //MetaManager.shared().load(configuration.getMeta().get("source.tableForAPI"));
            //MetaManager.shared().load(configuration.getMeta().get("source.tableForFlow"));
            //MetaManager.shared().load(configuration.getMeta().get("source.tableForConnection"));
            MetaManager.shared().load((String) config.get("meta.source.tableForAPI"));
            MetaManager.shared().load((String) config.get("meta.source.tableForFlow"));
            MetaManager.shared().load((String) config.get("meta.source.tableForConnection"));

            //holder = new byte[104800000];
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            SpringApplication.run(FloodgateApplication.class, args);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
