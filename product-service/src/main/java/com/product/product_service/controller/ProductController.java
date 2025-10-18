package com.product.product_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.product.product_service.model.ProductDto;
import com.product.product_service.model.ProductEntity;
import com.product.product_service.service.ProductService;


@RestController
@RequestMapping("api/products")

public class ProductController {
	@Autowired
	private ProductService productService;
	
//	public ProductController(ProductService productService) {
//		this.productService = productService;
//	}
	
	@GetMapping
	 public ResponseEntity<List<ProductEntity>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
	@GetMapping("/{id}")
	 public ResponseEntity<?> getProductsById(@PathVariable Long id) {
       return ResponseEntity.ok(productService.getProductById(id));
   }
	
	@PostMapping("/newEntry")
    public String createProduct(@RequestBody ProductDto productDTO) {
        		String message = productService.createProduct(productDTO);
				return message;
        		
    }
	
	@PutMapping("/updateProduct/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDTO) {
        return ResponseEntity.ok(productService.updateProductByPrice(id,productDTO));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDto> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }
	
	
	
}
