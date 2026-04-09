package com.example.ecommerce_order_service.repositories;

import com.example.ecommerce_order_service.entities.OutboxAvroEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxAvroRepository extends JpaRepository<OutboxAvroEventEntity, Long>{

	List<OutboxAvroEventEntity> findByPublishedFalse();

}
