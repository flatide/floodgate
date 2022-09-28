package com.flatide.floodgate.agent;

import com.flatide.floodgate.ConfigurationManager;
import com.flatide.floodgate.agent.flow.Flow;
import com.flatide.floodgate.agent.flow.stream.FGInputStream;
import com.flatide.floodgate.agent.meta.MetaManager;

import java.util.HashMap;
import java.util.Map;

public class PushAgent extends Spoolable {
    Context context;

    public PushAgent() {
        this.context = new Context();
    }

    public void addContext(String key, Object value) {
        this.context.add(key, value);
    }

    public Map<String, Object> process(FGInputStream data, String ifId) {

        // 페이로드 저장


        // 잡이 즉시처리인지 풀링인지 확인
        // 풀링일 경우
        // 즉시처리인 경우

        Map<String, Object> result = new HashMap<>();

        try {
            String tableName = (String) ConfigurationManager.shared().getConfig().get("meta.source.tableForFlow");
            Map<String, Object> flowInfoResult = MetaManager.shared().read( tableName, ifId);
            Map<String, Object> flowInfo = (Map<String, Object>) flowInfoResult.get("FLOW");
            Flow flow = new Flow(ifId, flowInfo, this.context);
            flow.process(data);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
