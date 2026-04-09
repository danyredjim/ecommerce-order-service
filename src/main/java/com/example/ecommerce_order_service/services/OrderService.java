package com.example.ecommerce_order_service.services;

import com.example.ecommerce_order_service.entities.OutboxAvroEventEntity;
import com.example.ecommerce_order_service.repositories.OutboxAvroRepository;
import com.example.events.OrderAvroCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.ecommerce_order_service.dto.CreateOrderRequest;
import com.example.ecommerce_order_service.dto.OrderStatus;
import com.example.ecommerce_order_service.entities.Order;
import com.example.ecommerce_order_service.repositories.OrderRepository;
import com.example.ecommerce_order_service.repositories.OutboxRepository;

import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OutboxRepository outboxRepository;
    private final OutboxAvroRepository outboxAvroRepository;//*********USING AVRO*********
    private final KafkaTemplate<String, OrderAvroCreatedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    //private final AvroSerializerService avroSerializer;//*********USING AVRO*********

    public OrderService(OrderRepository repository,
                        KafkaTemplate<String, OrderAvroCreatedEvent> kafkaTemplate,
                        OutboxRepository outboxRepository, OutboxAvroRepository outboxAvroRepository,
                        ObjectMapper objectMapper/*, AvroSerializerService avroSerializer*/
    ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.outboxRepository = outboxRepository;
        this.outboxAvroRepository = outboxAvroRepository;
        this.objectMapper = objectMapper;
        /*this.avroSerializer = avroSerializer;*///*********USING AVRO*********
    }

    /*@Transactional
    public Long createOrder(CreateOrderRequest request) {//sin outbox Event

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setShippingAddress(request.getShippingAddress());
        order.setQuantity(request.getQuantity());
        order.setStatus(OrderStatus.PENDING);

        order =repository.save(order);
        // 🔥 Evento completamente inicializado
        OrderCreatedEvent event = new OrderCreatedEvent(order.getId() ,request.getProductId() ,request.getQuantity() ,41.3841,2.1734);

        kafkaTemplate.send("order-created",event);

        return order.getId();
    }*/
    
    /*@Transactional
    public Long createOrderWhitOutbox(CreateOrderRequest request) {

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setShippingAddress(request.getShippingAddress());
        order.setQuantity(request.getQuantity());
        order.setStatus(OrderStatus.PENDING);

        order = repository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                request.getProductId(),
                request.getQuantity(),
                41.3841,
                2.1734
        );

        String payload = objectMapper.writeValueAsString(event);

        OutboxEvent outbox = new OutboxEvent(
                event.getEventId(),
                "ORDER",
                order.getId().toString(),
                "OrderCreatedEvent",
                payload
        );

        outboxRepository.save(outbox);

        return order.getId();
    }*/

    /*@Transactional
    public Long createOrderWhitAvroAndOutbox(CreateOrderRequest request) {//*********USING AVRO********* Avro binario manual

        // 1. Guardar Order
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setShippingAddress(request.getShippingAddress());
        order.setQuantity(request.getQuantity());
        order.setStatus(OrderStatus.PENDING);

        order = repository.save(order);

        // 2. Crear evento AVRO
        OrderAvroCreatedEvent event = OrderAvroCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOrderId(order.getId())
                .setProductId(request.getProductId())
                .setQuantity(request.getQuantity())
                .setClientLat(41.3841)
                .setClientLon(2.1734)
                .setOccurredAt(Instant.now().toString())
                .build();

        // 3. Serializar (manejando excepción)
        byte[] payload;
        try {
            payload = event.toByteBuffer().array();
        } catch (IOException e) {
            throw new RuntimeException("Error serializando evento Avro", e);
        }

        // 4. Outbox
        OutboxAvroEvent outbox = new OutboxAvroEvent(
                event.getEventId().toString(),
                "ORDER",
                order.getId().toString(),
                "OrderAvroCreatedEvent",
                payload
        );

        outboxAvroRepository.save(outbox);

        return order.getId();
    }*/

    @Transactional
    public Long createOrderWhitAvroAndOutboxAndRegistry(CreateOrderRequest request) {

        // 1. Guardar Order
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setShippingAddress(request.getShippingAddress());
        order.setQuantity(request.getQuantity());
        order.setStatus(OrderStatus.PENDING);

        order = repository.save(order);

        // 2. Crear evento AVRO (SOLO objeto, NO serializar)
        OrderAvroCreatedEvent event = OrderAvroCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOrderId(order.getId())
                .setProductId(request.getProductId())
                .setQuantity(request.getQuantity())
                .setClientLat(41.3841)
                .setClientLon(2.1734)
                .setOccurredAt(Instant.now().toString())
                .build();

        // 3. Guardar en Outbox como JSON (NO binario)
        String payload;
        try {
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("eventId", event.getEventId());
            payloadMap.put("orderId", event.getOrderId());
            payloadMap.put("productId", event.getProductId());
            payloadMap.put("quantity", event.getQuantity());
            payloadMap.put("clientLat", event.getClientLat());
            payloadMap.put("clientLon", event.getClientLon());
            payloadMap.put("occurredAt", event.getOccurredAt());

            payload = objectMapper.writeValueAsString(payloadMap);

        } catch (Exception e) {
            throw new RuntimeException("Error serializando evento a JSON", e);
        }

        OutboxAvroEventEntity outbox = new OutboxAvroEventEntity(
                event.getEventId().toString(),
                "ORDER",
                order.getId().toString(),
                "OrderAvroCreatedEvent",
                payload // 👈 JSON, no bytes
        );

        outboxAvroRepository.save(outbox);

        return order.getId();
    }
    
}
