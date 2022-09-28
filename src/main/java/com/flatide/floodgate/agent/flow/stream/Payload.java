package com.flatide.floodgate.agent.flow.stream;

public class Payload {
    private final FGInputStream current;
    private final int id;

    private Object data = null;
    private long dataSize = 0;

    Payload(FGInputStream current, int id) {
        this.current = current;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }

    void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    // Total size, -1 if not possible
    public long    size() {
        return this.current.size();
    }

    private long    remains() {
        return this.current.remains(this);
    }

    public Object  getHeader() {
        return this.current.getHeader();
    }

    // whether whole data sent
    private boolean isFinished() {
        return remains() <= 0;
    }

    // blocking
    public long next() throws Exception {
        if(isFinished()) {
            this.dataSize = -1;
            return -1;
        }
        long length = this.current.next(this);
        if( length <= 0 ) {
            this.dataSize = -1;
            return -1;
        }

        return length;
    }

    public long getReadLength() {
        return dataSize;
    }
}
