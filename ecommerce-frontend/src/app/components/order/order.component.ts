// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { ActivatedRoute, Router } from '@angular/router';
// import { OrderService } from '../../services/order.service';
// import { CartService } from '../../services/cart.service';
// import { UserService } from '../../services/user.service';
// import { CheckoutService } from '../../services/checkout.service';
// import { OrderDTO, CreateOrderRequestDTO } from '../../models/order.model';
// import { CartDTO } from '../../models/cart.model';
// import { UserDTO } from '../../models/user.model';

// @Component({
//   selector: 'app-order',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './order.component.html'
// })
// export class OrderComponent implements OnInit {
//   orders: OrderDTO[] = [];
//   currentOrder: OrderDTO | null = null;
//   userOrders: OrderDTO[] = [];
//   cart: CartDTO | null = null;
//   user: UserDTO | null = null;
  
//   newOrder: CreateOrderRequestDTO = {
//     userId: 0,
//     items: [],
//     shippingAddress: '',
//     paymentMethod: 'CREDIT_CARD'
//   };

//   selectedOrderId: number | null = null;
//   newStatus: string = '';

//   isCreatingOrder = false;
//   isViewingDetails = false;
//   isUpdatingStatus = false;
//   activeTab: 'my-orders' | 'all-orders' | 'create' = 'my-orders';
  
//   checkoutSuccess = false;
//   recentOrderId: number | null = null;

//   constructor(
//     private orderService: OrderService,
//     private cartService: CartService,
//     private userService: UserService,
//     private checkoutService: CheckoutService,
//     private route: ActivatedRoute,
//     public router: Router
//   ) { }

//   ngOnInit(): void {
//     // Automatically load order details when component initializes
//     this.loadOrderDetails();
    
//     // Optional: Listen for route changes if needed
//     this.route.queryParams.subscribe(params => {
//       if (params['checkoutSuccess']) {
//         // Refresh order details if coming from checkout
//         this.loadOrderDetails();
//       }
//     });
//     // this.checkQueryParams();
//     // this.loadUserData();
//   }

//   checkQueryParams(): void {
//     this.route.queryParams.subscribe(params => {
//       if (params['checkoutSuccess'] === 'true') {
//         this.checkoutSuccess = true;
//         this.recentOrderId = params['orderId'] ? Number(params['orderId']) : null;
//         this.activeTab = 'my-orders';
//         this.router.navigate([], {
//           queryParams: {},
//           replaceUrl: true
//         });
//       }
//     });
//   }

//   loadUserData(): void {
//     const userId = 1;
    
//     this.userService.getUserById(userId).subscribe(user => {
//       this.user = user;
//       this.newOrder.userId = userId;
//     });

//     this.loadOrderDetails(userId);
//     this.loadCart(userId);
//   }

//   loadOrderDetails(userId: number): void {
//     this.orderService.getOrderDetails(userId).subscribe({
//       next: (order) => {
//         console.log('Order details loaded:', order);
//         if (order) {
//           this.userOrders = [order];
//           this.currentOrder = order;
//         } else {
//           this.userOrders = [];
//           this.currentOrder = null;
//         }
//       },
//       error: (error) => {
//         console.error('Error loading order details:', error);
//         this.userOrders = [];
//       }
//     });
//   }

//   loadCart(userId: number): void {
//     this.cartService.getCart(userId).subscribe({
//       next: (cart) => {
//         console.log('Cart loaded:', cart);
//         this.cart = cart;
//         if (cart && cart.items.length > 0) {
//           this.newOrder.items = cart.items.map(item => ({
//             productId: item.productId,
//             productName: item.productName,
//             quantity: item.quantity,
//             price: item.unitPrice, // Use unitPrice instead of price
//             imageUrl: item.imageUrl
//           }));
//         }
//       },
//       error: (error) => {
//         console.error('Error loading cart:', error);
//         this.cart = null;
//       }
//     });
//   }

//   createOrder(): void {
//     console.log('Creating order with data:', this.newOrder);
    
//     if (this.newOrder.items.length === 0) {
//       alert('Cannot create order with empty items');
//       return;
//     }

//     if (!this.newOrder.shippingAddress) {
//       alert('Please enter shipping address');
//       return;
//     }

//     this.isCreatingOrder = true;
    
//     const orderData: CreateOrderRequestDTO = {
//       userId: this.newOrder.userId,
//       items: this.newOrder.items,
//       shippingAddress: this.newOrder.shippingAddress,
//       paymentMethod: this.newOrder.paymentMethod
//     };

