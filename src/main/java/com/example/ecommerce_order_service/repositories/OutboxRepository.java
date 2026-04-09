package com.example.ecommerce_order_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce_order_service.entities.OutboxEventEntity;

public interface OutboxRepository extends JpaRepository<OutboxEventEntity, Long>{

	List<OutboxEventEntity> findByPublishedFalse();

}
