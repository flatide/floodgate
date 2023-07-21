/*
 * MIT License
 *
 * Copyright (c) 2022 FLATIDE LC.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.flatide.floodgate.agent.handler;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.flatide.floodgate.ConfigurationManager;
import com.flatide.floodgate.FloodgateEnv;
import com.flatide.floodgate.agent.Context;
import com.flatide.floodgate.agent.Context.CONTEXT_KEY;
import com.flatide.floodgate.agent.flow.Flow;
import com.flatide.floodgate.agent.flow.module.Module;
import com.flatide.floodgate.agent.handler.FloodgateAbstractHandler;
import com.flatide.floodgate.agent.logging.LoggingManager;

public class DBLogHandler implements FloodgateAbstractHandler {
    @Override
    public void handleChannelIn(Context context, Object object) {
        Map<String, Object> log = new HashMap<>();

        long cur = System.currentTimeMillis();
        java.sql.Timestamp startTime = new java.sql.Timestamp(cur);

        context.add(CONTEXT_KEY.CHANNEL_START_TIME, cur);

        String id = context.getString(CONTEXT_KEY.CHANNEL_ID);
        String api = context.getString(CONTEXT_KEY.API);

        log.put("LOG_KEY", id);
        log.put("LOG_STEP", "API_IN");
        log.put("LOG_TIMESTAMP", startTime);
        log.put("REMARK", api);

        loggingInsert("LOG_KEY", log);
    }

    @Override
    public void handleChannelOut(Context context, Object object) {
        Map<String, Object> log = new HashMap<>();

        long cur = System.currentTimeMillis();
        java.sql.Timestamp endTime = new java.sql.Timestamp(cur);

        long start = (long) context.getDefault(CONTEXT_KEY.CHANNEL_START_TIME, Long.valueOf(0));

        context.add(CONTEXT_KEY.CHANNEL_ELAPSED, cur - start);

        String id = context.getString(CONTEXT_KEY.CHANNEL_ID);
        String api = context.getString(CONTEXT_KEY.API);

        log.put("LOG_KEY", id);
        log.put("LOG_STEP", "API_OUT");
        log.put("LOG_TIMESTAMP", endTime);
        log.put("REMARK", api);

        String result = context.getString(CONTEXT_KEY.LATEST_RESULT);
        String msg = context.getString(CONTEXT_KEY.LATEST_MSG);
        log.put("STATUS", result);
        log.put("ERR_MSG", msg);

        loggingInsert("LOG_KEY", log);
    }

    @Override
    public void handleFlowIn(Context context, Object object) {
        Map<String, Object> log = new HashMap<>();

        long cur = System.currentTimeMillis();
        java.sql.Timestamp startTime = new java.sql.Timestamp(cur);
        context.add(CONTEXT_KEY.FLOW_START_TIME, cur);

        Flow flow = (Flow) object;
        String id = flow.getFlowId(); 
        String parentId = context.getString(CONTEXT_KEY.CHANNEL_ID);
        String targetId = flow.getTargetId();

        log.put("LOG_KEY", parentId);
        log.put("FLOW_KEY", id);
        log.put("LOG_STEP", "IF_IN");
        log.put("LOG_TIMESTAMP", startTime);
        log.put("INTERFACE_ID", targetId);

        loggingInsert("LOG_KEY", log);
    }

    @Override
    public void handleFlowOut(Context context, Object object) {
        Map<String, Object> log = new HashMap<>();

        long cur = System.currentTimeMillis();
        java.sql.Timestamp endTime = new java.sql.Timestamp(cur);

        Flow flow = (Flow) object;
        String id = flow.getFlowId(); 
        String parentId = context.getString(CONTEXT_KEY.CHANNEL_ID);
        String targetId = flow.getTargetId();

        log.put("LOG_KEY", parentId);
        log.put("FLOW_KEY", id);
        log.put("LOG_STEP", "IF_OUT");
        log.put("LOG_TIMESTAMP", endTime);
        log.put("INTERFACE_ID", targetId);

        String result = flow.getResult();
        String msg = flow.getMsg();
        log.put("STATUS", result);
        log.put("ERR_MSG", msg);

        loggingInsert("LOG_KEY", log);
    }

    @Override
    public void handleModuleIn(Context context, Object object) {
        Map<String, Object> log = new HashMap<>();

        long cur = System.currentTimeMillis();
        java.sql.Timestamp startTime = new java.sql.Timestamp(cur);
        context.add(CONTEXT_KEY.MODULE_START_TIME, cur);

        Module module = (Module) object;
        String channelId = context.getString(CONTEXT_KEY.CHANNEL_ID);
        String flowId = module.getFlow().getFlowId();
        String id = module.getId(); 
        String targetId = module.getFlow().getTargetId();

        log.put("LOG_KEY", channelId);
        log.put("FLOW_KEY", flowId);
        log.put("MODULE_KEY", id);
        log.put("LOG_STEP", module.getName());
        log.put("LOG_TIMESTAMP", startTime);
        log.put("INTERFACE_ID", targetId);

        loggingInsert("LOG_KEY", log);
    }

    @Override
    public void handleModuleOut(Context context, Object object) {
        Map<String, Object> log = new HashMap<>();

        long cur = System.currentTimeMillis();
        java.sql.Timestamp endTime = new java.sql.Timestamp(cur);

        Module module = (Module) object;
        String channelId = context.getString(CONTEXT_KEY.CHANNEL_ID);
        String flowId = module.getFlow().getFlowId();
        String id = module.getId();

        log.put("LOG_KEY", channelId);
        log.put("FLOW_KEY", flowId);
        log.put("MODULE_KEY", id);
        log.put("LOG_END_TIMESTAMP", endTime);

        String result = module.getResult();
        String msg = module.getMsg();
        log.put("STATUS", result);
        log.put("ERR_MSG", msg);

        Integer count = module.getProgress();
        log.put("DATA_CNT", count);

        context.add("count", count);

        loggingUpdate("MODULE_KEY", log);
    }

    @Override
    public void handleModuleProgress(Context context, Object object) {
        Map<String, Object> log = new HashMap<>();

        Module module = (Module) object;
        String channelId = context.getString(CONTEXT_KEY.CHANNEL_ID);
        String flowId = module.getFlow().getFlowId();
        String id = module.getId();

        log.put("LOG_KEY", channelId);
        log.put("FLOW_KEY", flowId);
        log.put("MODULE_KEY", id);

        Integer count = module.getProgress();
        log.put("DATA_CNT", count);

        loggingUpdate("MODULE_KEY", log);
    }

    private void loggingInsert(String keyColumn, Map log) {
        java.sql.Timestamp time = (java.sql.Timestamp) log.get("LOG_TIMESTAMP");
        LocalDateTime local = time.toLocalDateTime();
        int nano = local.getNano();
        log.put("LOG_MILLI_SECONDS", nano / 1000000);

        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        log.put("LOG_WEEK", String.format("%02d", cal.get(3)));

        log.put("LOG_MODULE", "Floodgate");
        log.put("LOG_HOST", FloodgateEnv.getInstance().getAddress());
        String table = ConfigurationManager.shared().getString("channel.log.tableForLog");
        LoggingManager.shared().insert(table, "LOG_KEY", log);
    }

    private void loggingUpdate(String keyColumn, Map log) {
        log.put("LOG_MODULE", "Floodgate");
        log.put("LOG_HOST", FloodgateEnv.getInstance().getAddress());
        String table = ConfigurationManager.shared().getString("channel.log.tableForLog");
        LoggingManager.shared().update(table, "MODULE_KEY", log);
    }
}
