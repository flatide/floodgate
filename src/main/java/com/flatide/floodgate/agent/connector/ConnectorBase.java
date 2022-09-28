package com.flatide.floodgate.agent.connector;

import com.flatide.floodgate.agent.Context;
import com.flatide.floodgate.agent.flow.rule.FunctionProcessor;
import com.flatide.floodgate.agent.template.DocumentTemplate;
import com.flatide.floodgate.agent.flow.FlowTag;
import com.flatide.floodgate.agent.flow.stream.Payload;
import com.flatide.floodgate.agent.flow.rule.MappingRule;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ConnectorBase implements Connector {
    protected String name;

    Context context;

    String url;
    String user;
    String password;

    private String output;
    private Integer batchSize = 0;

    private DocumentTemplate documentTemplate;

    DocumentTemplate getDocumentTemplate() {
        return this.documentTemplate;
    }

    public void setDocumentTemplate(DocumentTemplate template) {
        this.documentTemplate = template;
    }

    protected String getOutput() {
        return this.output;
    }
    
    protected Context getContext() {
        return this.context;
    }

    @Override
    public void connect(Context context) throws Exception {
        this.context = context;

        this.url = this.context.getString("CONNECT_INFO." + ConnectorTag.URL.name());
        this.user = this.context.getString("CONNECT_INFO." + ConnectorTag.USER.name());
        this.password = this.context.getString("CONNECT_INFO." + ConnectorTag.PASSWORD.name());

        this.output = this.context.getString("SEQUENCE." + FlowTag.OUTPUT.name());
        this.batchSize = this.context.getInteger("SEQUENCE." + FlowTag.BATCHSIZE.name());
    }

    @Override
    //public final long createForStream(Payload payload, MappingRule mappingRule) throws Exception {
    public final long create(Payload payload, MappingRule mappingRule) throws Exception {
        long cur = System.currentTimeMillis();
        beforeCreate(mappingRule);

        long sent = 0;
        List<Map<String, Object>> itemList = new LinkedList<>();

        while( payload.next() != -1 ) {
            Object dataList = payload.getData();
            long length = payload.getReadLength();

            if( dataList instanceof List ) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> temp = (List<Map<String, Object>>) dataList;
                itemList.addAll(temp);

                // TODO 스트리밍이 아닌 경우에 대한 배치 처리를 위한 Method를 만들 것
                if( this.batchSize > 0 && itemList.size() >= this.batchSize ) {   // batch size
                    List<Map<String, Object>> sub = itemList.subList(0, this.batchSize);
                    creating(sub, mappingRule, sent, this.batchSize);
                    sent += this.batchSize;
                    sub.clear();
                }
            } else {
                creatingBinary((byte[]) dataList, length, sent);
                sent += length;
            }
        }

        if( itemList.size() > 0 ) {
            creating(itemList, mappingRule, sent, this.batchSize);
            sent += itemList.size();
            itemList.clear();
        }

        afterCreate(mappingRule);

        System.out.println(String.format("Done : %ss", System.currentTimeMillis() - cur));

        return sent;
    }

    public void beforeCreate(MappingRule mappingRule) throws Exception {}

    public abstract int creating(List<Map<String, Object>> itemList, MappingRule mappingRule, long index, int batchSize) throws Exception;
    public int creatingBinary(byte[] item, long size, long sent) throws Exception { return 0;}

    public void afterCreate(MappingRule mappingRule) throws Exception  {}

    public FunctionProcessor getFunctionProcessor(String type) {
        return null;
    }
}
