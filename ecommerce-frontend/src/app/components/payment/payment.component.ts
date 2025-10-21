import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { PaymentService } from '../../services/payment.service';
import { PaymentRequestDTO, PaymentResponseDTO } from '../../models/payment.model';

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment.component.html'
})
export class PaymentComponent implements OnInit {
  paymentRequest: PaymentRequestDTO = {
    orderId: 0,
    userId: 1,
    amount: 0,
    paymentMethod: 'CREDIT_CARD',
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    cardHolderName: ''
  };

  paymentResponse: PaymentResponseDTO | null = null;
  paymentHistory: PaymentResponseDTO[] = [];
  serviceHealth: string = 'Unknown';

  isProcessing = false;
  activeTab: 'process' | 'details' = 'process';

  constructor(
    private paymentService: PaymentService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.checkServiceHealth();
    this.handleOrderRedirect();
  }

  private handleOrderRedirect(): void {
    this.route.queryParams.subscribe(params => {
      if (params['orderId']) {
        this.paymentRequest.orderId = +params['orderId'];
        
        if (params['amount']) {
          this.paymentRequest.amount = +params['amount'];
        }
        
        this.activeTab = 'process';
        
        console.log('Pre-filled payment form with order:', {
          orderId: this.paymentRequest.orderId,
          amount: this.paymentRequest.amount
        });
      }
    });
  }

  checkServiceHealth(): void {
    this.paymentService.healthCheck().subscribe({
      next: (response) => {
        this.serviceHealth = 'Healthy';
      },
      error: (error) => {
        this.serviceHealth = 'Unhealthy';
      }
    });
  }

  processPayment(): void {
    if (!this.validatePaymentForm()) {
      return;
    }

    this.isProcessing = true;
    this.paymentService.processPayment(this.paymentRequest).subscribe({
      next: (response) => {
        this.paymentResponse = response;
        this.isProcessing = false;
        
        if (response.status === 'SUCCESS') {
          alert('Payment processed successfully!');
          this.resetPaymentForm();
        } else {
          alert(`Payment failed: ${response.message}`);
        }
      },
      error: (error) => {
        this.paymentResponse = {
          status: 'ERROR',
          message: error.message
        };
        this.isProcessing = false;
        alert('Payment processing failed: ' + error.message);
      }
    });
  }

  getPaymentById(paymentId: number): void {
    this.paymentService.getPaymentById(paymentId).subscribe({
      next: (response) => {
        this.paymentResponse = response;
        this.activeTab = 'details';
      },
      error: (error) => {
        alert('Error fetching payment: ' + error.message);
      }
    });
  }

  getPaymentByOrderId(orderId: number): void {
    this.paymentService.getPaymentByOrderId(orderId).subscribe({
      next: (response) => {
        this.paymentResponse = response;
        this.activeTab = 'details';
      },
      error: (error) => {
        alert('Error fetching payment: ' + error.message);
      }
    });
  }

  testRabbitMQ(): void {
    this.paymentService.testRabbitMQ().subscribe({
      next: (response) => {
        alert('RabbitMQ test successful: ' + response);
      },
      error: (error) => {
        alert('RabbitMQ test failed: ' + error.message);
      }
    });
  }

  private validatePaymentForm(): boolean {
    if (!this.paymentRequest.orderId) {
      alert('Please enter an order ID');
      return false;
    }

    if (this.paymentRequest.paymentMethod === 'CREDIT_CARD' || this.paymentRequest.paymentMethod === 'DEBIT_CARD') {
      if (!this.paymentRequest.cardNumber || !this.paymentRequest.expiryDate || !this.paymentRequest.cvv) {
        alert('Please fill in all card details');
        return false;
      }
    }

    return true;
  }

  private resetPaymentForm(): void {
    this.paymentRequest = {
      orderId: 0,
      userId: 1,
      amount: 0,
      paymentMethod: 'CREDIT_CARD',
      cardNumber: '',
      expiryDate: '',
      cvv: '',
      cardHolderName: ''
    };
  }

  getStatusBadgeClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'SUCCESS': return 'bg-success text-white';
      case 'PENDING': return 'bg-warning text-dark';
      case 'FAILED': return 'bg-danger text-white';
      case 'ERROR': return 'bg-danger text-white';
      default: return 'bg-secondary text-white';
    }
  }

  setActiveTab(tab: 'process' | 'details'): void {
    this.activeTab = tab;
  }
}