package com.order.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order.order_service.model.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity,Long>{

}
