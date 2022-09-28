package com.flatide.floodgate.agent.flow.stream.carrier;

public interface Carrier {
    long forward() throws Exception;

    long totalSize();

    long remainSize();

    Object getHeaderData();

    boolean isFinished();

    Object getBuffer();

    long getBufferReadSize();

    void flushToFile(String filename) throws Exception;

    void reset();

    void close();
}
