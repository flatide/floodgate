package com.flatide.floodgate.agent.flow;

public enum FlowTag {
    ENTRY,
    DEBUG,
    SPOOLING,
    MODULE,
    RULE,
    FILTER,

    // For Module
    BEFORE,
    AFTER,
    CALL,

    CONNECT,
    TEMPLATE,
    OUTPUT,
    BATCHSIZE,
    ACTION,

    // For Action

    CREATE,
    READ,
    UPDATE,
    DELETE

}
