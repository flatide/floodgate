package com.flatide.floodgate.agent.connector;

import com.flatide.floodgate.agent.Context;
import com.flatide.floodgate.agent.flow.stream.Payload;
import com.flatide.floodgate.agent.flow.rule.MappingRule;

import java.util.List;
import java.util.Map;

public interface Connector {
    void connect(Context context) throws Exception;

    long create(Payload payload, MappingRule mappingRule) throws Exception;
    List<Map> read(Map rule) throws Exception;
    int update(MappingRule mappingRule, Object data) throws Exception;
    int delete() throws Exception;

    void close() throws Exception;

    void check() throws Exception;

    int getSent();
}
