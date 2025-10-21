import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CheckoutService {
  private hasCheckedOutSubject = new BehaviorSubject<boolean>(false);
  private orderCountSubject = new BehaviorSubject<number>(0);

  // Observables
  hasCheckedOut$ = this.hasCheckedOutSubject.asObservable();
  orderCount$ = this.orderCountSubject.asObservable();

  constructor() {
    // Load initial state from localStorage
    const hasCheckedOut = localStorage.getItem('hasCheckedOut') === 'true';
    const orderCount = localStorage.getItem('orderCount') ? parseInt(localStorage.getItem('orderCount')!) : 0;
    
    this.hasCheckedOutSubject.next(hasCheckedOut);
    this.orderCountSubject.next(orderCount);
  }

  setCheckoutSuccess(orderCount: number = 1): void {
    this.hasCheckedOutSubject.next(true);
    this.orderCountSubject.next(orderCount);
    
    // Persist to localStorage
    localStorage.setItem('hasCheckedOut', 'true');
    localStorage.setItem('orderCount', orderCount.toString());
  }

  getHasCheckedOut(): boolean {
    return this.hasCheckedOutSubject.value;
  }

  getOrderCount(): number {
    return this.orderCountSubject.value;
  }

  resetCheckout(): void {
    this.hasCheckedOutSubject.next(false);
    this.orderCountSubject.next(0);
    localStorage.removeItem('hasCheckedOut');
    localStorage.removeItem('orderCount');
  }
}