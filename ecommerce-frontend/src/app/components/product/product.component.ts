import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { ProductDto, ProductEntity } from '../../models/product.model';

@Component({
  selector: 'app-product',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product.component.html'
})
export class ProductComponent implements OnInit {
  products: ProductEntity[] = [];
  selectedProduct: ProductDto = {} as ProductDto;
  isEditing = false;

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getAllProducts().subscribe(products => {
      this.products = products;
    });
  }

  createProduct(): void {
    this.productService.createProduct(this.selectedProduct).subscribe(() => {
      this.loadProducts();
      this.selectedProduct = {} as ProductDto;
    });
  }

  updateStock(productId: number, quantity: number): void {
    this.productService.updateStock(productId, quantity).subscribe(() => {
      this.loadProducts();
    });
  }
}