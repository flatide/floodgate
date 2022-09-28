package com.flatide.floodgate.agent.flow;

import com.flatide.floodgate.agent.Context;
import com.flatide.floodgate.agent.flow.stream.FGInputStream;
import com.flatide.floodgate.agent.flow.module.Module;
import com.flatide.floodgate.agent.flow.rule.MappingRule;

import java.util.Map;

/*
    하나의 인터페이스를 의미한다
 */

public class Flow {
    private final FlowContext context;

    public Flow(String id, Map<String, Object> flowInfo, Context agentContext) {
        this.context = new FlowContext(id, flowInfo);
        this.context.setEntry((String) flowInfo.get(FlowTag.ENTRY.name()));
        this.context.setDebug((Boolean) flowInfo.get(FlowTag.DEBUG.name()));
        this.context.add("CONTEXT", agentContext);

        // Module
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> mods = (Map<String, Map<String, Object>>) flowInfo.get(FlowTag.MODULE.name());
        for( Map.Entry<String, Map<String, Object>> entry : mods.entrySet() ) {
            Module module = new Module( this, entry.getKey(), entry.getValue());
            this.context.getModules().put( entry.getKey(), module);
        }

        // Connect Info
        /*HashMap<String, Object> connectInfoData = (HashMap) meta.get(FlowTag.CONNECT.name());
        if( connectInfoData != null ) {
            this.connectInfo = new ConnectInfo(connectInfoData);
        }*/

        // Rule
        @SuppressWarnings("unchecked")
        Map<String, Object> mappingData = (Map<String, Object>) flowInfo.get(FlowTag.RULE.name());
        //HashMap<String, Object> mappingData = (HashMap) meta.get(FlowTag.RULE.name());
        for( Map.Entry<String, Object> entry : mappingData.entrySet() ) {
            MappingRule rule = new MappingRule();
            @SuppressWarnings("unchecked")
            Map<String, String> temp = (Map<String, String>) entry.getValue();
            rule.addRule( temp );
            this.context.getRules().put( entry.getKey(), rule );
        }
    }

    public FGInputStream process(FGInputStream input) {
        this.context.setCurrent(input);

        this.context.setNext(this.context.getEntry());
        while( this.context.hasNext()  ) {
            Module module = this.context.next();

            try {

                module.processBefore(context);
                module.process(context);
                module.processAfter(context);

                //TODO

            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return this.context.getCurrent();
    }
}
