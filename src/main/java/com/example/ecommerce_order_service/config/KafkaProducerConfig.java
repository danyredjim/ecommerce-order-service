package com.example.ecommerce_order_service.config;

import com.example.events.OrderAvroCreatedEvent;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {


    // ✅ TEMPLATE PARA JSON (STRING)
    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }

    // ✅ TEMPLATE PARA AVRO
    @Bean
    public ProducerFactory<String, OrderAvroCreatedEvent> avroProducerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put("schema.registry.url", "http://localhost:8083"); // OK

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean(name = "avroKafkaTemplate")
    public KafkaTemplate<String, OrderAvroCreatedEvent> kafkaAvroTemplate() {
        return new KafkaTemplate<>(avroProducerFactory());
    }
}
