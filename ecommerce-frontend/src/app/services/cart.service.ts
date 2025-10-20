// services/cart.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CartDTO, CartItemDTO } from '../models/cart.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = environment.api.cart;

  constructor(private http: HttpClient) { }

  getCart(userId: number): Observable<CartDTO> {
    return this.http.get<CartDTO>(`${this.apiUrl}/${userId}`);
  }

  addItem(userId: number, item: CartItemDTO): Observable<CartDTO> {
    return this.http.post<CartDTO>(`${this.apiUrl}/${userId}/items`, item);
  }

  updateItemQuantity(userId: number, productId: number, quantity: number): Observable<CartDTO> {
    return this.http.put<CartDTO>(`${this.apiUrl}/${userId}/items/${productId}`, null, {
      params: { quantity: quantity.toString() }
    });
  }

  removeItem(userId: number, productId: number): Observable<CartDTO> {
    return this.http.delete<CartDTO>(`${this.apiUrl}/${userId}/items/${productId}`);
  }

  checkout(userId: number): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/${userId}/checkout`, {});
  }
}