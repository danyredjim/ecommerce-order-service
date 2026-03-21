package com.example.ecommerce_order_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce_order_service.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
