import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { CartDTO, CartItemDTO } from '../models/cart.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = environment.api.cart;

  constructor(private http: HttpClient) { }

  getCart(userId: number): Observable<CartDTO> {
    return this.http.get<CartDTO>(`${this.apiUrl}/${userId}`).pipe(
      map(cart => {
        // Transform backend response to match frontend structure
        return {
          ...cart,
          totalPrice: cart.totalAmount || 0,
          totalItems: cart.items ? cart.items.reduce((total, item) => total + item.quantity, 0) : 0
        } as CartDTO;
      })
    );
  }

  addItem(userId: number, item: CartItemDTO): Observable<CartDTO> {
    // Prepare the item data in the exact format backend expects
    const backendItem = {
      productId: item.productId,
      productName: item.productName,
      quantity: item.quantity,
      unitPrice: item.unitPrice,
      totalPrice: item.unitPrice * item.quantity
    };

    console.log('Sending to backend:', backendItem);
    
    return this.http.post<CartDTO>(`${this.apiUrl}/${userId}/items`, backendItem).pipe(
      map(cart => {
        // Transform backend response to match frontend structure
        return {
          ...cart,
          totalPrice: cart.totalAmount || 0,
          totalItems: cart.items ? cart.items.reduce((total, item) => total + item.quantity, 0) : 0
        } as CartDTO;
      })
    );
  }

  updateItemQuantity(userId: number, productId: number, quantity: number): Observable<CartDTO> {
    return this.http.put<CartDTO>(`${this.apiUrl}/${userId}/items/${productId}`, null, {
      params: { quantity: quantity.toString() }
    }).pipe(
      map(cart => {
        return {
          ...cart,
          totalPrice: cart.totalAmount || 0,
          totalItems: cart.items ? cart.items.reduce((total, item) => total + item.quantity, 0) : 0
        } as CartDTO;
      })
    );
  }

  removeItem(userId: number, productId: number): Observable<CartDTO> {
    return this.http.delete<CartDTO>(`${this.apiUrl}/${userId}/items/${productId}`).pipe(
      map(cart => {
        return {
          ...cart,
          totalPrice: cart.totalAmount || 0,
          totalItems: cart.items ? cart.items.reduce((total, item) => total + item.quantity, 0) : 0
        } as CartDTO;
      })
    );
  }

  checkout(userId: number): Observable<{ message: string }> {
    return this.http.post(
      `${this.apiUrl}/${userId}/checkout`, 
      {},
      { responseType: 'text' } // Add this to handle plain text response
    ).pipe(
      map((response: string) => {
        // Convert the plain text response to a proper object
        return { 
          message: response 
        };
      })
    );
  }
}