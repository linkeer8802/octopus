package com.github.linkeer8802.octopus.spring.event;

import com.github.linkeer8802.octopus.core.DomainEvent;
import com.github.linkeer8802.octopus.core.EventRepository;
import com.github.linkeer8802.octopus.core.serializer.EventSerializer;
import com.github.linkeer8802.octopus.core.serializer.Serializer;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 基于JDBC的事件仓储实现
 * @author wrd
 */
public class JDBCEventRepository implements EventRepository {

    private JdbcTemplate jdbcTemplate;
    private EventSerializer serializer;


    public JDBCEventRepository(EventSerializer serializer, JdbcTemplate jdbcTemplate) {
        this.serializer = serializer;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(DomainEvent event) {
        insert(event);
    }

    private void insert(DomainEvent event) {
        byte[] bytes = serializer.serialize(event);
        String data = new String(bytes, Serializer.CHARSET_UTF8);
        String sql = "INSERT INTO `global_events`(`id`,`aggregate_root_type`,`timestamp`,`data`,`published`) VALUES(?,?,?,?,?)";
        jdbcTemplate.update(sql, event.getEventId(), event.getAggregateRootType(), event.getTimestamp(), data, EVENT_UNPUBLISHED);
    }
}
