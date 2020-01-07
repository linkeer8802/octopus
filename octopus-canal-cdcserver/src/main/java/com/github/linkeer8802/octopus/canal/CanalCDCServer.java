package com.github.linkeer8802.octopus.canal;

/**
 * @author weird
 * @date 2020/1/3
 */

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.github.linkeer8802.octopus.core.message.CDCServer;
import com.github.linkeer8802.octopus.core.message.impl.MessageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class CanalCDCServer implements CDCServer {

    private CanalConnector connector;
    private List<Consumer<com.github.linkeer8802.octopus.core.message.Message>> messageListeners;

    @Value("${octopus.cdc.canal.connector.host:localhost}")
    private String canalConnectorHost;
    @Value("${octopus.cdc.canal.connector.port:1111}")
    private int canalConnectorPort;
    @Value("${octopus.cdc.canal.connector.destination:}")
    private String canalConnectorDestination;
    @Value("${octopus.cdc.canal.connector.username:}")
    private String canalConnectorUsername;
    @Value("${octopus.cdc.canal.connector.password:}")
    private String canalConnectorPassword;
    @Value("${octopus.cdc.canal.connector.subscribe:.*\\..*}")
    private String canalConnectorSubscribeFilter;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public CanalCDCServer() {
        this.messageListeners = new ArrayList<>();
    }

    @PostConstruct
    void init() {
        this.connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalConnectorHost,
                canalConnectorPort), canalConnectorDestination, canalConnectorUsername, canalConnectorPassword);
    }

    @Override
    public void addMessageListener(Consumer<com.github.linkeer8802.octopus.core.message.Message> listener) {
        messageListeners.add(listener);
    }

    @Override
    public void start() {
        int batchSize = 1000;
        try {
            connector.connect();
            connector.subscribe(canalConnectorSubscribeFilter);
            connector.rollback();

            long batchId = -1;
            Message message;
            while (true) {
                try {
                    message = connector.getWithoutAck(batchSize);
                    batchId = message.getId();
                    int size = message.getEntries().size();
                    boolean isEmpty = batchId == -1 || size == 0;
                    Thread.sleep(2000);
                    if (!isEmpty) {
                        consumeMessage(message.getEntries());
                    }
                    connector.ack(batchId);
                } catch (Exception e) {
                    connector.rollback(batchId);
                    log.error("CDC Server error:", e);
                }
            }
        } catch (Exception e){
            connector.disconnect();
        }
    }

    private void consumeMessage(List<CanalEntry.Entry> entries) {
        handleEntry(entries).forEach(message -> messageListeners.forEach(messageListener -> messageListener.accept(message)));
    }

    private List<com.github.linkeer8802.octopus.core.message.Message> handleEntry(List<CanalEntry.Entry> entries) {
        List<com.github.linkeer8802.octopus.core.message.Message> messages = new ArrayList<>();
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChage = null;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
            }

            CanalEntry.EventType eventType = rowChage.getEventType();
            log.info(String.format("binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.INSERT) {
                    List<CanalEntry.Column> columns = rowData.getAfterColumnsList();
                    String data = null;
                    String eventId = null;
                    String aggregateRootType = null;
                    for (CanalEntry.Column column : columns) {
                        if (column.getName().toLowerCase().equals("aggregate_root_type")) {
                            aggregateRootType = column.getValue();
                        } else if (column.getName().toLowerCase().equals("data")) {
                            data = column.getValue();
                        } else if (column.getName().toLowerCase().equals("id")) {
                            eventId = column.getValue();
                        }
                    }
                    if (data != null && aggregateRootType != null && eventId != null) {

                        if (Boolean.TRUE.equals(redisTemplate.hasKey(eventId))) {
                            continue;
                        }

                        Map<String, Object> headers = new HashMap<>(2);
                        headers.put(com.github.linkeer8802.octopus.core.message.Message.CHANNEL, aggregateRootType);
                        headers.put(com.github.linkeer8802.octopus.core.message.Message.PAYLOAD_TYPE,
                                com.github.linkeer8802.octopus.core.message.Message.TYPE_JSON_PAYLOAD);

                        messages.add(new MessageImpl(headers, data));
                    }
                }
            }
        }
        return messages;
    }
}
