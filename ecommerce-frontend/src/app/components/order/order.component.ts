import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../services/order.service';
import { CartService } from '../../services/cart.service';
import { UserService } from '../../services/user.service';
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
  userOrders: OrderDTO[] = [];
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

  constructor(
    private orderService: OrderService,
    private cartService: CartService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.loadUserData();
    this.loadAllOrders();
  }

  loadUserData(): void {
    const userId = 1;
    
    this.userService.getUserById(userId).subscribe(user => {
      this.user = user;
      this.newOrder.userId = userId;
    });

    this.loadUserOrders(userId);
    this.loadCart(userId);
  }

  loadAllOrders(): void {
    // For now, we'll use a mock since getAllOrders might not exist
    this.userOrders = []; // Initialize empty
  }

  loadUserOrders(userId: number): void {
    this.orderService.getOrderDetails(userId).subscribe(order => {
      this.userOrders = order ? [order] : [];
    });
  }

  loadCart(userId: number): void {
    this.cartService.getCart(userId).subscribe(cart => {
      this.cart = cart;
      if (cart && cart.items.length > 0) {
        this.newOrder.items = cart.items.map(item => ({
          productId: item.productId,
          productName: item.productName,
          quantity: item.quantity,
          price: item.price,
          imageUrl: item.imageUrl
        }));
      }
    });
  }

  createOrder(): void {
    if (this.newOrder.items.length === 0) {
      alert('Cannot create order with empty items');
      return;
    }

    if (!this.newOrder.shippingAddress) {
      alert('Please enter shipping address');
      return;
    }

    this.isCreatingOrder = true;
    this.orderService.createOrder(this.newOrder).subscribe({
      next: (response) => {
        alert('Order created successfully!');
        this.resetNewOrderForm();
        this.loadUserData();
        this.loadAllOrders();
        this.activeTab = 'my-orders';
        this.isCreatingOrder = false;
      },
      error: (error) => {
        console.error('Error creating order:', error);
        alert('Error creating order: ' + error.message);
        this.isCreatingOrder = false;
      }
    });
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
        this.loadAllOrders();
        this.loadUserData();
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
        this.loadAllOrders();
        this.loadUserData();
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
  }
}