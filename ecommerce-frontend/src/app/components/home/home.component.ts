import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { CheckoutService } from '../../services/checkout.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit, OnDestroy {
  hasCheckedOut = false;
  private checkoutSubscription: Subscription = new Subscription();

  constructor(private checkoutService: CheckoutService) {}

  ngOnInit(): void {
    this.checkoutSubscription.add(
      this.checkoutService.hasCheckedOut$.subscribe(hasCheckedOut => {
        this.hasCheckedOut = hasCheckedOut;
      })
    );
  }

  ngOnDestroy(): void {
    this.checkoutSubscription.unsubscribe();
  }
}