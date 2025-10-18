package com.order.order_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.order.order_service.model.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long>{

    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.userId = :userId")
    List<OrderEntity> findOrdersWithItemsByUserId(@Param("userId") Long userId);
}