//     console.log('Sending order data:', orderData);

//     this.orderService.createOrder(orderData).subscribe({
//       next: (response) => {
//         console.log('Order created successfully:', response);
//         this.isCreatingOrder = false;
//         this.checkoutService.setCheckoutSuccess(1);
//         alert('Order created successfully!');
//         this.resetNewOrderForm();
//         this.loadUserData();
//         this.activeTab = 'my-orders';
//         this.clearCart();
//       },
//       error: (error) => {
//         console.error('Error creating order:', error);
//         this.isCreatingOrder = false;
//         alert('Error creating order: ' + (error.error?.message || error.message || 'Unknown error'));
//       }
//     });
//   }

//   clearCart(): void {
//     if (this.cart?.items) {
//       this.cart.items.forEach(item => {
//         this.cartService.removeItem(this.newOrder.userId, item.productId).subscribe();
//       });
//     }
//   }

//   viewOrderDetails(order: OrderDTO): void {
//     this.currentOrder = order;
//     this.isViewingDetails = true;
//   }

//   updateOrderStatus(): void {
//     if (!this.selectedOrderId || !this.newStatus) {
//       alert('Please select an order and status');
//       return;
//     }

//     this.isUpdatingStatus = true;
//     this.orderService.updateOrderStatus(this.selectedOrderId, this.newStatus).subscribe({
//       next: (updatedOrder) => {
//         alert('Order status updated successfully!');
//         this.loadUserData();
//         this.resetStatusForm();
//         this.isUpdatingStatus = false;
//       },
//       error: (error) => {
//         console.error('Error updating order status:', error);
//         alert('Error updating order status: ' + error.message);
//         this.isUpdatingStatus = false;
//       }
//     });
//   }

//   markOrderReady(orderId: number): void {
//     this.orderService.markOrderReady(orderId).subscribe({
//       next: (updatedOrder) => {
//         alert('Order marked as ready!');
//         this.loadUserData();
//       },
//       error: (error) => {
//         console.error('Error marking order ready:', error);
//         alert('Error: ' + error.message);
//       }
//     });
//   }
  
//   getStatusBadgeClass(status: string): string {
//     switch (status?.toUpperCase()) {
//       case 'PENDING': return 'bg-warning text-dark';
//       case 'CONFIRMED': return 'bg-info text-white';
//       case 'PROCESSING': return 'bg-primary text-white';
//       case 'READY': return 'bg-success text-white';
//       case 'SHIPPED': return 'bg-secondary text-white';
//       case 'DELIVERED': return 'bg-dark text-white';
//       case 'CANCELLED': return 'bg-danger text-white';
//       default: return 'bg-light text-dark';
//     }
//   }

//   calculateOrderTotal(order: OrderDTO): number {
//     return order.items.reduce((total, item) => total + (item.price * item.quantity), 0);
//   }

//   private resetNewOrderForm(): void {
//     this.newOrder = {
//       userId: this.user?.id || 0,
//       items: [],
//       shippingAddress: '',
//       paymentMethod: 'CREDIT_CARD'
//     };
//   }

//   private resetStatusForm(): void {
//     this.selectedOrderId = null;
//     this.newStatus = '';
//   }

//   closeOrderDetails(): void {
//     this.isViewingDetails = false;
//     this.currentOrder = null;
//   }

//   setActiveTab(tab: 'my-orders' | 'all-orders' | 'create'): void {
//     this.activeTab = tab;
//     if (tab === 'my-orders') {
//       this.loadUserData();
//     }
//   }

//   refreshOrders(): void {
//     this.loadUserData();
//     this.checkoutSuccess = false;
//   }

//   cancelOrder(orderId: number): void {
//     if (confirm('Are you sure you want to cancel this order?')) {
//       this.orderService.updateOrderStatus(orderId, 'CANCELLED').subscribe({
//         next: (updatedOrder) => {
//           alert('Order cancelled successfully!');
//           this.loadUserData();
//           this.closeOrderDetails();
//         },
//         error: (error) => {
//           console.error('Error cancelling order:', error);
//           alert('Error cancelling order: ' + error.message);
//         }
//       });
//     }
//   }
// }
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { CartService } from '../../services/cart.service';
import { UserService } from '../../services/user.service';
import { CheckoutService } from '../../services/checkout.service';
import { OrderDTO, CreateOrderRequestDTO } from '../../models/order.model';
import { CartDTO } from '../../models/cart.model';
import { UserDTO } from '../../models/user.model';

