package com.flatide.floodgate.agent.flow.rule;

import com.flatide.floodgate.agent.flow.rule.MappingRuleItem;

public interface FunctionProcessor {
    Object process(MappingRuleItem item);
}