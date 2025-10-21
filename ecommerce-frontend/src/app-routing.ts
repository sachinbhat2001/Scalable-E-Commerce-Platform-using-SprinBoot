import { Routes } from '@angular/router';
import { UserComponent } from './app/components/user/user.component';
import { ProductComponent } from './app/components/product/product.component';
import { CartComponent } from './app/components/cart/cart.component';
import { OrderComponent } from './app/components/order/order.component';
import { PaymentComponent } from './app/components/payment/payment.component';

export const routes: Routes = [
  { path: 'users', component: UserComponent },
  { path: 'products', component: ProductComponent },
  { path: 'cart', component: CartComponent },
  { path: 'orders', component: OrderComponent },
  { path: 'payments', component: PaymentComponent },
  { path: '', redirectTo: '/products', pathMatch: 'full' }
];