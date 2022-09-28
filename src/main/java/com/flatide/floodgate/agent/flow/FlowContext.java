package com.flatide.floodgate.agent.flow;

import com.flatide.floodgate.agent.Context;
import com.flatide.floodgate.agent.flow.stream.FGInputStream;
import com.flatide.floodgate.agent.flow.stream.Payload;
import com.flatide.floodgate.agent.flow.module.Module;
import com.flatide.floodgate.agent.flow.rule.MappingRule;

import java.util.HashMap;
import java.util.Map;

public class FlowContext extends Context {
    Boolean debug = false;

    String id;
    //Map<String, Object> flowData;

    String entry = "";

    Map<String, Module> modulesMap = new HashMap<>();
    Map<String, MappingRule> rulesMap = new HashMap<>();

    Module previousModule = null;
    Module currentModule = null;
    Module nextModule = null;

    // Input Data
    FGInputStream current;

    Payload payload;

    public FlowContext(String id, Map<String, Object> flowData) {
        this.id = id;
        super.add("FLOW", flowData);
        //this.flowData = flowData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public Map<String, Module> getModules() {
        return modulesMap;
    }

    public void setModule(HashMap<String, Module> modulesMap) {
        this.modulesMap = modulesMap;
    }

    public Map<String, MappingRule> getRules() {
        return rulesMap;
    }

    public void setRules(HashMap<String, MappingRule> rulesMap) {
        this.rulesMap = rulesMap;
    }

    public FGInputStream getCurrent() {
        return current;
    }

    public void setCurrent(FGInputStream data) {
        this.current = data;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public boolean hasNext() {
        return this.nextModule != null;
    }

    public Module setNext(String id) {
        Module module = this.modulesMap.get(id);
        this.nextModule = module;

        return module;
    }

    public Module next() {
        this.previousModule = this.currentModule;
        this.currentModule = this.nextModule;
        this.nextModule = null;

        return this.currentModule;
    }
}