@Component({
  selector: 'app-order',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order.component.html'
})
export class OrderComponent implements OnInit {
  orders: OrderDTO[] = [];
  currentOrder: OrderDTO | null = null;
  userOrders: OrderDTO[] = []; // This should contain the single order from backend
  cart: CartDTO | null = null;
  user: UserDTO | null = null;
  
  newOrder: CreateOrderRequestDTO = {
    userId: 0,
    items: [],
    shippingAddress: '',
    paymentMethod: 'CREDIT_CARD'
  };

  selectedOrderId: number | null = null;
  newStatus: string = '';

  isCreatingOrder = false;
  isViewingDetails = false;
  isUpdatingStatus = false;
  activeTab: 'my-orders' | 'all-orders' | 'create' = 'my-orders';
  
  checkoutSuccess = false;
  recentOrderId: number | null = null;
  isLoading = true;
  error: string | null = null;

  constructor(
    private orderService: OrderService,
    private cartService: CartService,
    private userService: UserService,
    private checkoutService: CheckoutService,
    private route: ActivatedRoute,
    public router: Router
  ) { }

  ngOnInit(): void {
    // Automatically load order details when component initializes
    this.loadUserData();
    
    // Listen for route changes if needed
    this.route.queryParams.subscribe(params => {
      if (params['checkoutSuccess']) {
        // Refresh order details if coming from checkout
        this.loadUserData();
      }
    });
  }

  loadUserData(): void {
    const userId = 1; // Hardcoded for now, replace with actual user ID from auth service
    
    this.isLoading = true;
    this.error = null;

    // Load user data
    this.userService.getUserById(userId).subscribe({
      next: (user) => {
        this.user = user;
        this.newOrder.userId = userId;
      },
      error: (error) => {
        console.error('Error loading user:', error);
      }
    });

    // Load order details - this is the main call that happens automatically
    this.loadOrderDetails(userId);
    
    // Load cart data
    this.loadCart(userId);
  }

  loadOrderDetails(userId: number): void {
    this.orderService.getOrderDetails(userId).subscribe({
      next: (orders) => {
        console.log('Order details loaded:', orders);
        this.isLoading = false;
        if (orders && orders.length > 0) {
          // CHANGED: Now assign the entire array directly
          this.userOrders = orders;
          // Optionally set the first order as current order
          this.currentOrder = orders[0];
        } else {
          this.userOrders = [];
          this.currentOrder = null;
          this.error = 'No orders found for this user.';
        }
        // if (order) {
        //   // Since backend returns a single OrderDTO, wrap it in an array
        //   this.userOrders = [order];
        //   this.currentOrder = order;
        // } else {
        //   this.userOrders = [];
        //   this.currentOrder = null;
        //   this.error = 'No orders found for this user.';
        // }
      },
      error: (error) => {
        console.error('Error loading order details:', error);
        this.isLoading = false;
        this.error = 'Failed to load order details: ' + (error.error?.message || error.message || 'Unknown error');
        this.userOrders = [];
        this.currentOrder = null;
      }
    });
  }

  loadCart(userId: number): void {
    this.cartService.getCart(userId).subscribe({
      next: (cart) => {
        console.log('Cart loaded:', cart);
        this.cart = cart;
        if (cart && cart.items.length > 0) {
          this.newOrder.items = cart.items.map(item => ({
            productId: item.productId,
            productName: item.productName,
            quantity: item.quantity,
            price: item.unitPrice,
            imageUrl: item.imageUrl
          }));
        }
      },
      error: (error) => {
        console.error('Error loading cart:', error);
        this.cart = null;
      }
    });
  }

  createOrder(): void {
    console.log('Creating order with data:', this.newOrder);
    
    if (this.newOrder.items.length === 0) {
      alert('Cannot create order with empty items');
      return;
    }

    if (!this.newOrder.shippingAddress) {
      alert('Please enter shipping address');
      return;
    }

    this.isCreatingOrder = true;
    
    const orderData: CreateOrderRequestDTO = {
      userId: this.newOrder.userId,
      items: this.newOrder.items,
      shippingAddress: this.newOrder.shippingAddress,
      paymentMethod: this.newOrder.paymentMethod
    };

    console.log('Sending order data:', orderData);

    this.orderService.createOrder(orderData).subscribe({
      next: (response) => {
        console.log('Order created successfully:', response);
        this.isCreatingOrder = false;
        this.checkoutService.setCheckoutSuccess(1);
        alert('Order created successfully!');
        this.resetNewOrderForm();
        this.loadUserData(); // Refresh data after creating order
        this.activeTab = 'my-orders';
        this.clearCart();
      },
      error: (error) => {
        console.error('Error creating order:', error);
        this.isCreatingOrder = false;
        alert('Error creating order: ' + (error.error?.message || error.message || 'Unknown error'));
      }
    });
  }

