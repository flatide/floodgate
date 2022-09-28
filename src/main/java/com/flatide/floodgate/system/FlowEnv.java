package com.flatide.floodgate.system;

import java.util.concurrent.atomic.AtomicLong;

public class FlowEnv {
    AtomicLong seq = new AtomicLong(0);

    private static final FlowEnv instance = new FlowEnv();

    public static FlowEnv shared() {
        return instance;
    }

    public long getSequence() {
       return this.seq.incrementAndGet();
    }
}
