package com.product.product_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.product.product_service.model.ProductDto;
import com.product.product_service.model.ProductEntity;

public interface ProductService {

	
	List<ProductEntity> getAllProducts();
	Optional<ProductEntity> getProductById(Long id);
	String createProduct(ProductDto productDTO);
	String updateProductByPrice(Long id,ProductDto productDTO);
	ProductDto updateProduct(Long id, ProductDto productDTO);
    ProductDto updateStock(Long id, Integer quantity);
}
