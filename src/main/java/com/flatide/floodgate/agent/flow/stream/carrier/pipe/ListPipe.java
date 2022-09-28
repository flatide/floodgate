package com.flatide.floodgate.agent.flow.stream.carrier.pipe;

import com.flatide.floodgate.agent.flow.stream.carrier.Carrier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListPipe<T> implements Carrier {
    private Map header;
    private List<T> data;

    private List<T> buffer = null;
    private final int bufferSize;

    private int current = 0;

    private boolean isFinished = false;

    public ListPipe(Map header, List<T> data) {
        this(header, data, data.size());
    }

    public ListPipe(Map header, List<T> data, int bufferSize) {
        this.header = header;
        this.data = data;
        this.bufferSize = bufferSize;
    }

    @Override
    public void flushToFile(String filename) throws Exception {
    }

    public void reset() {
        this.current = 0;
        this.isFinished = false;
    }

    public void close() {
        this.header = null;
        this.data = null;
    }

    public long totalSize() {
        return this.data.size();
    }

    public long remainSize() {
        return this.data.size() - this.current;
    }

    public Object getHeaderData() {
        return header;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public Object getBuffer() {
        return this.buffer;
    }

    public long getBufferReadSize() {
        return this.buffer.size();
    }

    public long forward() {
        this.buffer = new ArrayList<>();

        for(int i = 0; i < this.bufferSize; i++) {
            if( this.current >= this.data.size() ) {
                break;
            }
            T data = this.data.get(this.current++);
            this.buffer.add(data);
        }

        if( this.buffer.size() == 0 ) {
            this.isFinished = true;
        }

        return this.buffer.size();
    }
}
