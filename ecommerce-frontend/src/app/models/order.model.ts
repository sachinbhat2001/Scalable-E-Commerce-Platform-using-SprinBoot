// models/order.model.ts
export interface OrderDTO {
  id?: number;
  userId: number;
  items: OrderItemDTO[];
  totalAmount: number;
  status: string;
  shippingAddress: string;
  paymentMethod: string;
  createdAt?: Date;
  updatedAt?: Date;
  userName?: string;
  userEmail?: string;
}

export interface OrderItemDTO {
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  imageUrl?: string;
}

export interface OrderStatusUpdateRequestDTO {
  status: string;
}

export interface CreateOrderRequestDTO {
  userId: number;
  items: OrderItemDTO[];
  shippingAddress: string;
  paymentMethod: string;
}