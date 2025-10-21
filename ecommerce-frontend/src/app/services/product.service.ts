import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ProductDto, ProductEntity } from '../models/product.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = environment.api.product;

  constructor(private http: HttpClient) { }

  getAllProducts(): Observable<ProductEntity[]> {
    return this.http.get<ProductEntity[]>(this.apiUrl).pipe(
      catchError(error => {
        console.error('Error fetching products:', error);
        // Return empty array instead of throwing error
        return of([]);
      })
    );
  }

  getProductById(id: number): Observable<ProductEntity> {
    return this.http.get<ProductEntity>(`${this.apiUrl}/${id}`).pipe(
      catchError(error => {
        console.error('Error fetching product:', error);
        throw error;
      })
    );
  }

  createProduct(product: ProductDto): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/newEntry`, product, { 
      responseType: 'text' as 'json' 
    }).pipe(
      catchError(error => {
        console.error('Error creating product:', error);
        throw error;
      })
    );
  }

  updateProduct(id: number, product: ProductDto): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/updateProduct/${id}`, product, {
      responseType: 'text' as 'json'
    }).pipe(
      catchError(error => {
        console.error('Error updating product:', error);
        throw error;
      })
    );
  }

  updateStock(id: number, quantity: number): Observable<ProductDto> {
    return this.http.put<ProductDto>(`${this.apiUrl}/${id}/stock`, null, {
      params: { quantity: quantity.toString() }
    }).pipe(
      catchError(error => {
        console.error('Error updating stock:', error);
        throw error;
      })
    );
  }
}