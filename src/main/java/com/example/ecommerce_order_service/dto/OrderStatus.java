package com.example.ecommerce_order_service.dto;

public enum OrderStatus {

    CREATED,              // orden creada
    STOCK_RESERVED,       // stock reservado
    STOCK_FAILED,         // no había stock
    PAYMENT_PENDING,      // esperando pago
    PAID,                 // pago confirmado
    PAYMENT_FAILED,       // pago rechazado
    SHIPPED,              // enviada
    DELIVERED,            // entregada
    CANCELLED             // cancelada
    , PENDING
}
