package com.product.product_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.product.product_service.model.ProductDto;
import com.product.product_service.model.ProductEntity;
import com.product.product_service.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService{
	
	@Autowired
	private ProductRepository productRepository;
	
//	@Autowired
//	private ProductService productService;
	
	@Override
	public List<ProductEntity> getAllProducts() {
		List<ProductEntity> data = productRepository.findAll();
		return data;
	}

	@Override
    public Optional<ProductEntity> getProductById(Long id) {
        return productRepository.findById(id);
    }

	@Override
	public String createProduct(ProductDto productDTO) {
		ProductEntity entity = new ProductEntity();
        entity.setName(productDTO.getName());
        entity.setDescription(productDTO.getDescription());
        entity.setPrice(productDTO.getPrice());
        entity.setStockQuantity(productDTO.getStockQuantity());
        entity.setCategory(productDTO.getCategory());
        ProductEntity savedEntity = productRepository.save(entity);
        return "Success";
	}

	
	@Override
	public String updateProductByPrice(Long id, ProductDto productDTO) {
		productRepository.updatePriceById(id, productDTO.getPrice());
		return "Success";
	}
	
	 @PutMapping("/{id}")
	 public ProductDto updateProduct(@PathVariable Long id, @RequestBody ProductDto productDTO) {
		 Optional<ProductEntity> optionalProduct = productRepository.findById(id);
	        if (optionalProduct.isEmpty()) {
	            throw new RuntimeException("Product not found with ID: " + id);
	        }

	        ProductEntity product = optionalProduct.get();

	        // Update only non-null fields (partial update)
	        if (productDTO.getName() != null) product.setName(productDTO.getName());
	        if (productDTO.getDescription() != null) product.setDescription(productDTO.getDescription());
	        if (productDTO.getPrice() != null) product.setPrice(productDTO.getPrice());
	        if (productDTO.getCategory() != null) product.setCategory(productDTO.getCategory());
	        if (productDTO.getStockQuantity() != null) product.setStockQuantity(productDTO.getStockQuantity());

	        ProductEntity updatedProduct = productRepository.save(product);

	        // Convert back to DTO
	        ProductDto updatedDTO = new ProductDto();
	        updatedDTO.setId(updatedProduct.getId());
	        updatedDTO.setName(updatedProduct.getName());
	        updatedDTO.setDescription(updatedProduct.getDescription());
	        updatedDTO.setPrice(updatedProduct.getPrice());
	        updatedDTO.setCategory(updatedProduct.getCategory());
	        updatedDTO.setStockQuantity(updatedProduct.getStockQuantity());

	        return updatedDTO;
        //return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @PutMapping("/{id}/stock")
    public ProductDto updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
    	Optional<ProductEntity> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found with ID: " + id);
        }

        ProductEntity product = optionalProduct.get();
        product.setStockQuantity(quantity);

        ProductEntity updatedProduct = productRepository.save(product);

        ProductDto updatedDTO = new ProductDto();
        updatedDTO.setId(updatedProduct.getId());
        updatedDTO.setName(updatedProduct.getName());
        updatedDTO.setDescription(updatedProduct.getDescription());
        updatedDTO.setPrice(updatedProduct.getPrice());
        updatedDTO.setCategory(updatedProduct.getCategory());
        updatedDTO.setStockQuantity(updatedProduct.getStockQuantity());

        return updatedDTO;    }
}
