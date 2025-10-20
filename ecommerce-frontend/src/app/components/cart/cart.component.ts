import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../services/cart.service';
import { ProductService } from '../../services/product.service';
import { CartDTO, CartItemDTO } from '../../models/cart.model';
import { ProductEntity } from '../../models/product.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.component.html'
})
export class CartComponent implements OnInit {
  cart: CartDTO = {} as CartDTO;
  products: ProductEntity[] = [];
  selectedProductId?: number;
  quantity = 1;

  constructor(
    private cartService: CartService,
    private productService: ProductService
  ) { }

  ngOnInit(): void {
    this.loadProducts();
    this.loadCart(1);
  }

  loadCart(userId: number): void {
    this.cartService.getCart(userId).subscribe(cart => {
      this.cart = cart;
    });
  }

  loadProducts(): void {
    this.productService.getAllProducts().subscribe(products => {
      this.products = products;
    });
  }

  addItem(): void {
    if (this.selectedProductId && this.quantity > 0) {
      const product = this.products.find(p => p.id === this.selectedProductId);
      if (product) {
        const item: CartItemDTO = {
          productId: product.id,
          productName: product.name,
          quantity: this.quantity,
          price: product.price,
          imageUrl: product.imageUrl
        };
        
        this.cartService.addItem(1, item).subscribe(cart => {
          this.cart = cart;
          this.selectedProductId = undefined;
          this.quantity = 1;
        });
      }
    }
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity > 0) {
      this.cartService.updateItemQuantity(1, productId, quantity).subscribe(cart => {
        this.cart = cart;
      });
    } else {
      this.removeItem(productId);
    }
  }

  removeItem(productId: number): void {
    this.cartService.removeItem(1, productId).subscribe(cart => {
      this.cart = cart;
    });
  }

  checkout(): void {
    this.cartService.checkout(1).subscribe(() => {
      alert('Checkout initiated!');
      this.loadCart(1);
    });
  }
}