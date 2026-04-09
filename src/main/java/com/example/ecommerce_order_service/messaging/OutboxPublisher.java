package com.example.ecommerce_order_service.messaging;

import java.util.List;
import java.util.Map;

import com.example.ecommerce_order_service.entities.OutboxAvroEventEntity;
import com.example.ecommerce_order_service.repositories.OutboxAvroRepository;
import com.example.events.OrderAvroCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.ecommerce_order_service.repositories.OutboxRepository;

@Service
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final OutboxAvroRepository outboxAvroRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, OrderAvroCreatedEvent> kafkaAvroTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutboxRepository outboxRepository,
                           OutboxAvroRepository outboxAvroRepository,
                           KafkaTemplate<String, String> kafkaTemplate,
                           @Qualifier("avroKafkaTemplate") KafkaTemplate<String, OrderAvroCreatedEvent> kafkaAvroTemplate,
                           ObjectMapper objectMapper) {

        this.outboxRepository = outboxRepository;
        this.outboxAvroRepository = outboxAvroRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaAvroTemplate = kafkaAvroTemplate;
        this.objectMapper = objectMapper;
    }

    // =========================
    // 🔥 OUTBOX JSON (NO AVRO)
    // =========================
   /* @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publish() {

        List<OutboxEvent> events = outboxRepository.findByPublishedFalse();

        for (OutboxEvent event : events) {

            System.out.println("📤 Enviando JSON a Kafka: " + event.getAggregateId());

            // 🔥 KEY + payload JSON
            kafkaTemplate.send("order-created", event.getAggregateId(), event.getPayload());

            event.setPublished(true);
        }
    }*/

    /*
    // =========================
    // 🔥 AVRO BINARIO (NO USADO)
    // =========================
    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishAvro() {

        List<OutboxAvroEvent> events = outboxRepository.findByPublishedFalse();

        for (OutboxAvroEvent event : events) {

            kafkaTemplate.send("order-created", event.getAggregateId(), event.getPayload());

            event.setPublished(true);
        }
    }
    */

    // =========================
    // 🔥 OUTBOX AVRO REAL (FIX DEFINITIVO)
    // =========================
    @Scheduled(fixedDelay = 5000)
    public void publishAvro() {

        System.out.println("⏱ Ejecutando OutboxPublisher AVRO...");

        List<OutboxAvroEventEntity> events = outboxAvroRepository.findByPublishedFalse();

        System.out.println("📦 Eventos encontrados: " + events.size());

        for (OutboxAvroEventEntity e : events) {

            try {
                System.out.println("🔄 Procesando evento ID: " + e.getId());

                // =========================================================
                // 🔥 1️⃣ JSON → MAP (NO convertir directamente a AVRO)
                // ❌ NUNCA hacer: objectMapper.readValue(..., OrderAvroCreatedEvent.class)
                // 👉 eso crea un "falso AVRO" y rompe Kafka
                // =========================================================
                Map<String, Object> payload =
                        objectMapper.readValue(e.getPayload(), Map.class);

                // =========================================================
                // 🔥 2️⃣ CONSTRUIR AVRO REAL con builder
                // 👉 SOLO esto garantiza que KafkaAvroSerializer funcione bien
                // =========================================================
                OrderAvroCreatedEvent event = OrderAvroCreatedEvent.newBuilder()
                        .setEventId((String) payload.get("eventId"))
                        .setOrderId((Integer) payload.get("orderId"))
                        .setProductId((Integer) payload.get("productId"))
                        .setQuantity((Integer) payload.get("quantity"))

                        .setClientLat((Double) payload.get("clientLat"))
                        .setClientLon((Double) payload.get("clientLon"))
                        .setOccurredAt((String) payload.get("occurredAt"))
                        .build();

                System.out.println("📤 Enviando a Kafka (AVRO REAL): " + event.getOrderId());

                // =========================================================
                // 🔥 3️⃣ Enviar a Kafka
                // 👉 .get() para forzar error si algo falla (debug PRO)
                // =========================================================
                kafkaAvroTemplate.send("order-created", event).get();

                System.out.println("✅ Evento enviado correctamente a Kafka");

                // =========================================================
                // 🔥 4️⃣ Marcar como publicado
                // =========================================================
                e.setPublished(true);
                outboxAvroRepository.save(e);

            } catch (Exception ex) {

                // =========================================================
                // 🔥 LOG REAL (MUY IMPORTANTE)
                // =========================================================
                System.err.println("❌ Error procesando outbox event ID: " + e.getId());
                ex.printStackTrace();
            }
        }
    }
}