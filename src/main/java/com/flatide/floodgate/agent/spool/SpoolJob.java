package com.flatide.floodgate.agent.spool;

import com.flatide.floodgate.ConfigurationManager;
import com.flatide.floodgate.agent.Context;
import com.flatide.floodgate.agent.flow.Flow;
import com.flatide.floodgate.agent.flow.stream.FGInputStream;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

class SpoolJob implements Callable<Map> {
    String flowId;
    String target;
    Map<String, Object> flowInfo;
    FGInputStream current;
    Context context;

    public SpoolJob(String flowId, String target, Map<String, Object> flowInfo, FGInputStream current, Context context) {
        this.flowId = flowId;
        this.target = target;
        this.flowInfo = flowInfo;
        this.current = current;
        this.context = context;
    }

    @Override
    public Map call() {
        System.out.println("Spooled Job " + flowId + " start at : " + Thread.currentThread().getId());
        String spoolingPath = (String) ConfigurationManager.shared().getConfig().get("channel.spooling.folder");

        Map<String, Object> result = new HashMap<>();
        try {

            Flow flow = new Flow(flowId, flowInfo, context);
            flow.process(current);
            result.put("result", "success");
            System.out.println("Spooled Job " + flowId + " completed.");

            try {
                File file = new File(spoolingPath + "/" + flowId);
                file.delete();
            } catch( Exception e ) {
                e.printStackTrace();
            }
        } catch(Exception e) {
            e.printStackTrace();
            result.put("result", "fail");
            result.put("reason", e.getMessage());
            System.out.println("Spooled Job " + flowId + " failed with : " + e.getMessage());
        }

        return result;
    }
}
