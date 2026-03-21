package com.example.ecommerce_order_service.services;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.ecommerce_common_events.OrderCreatedEvent;
import com.example.ecommerce_order_service.dto.CreateOrderRequest;
import com.example.ecommerce_order_service.dto.OrderStatus;
import com.example.ecommerce_order_service.entities.Order;
import com.example.ecommerce_order_service.entities.OutboxEvent;
import com.example.ecommerce_order_service.repositories.OrderRepository;
import com.example.ecommerce_order_service.repositories.OutboxRepository;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository repository,
                        KafkaTemplate<String, Object> kafkaTemplate,
                        OutboxRepository outboxRepository,
                        ObjectMapper objectMapper
                        ) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
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
    }
    
    @Transactional
    public Long createOrderConOutbox(CreateOrderRequest request) {

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
    }
    
}
