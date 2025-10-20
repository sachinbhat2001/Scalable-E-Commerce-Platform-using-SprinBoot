// services/product.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductDto, ProductEntity } from '../models/product.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = environment.api.product;

  constructor(private http: HttpClient) { }

  getAllProducts(): Observable<ProductEntity[]> {
    return this.http.get<ProductEntity[]>(this.apiUrl);
  }

  getProductById(id: number): Observable<ProductEntity> {
    return this.http.get<ProductEntity>(`${this.apiUrl}/${id}`);
  }

  createProduct(product: ProductDto): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/newEntry`, product);
  }

  updateProduct(id: number, product: ProductDto): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/updateProduct/${id}`, product);
  }

  updateStock(id: number, quantity: number): Observable<ProductDto> {
    return this.http.put<ProductDto>(`${this.apiUrl}/${id}/stock`, null, {
      params: { quantity: quantity.toString() }
    });
  }
}