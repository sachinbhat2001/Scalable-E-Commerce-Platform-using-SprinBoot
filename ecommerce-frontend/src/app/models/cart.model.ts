// models/cart.model.ts
export interface CartItemDTO {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
  imageUrl?: string;
}

export interface CartDTO {
  id?: number;
  userId: number;
  items: CartItemDTO[];
  totalPrice: number;
  totalItems: number;
  createdAt?: Date;
  updatedAt?: Date;
}