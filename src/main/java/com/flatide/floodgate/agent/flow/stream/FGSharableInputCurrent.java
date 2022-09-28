package com.flatide.floodgate.agent.flow.stream;

import com.flatide.floodgate.agent.flow.stream.carrier.Carrier;

public class FGSharableInputCurrent extends FGInputStream {
    private int maxSubscriber = 1;
    private int currentSubscriber = 0;
    private int countOfCurrentDone = Integer.MAX_VALUE;

    private Object currentData = null;
    private long currentSize = 0;

    public FGSharableInputCurrent(Carrier carrier) {
        super(carrier);
        try {
            this.currentSize = this.carrier.forward();

            this.currentData = this.carrier.getBuffer();
            this.countOfCurrentDone = 0;
        } catch(Exception e) {
            e.printStackTrace();
        }
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
    }

    public void close() {
    }

    public void setMaxSubscriber(int maxSubscriber) {
        this.maxSubscriber = maxSubscriber;
    }

    public Payload subscribe() {
        Payload payload = new Payload(this, 0);
        return payload;
    }

    public void unsubscribe(Payload payload)
    {
    }

    public long size() {
        return this.carrier.totalSize();
    }

    @Override
    public long remains(Payload payload) {
        if( payload.getData() != null ) {
            return 0;
        } else {
            return currentSize;
        }
    }

    public Object  getHeader() {
        return this.carrier.getHeaderData();
    }

    public long next(Payload payload) throws Exception {
        if( payload.getData() != null ) {
            return -1;
        }

        payload.setData(this.currentData);
        payload.setDataSize(this.currentSize);

        return this.currentSize;
    }
}
