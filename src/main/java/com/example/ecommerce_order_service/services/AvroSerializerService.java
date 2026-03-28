package com.example.ecommerce_order_service.services;

import com.example.ecommerce_common_events.OrderAvroCreatedEvent;
import com.example.ecommerce_common_events.OrderCreatedEvent;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AvroSerializerService {//*********USING AVRO*********

    private final Serializer<Object> serializer;

    public AvroSerializerService() {
        Map<String, Object> config = new HashMap<>();
        config.put("schema.registry.url", "http://localhost:8081");

        this.serializer = new KafkaAvroSerializer();
        this.serializer.configure(config, false);
    }

    public byte[] serialize(String topic, OrderAvroCreatedEvent event) {
        return serializer.serialize(topic, event);
    }
}