  clearCart(): void {
    if (this.cart?.items) {
      this.cart.items.forEach(item => {
        this.cartService.removeItem(this.newOrder.userId, item.productId).subscribe();
      });
    }
  }

  viewOrderDetails(order: OrderDTO): void {
    this.currentOrder = order;
    this.isViewingDetails = true;
  }

  updateOrderStatus(): void {
    if (!this.selectedOrderId || !this.newStatus) {
      alert('Please select an order and status');
      return;
    }

    this.isUpdatingStatus = true;
    this.orderService.updateOrderStatus(this.selectedOrderId, this.newStatus).subscribe({
      next: (updatedOrder) => {
        alert('Order status updated successfully!');
        this.loadUserData(); // Refresh data after updating status
        this.resetStatusForm();
        this.isUpdatingStatus = false;
      },
      error: (error) => {
        console.error('Error updating order status:', error);
        alert('Error updating order status: ' + error.message);
        this.isUpdatingStatus = false;
      }
    });
  }

  markOrderReady(orderId: number): void {
    this.orderService.markOrderReady(orderId).subscribe({
      next: (updatedOrder) => {
        alert('Order marked as ready!');
        this.loadUserData(); // Refresh data
      },
      error: (error) => {
        console.error('Error marking order ready:', error);
        alert('Error: ' + error.message);
      }
    });
  }
  
  getStatusBadgeClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'PENDING': return 'bg-warning text-dark';
      case 'CONFIRMED': return 'bg-info text-white';
      case 'PROCESSING': return 'bg-primary text-white';
      case 'READY': return 'bg-success text-white';
      case 'SHIPPED': return 'bg-secondary text-white';
      case 'DELIVERED': return 'bg-dark text-white';
      case 'CANCELLED': return 'bg-danger text-white';
      default: return 'bg-light text-dark';
    }
  }

  calculateOrderTotal(order: OrderDTO): number {
    return order.items.reduce((total, item) => total + (item.price * item.quantity), 0);
  }

  private resetNewOrderForm(): void {
    this.newOrder = {
      userId: this.user?.id || 0,
      items: [],
      shippingAddress: '',
      paymentMethod: 'CREDIT_CARD'
    };
  }

  private resetStatusForm(): void {
    this.selectedOrderId = null;
    this.newStatus = '';
  }

  closeOrderDetails(): void {
    this.isViewingDetails = false;
    this.currentOrder = null;
  }

  setActiveTab(tab: 'my-orders' | 'all-orders' | 'create'): void {
    this.activeTab = tab;
    if (tab === 'my-orders') {
      this.loadUserData(); // Refresh orders when switching to my-orders tab
    }
  }

  refreshOrders(): void {
    this.loadUserData();
    this.checkoutSuccess = false;
  }

  cancelOrder(orderId: number): void {
    if (confirm('Are you sure you want to cancel this order?')) {
      this.orderService.updateOrderStatus(orderId, 'CANCELLED').subscribe({
        next: (updatedOrder) => {
          alert('Order cancelled successfully!');
          this.loadUserData(); // Refresh data after cancellation
          this.closeOrderDetails();
        },
        error: (error) => {
          console.error('Error cancelling order:', error);
          alert('Error cancelling order: ' + error.message);
        }
      });
    }
  }

  // Helper method to check if user has any orders
  hasOrders(): boolean {
    return this.userOrders.length > 0;
  }

  getLatestOrder(): OrderDTO | null {
    return this.userOrders.length > 0 ? this.userOrders[this.userOrders.length - 1] : null;
  }
// getSortedOrders(): OrderDTO[] {
//     return [...this.userOrders].sort((a, b) => 
//         new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
//     );
// }
// getSortedOrders(): OrderDTO[] {
//     return [...this.userOrders].sort((a, b) => 
//       new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
//     );
//   }
  // Get the user's order (single order)
  getUserOrder(): OrderDTO | null {
    return this.userOrders.length > 0 ? this.userOrders[0] : null;
  }
}