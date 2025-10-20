// services/order.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  OrderDTO, 
  OrderStatusUpdateRequestDTO, 
  CreateOrderRequestDTO 
} from '../models/order.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = environment.api.order;

  constructor(private http: HttpClient) { }

  createOrder(order: CreateOrderRequestDTO): Observable<string> {
    return this.http.post<string>(this.apiUrl, order);
  }

  getOrderDetails(userId: number): Observable<OrderDTO> {
    return this.http.get<OrderDTO>(`${this.apiUrl}/orderDetails/${userId}`);
  }

  updateOrderStatus(orderId: number, status: string): Observable<OrderDTO> {
    const statusRequest: OrderStatusUpdateRequestDTO = { status };
    return this.http.put<OrderDTO>(`${this.apiUrl}/${orderId}/status`, statusRequest);
  }

  markOrderReady(orderId: number): Observable<OrderDTO> {
    return this.http.put<OrderDTO>(`${this.apiUrl}/${orderId}/ready`, {});
  }

  // Get all orders for admin view (you might need to add this endpoint to your backend)
  getAllOrders(): Observable<OrderDTO[]> {
    return this.http.get<OrderDTO[]>(this.apiUrl);
  }

  // Get order by ID
  getOrderById(orderId: number): Observable<OrderDTO> {
    return this.http.get<OrderDTO>(`${this.apiUrl}/${orderId}`);
  }
}