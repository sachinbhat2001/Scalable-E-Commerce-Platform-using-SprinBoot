// models/user.model.ts
export interface UserDTO {
  id?: number;
  username: string;
  email: string;
  password?: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  createdAt?: Date;
  updatedAt?: Date;
}