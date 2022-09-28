package com.flatide.floodgate.agent.flow.stream;

import com.flatide.floodgate.agent.flow.stream.carrier.Carrier;

import java.util.ArrayList;

public abstract class FGInputStream {
    protected final Carrier carrier;
    private int maxSubscriber = 1;
    private int currentSubscriber = 0;
    private int countOfCurrentDone = Integer.MAX_VALUE;

    private final ArrayList<Payload> payloads =  new ArrayList<>();

    private Object currentData = null;
    private long currentSize = 0;

    public FGInputStream(Carrier carrier) {
        this.carrier = carrier;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    int getMaxSubscriber() {
        return maxSubscriber;
    }

    int getCurrentSubscriber() {
        return currentSubscriber;
    }

    public void setCurrentSubscriber(int currentSubscriber) {
        this.currentSubscriber = currentSubscriber;
    }

    public int getCountOfCurrentDone() {
        return countOfCurrentDone;
    }

    public void setCountOfCurrentDone(int countOfCurrentDone) {
        this.countOfCurrentDone = countOfCurrentDone;
    }

    public void increaseCountOfCurrentDone() {
        this.countOfCurrentDone++;
    }

    public void reset() {
        this.carrier.reset();
    }

    public void close() {
    }

    public void setMaxSubscriber(int maxSubscriber) {
        this.maxSubscriber = maxSubscriber;
    }

    public synchronized Payload subscribe() {
        Payload payload = new Payload(this, this.currentSubscriber++);
        this.payloads.add(payload);
        return payload;
    }

    public synchronized void unsubscribe(Payload payload)
    {
        int id = payload.getId();
        this.payloads.remove(id);
        this.currentSubscriber--;
    }

    public long size() {
        return this.carrier.totalSize();
    }

    public long remains(Payload payload) {
        return this.carrier.remainSize();
    }

    public Object  getHeader() {
        return this.carrier.getHeaderData();
    }

    public long next(Payload payload) throws Exception {
        if( this.carrier.isFinished() ) {
            return -1;
        }

        if( this.countOfCurrentDone >= this.currentSubscriber ) {
            this.currentSize = this.carrier.forward();

            this.currentData = this.carrier.getBuffer();
            this.countOfCurrentDone = 0;
        }

        payload.setData(this.currentData);
        payload.setDataSize(this.currentSize);

        this.countOfCurrentDone++;

        return this.currentSize;
    }
}
