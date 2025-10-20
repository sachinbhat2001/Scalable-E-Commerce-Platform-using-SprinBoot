// services/payment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PaymentRequestDTO, PaymentResponseDTO } from '../models/payment.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = environment.api.payment;

  constructor(private http: HttpClient) { }

  processPayment(paymentRequest: PaymentRequestDTO): Observable<PaymentResponseDTO> {
    return this.http.post<PaymentResponseDTO>(`${this.apiUrl}/process`, paymentRequest);
  }

  getPaymentById(paymentId: number): Observable<PaymentResponseDTO> {
    return this.http.get<PaymentResponseDTO>(`${this.apiUrl}/${paymentId}`);
  }

  getPaymentByOrderId(orderId: number): Observable<PaymentResponseDTO> {
    return this.http.get<PaymentResponseDTO>(`${this.apiUrl}/order/${orderId}`);
  }

  healthCheck(): Observable<string> {
    return this.http.get<string>(`${this.apiUrl}/health`);
  }

  testRabbitMQ(): Observable<string> {
    return this.http.get<string>(`${this.apiUrl}/test-rabbitmq`);
  }
}