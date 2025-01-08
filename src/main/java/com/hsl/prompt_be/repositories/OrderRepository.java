package com.hsl.prompt_be.repositories;

import com.hsl.prompt_be.entities.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    boolean existsByPrinterIdAndCustomerId(UUID printerId, UUID customerId);
    int countByCreatedAt(Instant createdAt);
    Page<Order> findAllByPrinterId(UUID printerId, Pageable pageable);
}
