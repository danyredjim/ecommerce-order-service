package com.example.ecommerce_order_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "outbox_avro_event")
public class OutboxAvroEvent {

    @Id
    private String id;

    private String aggregateType;
    private String aggregateId;
    private String eventType;

    @Lob
    private byte[] payload;

    private Instant createdAt = Instant.now();
    private boolean published = false;

    public OutboxAvroEvent() {}

    public OutboxAvroEvent(String id, String aggregateType, String aggregateId,
                       String eventType, byte[] payload) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
    }
}
