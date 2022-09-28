package com.flatide.floodgate.agent.flow.stream.carrier.container;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flatide.floodgate.agent.flow.stream.carrier.Carrier;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JSONContainer implements Carrier {
    private final String headerTag;
    private final String dataTag;

    private final Map<String, Object> header = null;
    private Map<String, Object> data = null;
    private List<Object> buffer = null;

    private final int bufferSize;
    private final int bufferReadSize = -1;

    private boolean isFinished = false;

    public JSONContainer(Map<String, Object> data, String headerTag, String dataTag) throws Exception {
        this.headerTag = headerTag;
        this.dataTag = dataTag;
        this.buffer = (List<Object>) data.get(dataTag);
        this.bufferSize = this.buffer.size();

        this.data = data;
    }

    @Override
    public void flushToFile(String filename) throws Exception {
        try {
            String path = filename.substring(0, filename.lastIndexOf("/"));
            File folder = new File(path);

            if(!folder.exists()) {
                if( !folder.mkdir() ) {
                    throw new IOException("Cannot make folder " + path);
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), data);
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void reset() {
        this.isFinished = false;
    }

    public void close() {
    }

    public long totalSize() {
        return this.buffer.size();
    }

    public long remainSize() {
        if( this.isFinished ) {
            return 0;    // file offset 고려해 볼 것
        }
        return this.buffer.size();
    }

    public Object getHeaderData() {
        return this.data.get(this.headerTag);
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

    public long forward() throws Exception {
        if( this.isFinished ) {
            return -1;
        }
        this.isFinished = true;
        return this.buffer.size();
    }
}
