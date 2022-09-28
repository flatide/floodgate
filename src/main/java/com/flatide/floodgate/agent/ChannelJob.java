package com.flatide.floodgate.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flatide.floodgate.ConfigurationManager;
import com.flatide.floodgate.agent.flow.Flow;
import com.flatide.floodgate.agent.flow.FlowTag;
import com.flatide.floodgate.agent.flow.stream.FGInputStream;
import com.flatide.floodgate.agent.meta.MetaManager;
import com.flatide.floodgate.agent.spool.SpoolingManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public class ChannelJob implements Callable<Map> {
    //String id;

    String target;
    Context context;
    FGInputStream current;

    public ChannelJob(String target, Context context, FGInputStream current) {
        this.target = target;
        this.context = context;
        this.current = current;
    }

    /*public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }*/

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public FGInputStream getCurrent() {
        return current;
    }

    public void setCurrent(FGInputStream current) {
        this.current = current;
    }

    @Override
    public Map call() {
        String flowInfoTable = (String) ConfigurationManager.shared().getConfig().get("meta.source.tableForFlow");
        Map<String, Object> flowInfo = MetaManager.shared().read( flowInfoTable, target);
        //Map<String, Object> flowInfo = (Map<String, Object>) flowInfoResult.get("FLOW");
        Map<String, Object> result = new HashMap<>();
        try {
            // Unique ID 생성
            UUID id = UUID.randomUUID();
            String flowId = id.toString();


            Object spooling = flowInfo.get(FlowTag.SPOOLING.name());
            if( spooling != null && (boolean) spooling) {
                // backup ChannelJob with flowId
                String path = (String) ConfigurationManager.shared().getConfig().get("channel.spooling.folder");
                Map<String, Object> contextMap = this.context.getMap();

                Map<String, Object> newContext = new HashMap<>();
                for( Map.Entry<String, Object> entry : contextMap.entrySet() ) {
                    if(!Context.CONTEXT_KEY.REQUEST_BODY.name().equals(entry.getKey())) {
                        newContext.put(entry.getKey(), entry.getValue());
                    }
                }

                Map<String, Object> spoolInfo = new HashMap<>();
                spoolInfo.put("target", this.target);
                spoolInfo.put("context", newContext);

                ObjectMapper mapper = new ObjectMapper();
                mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path + "/" + flowId), spoolInfo);

                SpoolingManager.shared().addJob(flowId);
                result.put("result", "spooled : " + flowId);
            } else {
                Flow flow = new Flow(flowId, flowInfo, this.context);
                flow.process(current);
                result.put("result", "success");
            }
        } catch(Exception e) {
            e.printStackTrace();
            result.put("result", "fail");
            result.put("reason", e.getMessage());
        }
        return result;
    }
}
