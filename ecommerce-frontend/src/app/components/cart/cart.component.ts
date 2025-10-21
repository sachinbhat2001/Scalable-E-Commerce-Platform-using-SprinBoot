import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { ProductService } from '../../services/product.service';
import { CheckoutService } from '../../services/checkout.service';
import { CartDTO, CartItemDTO } from '../../models/cart.model';
import { ProductEntity } from '../../models/product.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.component.html'
})
export class CartComponent implements OnInit {
  cart: CartDTO = {
    userId: 1,
    items: [],
    totalPrice: 0,
    totalItems: 0,
    totalAmount: 0
  };
  products: ProductEntity[] = [];
  selectedProductId: number | null = null;
  quantity = 1;
  isCheckingOut = false;
  isLoading = true;

  constructor(
    private cartService: CartService,
    private productService: ProductService,
    private checkoutService: CheckoutService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadProducts();
    this.loadCart(1);
  }

  loadCart(userId: number): void {
    this.cartService.getCart(userId).subscribe({
      next: (cart) => {
        console.log('Cart loaded from backend:', cart);
        this.cart = {
          ...cart,
          items: cart.items || [],
          totalPrice: cart.totalAmount || 0,
          totalItems: cart.items ? cart.items.reduce((total, item) => total + item.quantity, 0) : 0
        };
      },
      error: (error) => {
        console.error('Error loading cart:', error);
        this.cart = {
          userId: 1,
          items: [],
          totalPrice: 0,
          totalItems: 0,
          totalAmount: 0
        };
      }
    });
  }

  loadProducts(): void {
    this.isLoading = true;
    this.productService.getAllProducts().subscribe({
      next: (products) => {
        this.products = products || [];
        this.isLoading = false;
        console.log('Products loaded:', this.products);
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.products = [];
        this.isLoading = false;
      }
    });
  }

  onProductSelect(event: any): void {
    console.log('Product selection event:', event);
    const value = event?.target?.value;
    this.selectedProductId = value && value !== '' ? Number(value) : null;
    console.log('Selected product ID:', this.selectedProductId);
  }

  onProductChange(): void {
    console.log('Selected product changed to:', this.selectedProductId);
  }

  onQuantityChange(): void {
    console.log('Quantity changed to:', this.quantity);
  }

  addItem(): void {
    console.log('Add item called with:', {
      selectedProductId: this.selectedProductId,
      quantity: this.quantity,
      products: this.products
    });

    if (!this.selectedProductId || this.quantity < 1) {
      alert('Please select a product and enter a valid quantity');
      return;
    }

    const product = this.products.find(p => p.id === this.selectedProductId);
    
    if (!product) {
      console.error('Product not found with ID:', this.selectedProductId);
      alert('Selected product not found. Please try again.');
      return;
    }

    console.log('Found product:', product);

    const unitPrice = product.price || 0;
    if (unitPrice <= 0) {
      alert('Product price is invalid. Please select a different product.');
      return;
    }

    const item: CartItemDTO = {
      productId: product.id,
      productName: product.name,
      quantity: this.quantity,
      unitPrice: unitPrice,
      imageUrl: product.imageUrl
    };

    console.log('Sending item to backend:', item);
    
    this.cartService.addItem(1, item).subscribe({
      next: (cart) => {
        console.log('Cart after adding item:', cart);
        this.cart = cart;
        this.selectedProductId = null;
        this.quantity = 1;
        alert('Item added to cart successfully!');
      },
      error: (error) => {
        console.error('Error adding item to cart:', error);
        alert('Error adding item to cart: ' + (error.error?.message || error.message || 'Unknown error'));
      }
    });
  }

  updateQuantity(productId: number, quantity: string): void {
    const numQuantity = Number(quantity);
    if (numQuantity > 0) {
      this.cartService.updateItemQuantity(1, productId, numQuantity).subscribe({
        next: (cart) => {
          this.cart = cart;
        },
        error: (error) => {
          console.error('Error updating quantity:', error);
          alert('Error updating quantity: ' + error.message);
        }
      });
    } else {
      this.removeItem(productId);
    }
  }

  removeItem(productId: number): void {
    this.cartService.removeItem(1, productId).subscribe({
      next: (cart) => {
        this.cart = cart;
        alert('Item removed from cart!');
      },
      error: (error) => {
        console.error('Error removing item:', error);
        alert('Error removing item: ' + error.message);
      }
    });
  }
  checkout(): void {
  if (!this.cart?.items || this.cart.items.length === 0) {
    alert('Your cart is empty. Add items before checkout.');
    return;
  }

  this.isCheckingOut = true;
  
  this.cartService.checkout(1).subscribe({
    next: (response) => {
      this.isCheckingOut = false;
      this.checkoutService.setCheckoutSuccess(1);
      alert('Checkout successful! Orders tab is now available.');
      
      // Navigate to orders tab - it will automatically make the REST call
      this.router.navigate(['/orders']);
    },
    error: (error) => {
      this.isCheckingOut = false;
      console.error('Checkout error:', error);
      alert('Checkout failed: ' + (error.error?.message || error.message || 'Unknown error'));
    }
  });
}
// checkout(): void {
//   if (!this.cart?.items || this.cart.items.length === 0) {
//     alert('Your cart is empty. Add items before checkout.');
//     return;
//   }

//   this.isCheckingOut = true;
  
//   this.cartService.checkout(1).subscribe({
//     next: (response) => {
//       this.isCheckingOut = false;
//       this.checkoutService.setCheckoutSuccess(1);
//       alert('Checkout successful! Orders tab is now available.');
      
//       // Navigate without orderId since it's not in the response
//       this.router.navigate(['/orders'], { 
//         queryParams: { 
//           checkoutSuccess: true 
//         }
//       });
//     },
//     error: (error) => {
//       this.isCheckingOut = false;
//       console.error('Checkout error:', error);
//       alert('Checkout failed: ' + (error.error?.message || error.message || 'Unknown error'));
//     }
//   });
// }
  // checkout(): void {
  //   if (!this.cart?.items || this.cart.items.length === 0) {
  //     alert('Your cart is empty. Add items before checkout.');
  //     return;
  //   }

  //   this.isCheckingOut = true;
    
  //   this.cartService.checkout(1).subscribe({
  //     next: (response) => {
  //       this.isCheckingOut = false;
  //       this.checkoutService.setCheckoutSuccess(1);
  //       alert('Checkout successful! Orders tab is now available.');
  //       this.router.navigate(['/orders'], { 
  //         queryParams: { 
  //           orderId: response.orderId,
  //           checkoutSuccess: true 
  //         }
  //       });
  //     },
  //     error: (error) => {
  //       this.isCheckingOut = false;
  //       console.error('Checkout error:', error);
  //       alert('Checkout failed: ' + (error.error?.message || error.message || 'Unknown error'));
  //     }
  //   });
  // }

  getTotalItems(): number {
    return this.cart?.items?.reduce((total, item) => total + item.quantity, 0) || 0;
  }

  getTotalPrice(): number {
    return this.cart?.totalPrice || 0;
  }

  canAddToCart(): boolean {
    return this.selectedProductId !== null && this.quantity >= 1;
  }

  getSelectedProductName(): string {
    if (!this.selectedProductId) return 'None';
    const product = this.products.find(p => p.id === this.selectedProductId);
    return product ? product.name : 'Unknown';
  }
}