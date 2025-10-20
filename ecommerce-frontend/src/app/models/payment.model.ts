// models/payment.model.ts
export interface PaymentRequestDTO {
  orderId: number;
  userId: number;
  amount: number;
  paymentMethod: string;
  cardNumber?: string;
  expiryDate?: string;
  cvv?: string;
  cardHolderName?: string;
}

export interface PaymentResponseDTO {
  paymentId?: number;
  userId?: number;
  orderId?: number;
  amount?: number;
  status: string;
  paymentMethod?: string;
  message?: string;
  transactionDate?: Date;
  error?: string;
}

export interface PaymentHistoryDTO {
  paymentId: number;
  orderId: number;
  userId: number;
  amount: number;
  status: string;
  paymentMethod: string;
  transactionDate: Date;
  message?: string;
}