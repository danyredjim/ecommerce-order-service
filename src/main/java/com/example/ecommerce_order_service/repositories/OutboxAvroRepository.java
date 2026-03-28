package com.example.ecommerce_order_service.repositories;

import com.example.ecommerce_order_service.entities.OutboxAvroEvent;
import com.example.ecommerce_order_service.entities.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxAvroRepository extends JpaRepository<OutboxAvroEvent, Long>{

	List<OutboxEvent> findByPublishedFalse();

}
