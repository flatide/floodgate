package com.flatide.floodgate.agent.spool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flatide.floodgate.ConfigurationManager;
import com.flatide.floodgate.agent.Context;
import com.flatide.floodgate.agent.flow.stream.FGInputStream;
import com.flatide.floodgate.agent.flow.stream.FGSharableInputCurrent;
import com.flatide.floodgate.agent.flow.stream.carrier.container.JSONContainer;
import com.flatide.floodgate.agent.meta.MetaManager;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

public class SpoolingManager {
    private static final SpoolingManager instance = new SpoolingManager();

    LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    ThreadPoolExecutor[] executor = new ThreadPoolExecutor[4];

    private SpoolingManager() {
        for(int i = 0; i < 4; i++) {
            this.executor[i] = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        }

        Thread thread = new Thread( () -> {
            System.out.println("Spooling thread started...");
            String spoolingPath = (String) ConfigurationManager.shared().getConfig().get("channel.spooling.folder");
            String flowInfoTable = (String) ConfigurationManager.shared().getConfig().get("meta.source.tableForFlow");
            String payloadPath = (String) ConfigurationManager.shared().getConfig().get("channel.payload.folder");
            while(true) {
                try {
                    long cur = System.currentTimeMillis();

                    String flowId = this.queue.take();

                    ObjectMapper mapper = new ObjectMapper();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> spoolingInfo = mapper.readValue(new File(spoolingPath + "/" + flowId), LinkedHashMap.class);
                    String target = (String) spoolingInfo.get("target");

                    Map<String, Object> flowInfoResult = MetaManager.shared().read(flowInfoTable, target);
                    Map<String, Object> flowInfo = (Map<String, Object>) flowInfoResult.get("FLOW");

                    Map<String, Object> spooledContext = (Map<String, Object>) spoolingInfo.get("context");

                    String channel_id = (String) spooledContext.get(Context.CONTEXT_KEY.CHANNEL_ID.name());

                    @SuppressWarnings("unchecked")
                    Map<String, Object> payload = mapper.readValue(new File(payloadPath + "/" + channel_id), LinkedHashMap.class);
                    FGInputStream current = new FGSharableInputCurrent(new JSONContainer(payload, "HEADER", "ITEMS"));

                    spooledContext.put(Context.CONTEXT_KEY.REQUEST_BODY.name(), payload);
                    Context context = new Context();
                    context.setMap(spooledContext);


                    // 동일 target은 동일 쓰레드에서만 순차적으로 처리될 수 있도록 한다
                    int index = target.charAt(target.length() - 1) % 4;

                    SpoolJob job = new SpoolJob(flowId, target, flowInfo, current, context);
                    executor[index].submit(job);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public static SpoolingManager shared() {
        return instance;
    }

    public void addJob(String id) {
        this.queue.offer(id);
    }
}
