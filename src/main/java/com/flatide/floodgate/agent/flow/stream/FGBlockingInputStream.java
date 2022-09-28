package com.flatide.floodgate.agent.flow.stream;

import com.flatide.floodgate.agent.flow.stream.carrier.Carrier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FGBlockingInputStream extends FGInputStream {
    private final List<BlockingQueue<Object>> currentDataList = new ArrayList<>();
    private long currentSize = 0;

    public FGBlockingInputStream(Carrier carrier) {
        super(carrier);
    }

    @Override
    public void setMaxSubscriber(int maxSubscriber) {
        super.setMaxSubscriber(maxSubscriber);

        for (int i = 0; i < maxSubscriber; i++) {
            BlockingQueue<Object> queue = new ArrayBlockingQueue<>(1);
            this.currentDataList.add(queue);
        }
    }

    @Override
    public long next(Payload payload) {
        try {
            synchronized (this) {
                boolean needForward = true;
                for (int i = 0; i < super.getMaxSubscriber(); i++) {
                    BlockingQueue<Object> queue = this.currentDataList.get(i);
                    if (queue.size() > 0) {
                        needForward = false;
                    }
                }

                if (needForward) {
                    if (!getCarrier().isFinished()) {
                        this.currentSize = getCarrier().forward();
                        if( this.currentSize > 0 ) {
                            Object data = getCarrier().getBuffer();
                            for (int i = 0; i < super.getMaxSubscriber(); i++) {
                                BlockingQueue<Object> queue = this.currentDataList.get(i);
                                queue.put(data);
                            }
                        }
                    }
                }
            }
            if (!getCarrier().isFinished()) {
                BlockingQueue<Object> queue = this.currentDataList.get(payload.getId());
                payload.setData(queue.take());
                payload.setDataSize(this.currentSize);
            }
        } catch (Exception e) {
            this.currentSize = -1;
            e.printStackTrace();
        }

        return this.currentSize;
    }
}
