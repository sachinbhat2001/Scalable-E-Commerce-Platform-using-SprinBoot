package com.product.product_service.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.product.product_service.model.ProductEntity;

import jakarta.transaction.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
	
	
	@Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.price = :price WHERE p.id = :id")
    void updatePriceById(Long id, BigDecimal price);

}
