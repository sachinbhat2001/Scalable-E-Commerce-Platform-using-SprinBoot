// export interface CartItemDTO {
//   productId: number;
//   productName: string;
//   quantity: number;
//   unitPrice: number; // Change from 'price' to 'unitPrice' to match backend
//   totalPrice?: number; // Add totalPrice for backend
//   imageUrl?: string;
// }

// export interface CartDTO {
//   id?: number;
//   userId: number;
//   items: CartItemDTO[];
//   totalPrice: number;
//   totalItems: number;
//   createdAt?: Date;
//   updatedAt?: Date;
// }

export interface CartItemDTO {
  id?: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  totalPrice?: number;
  imageUrl?: string;
}

export interface CartDTO {
  id?: number;
  userId: number;
  items: CartItemDTO[];
  totalAmount: number;
  totalPrice: number;
  totalItems: number;
  createdAt?: Date;
  updatedAt?: Date;
}