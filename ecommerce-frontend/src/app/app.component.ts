import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { UserService } from './services/user.service';
import { CheckoutService } from './services/checkout.service';
import { UserDTO } from './models/user.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, FormsModule],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <a class="navbar-brand" routerLink="/">E-Commerce</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <!-- Left Navigation -->
          <div class="navbar-nav me-auto">
            <a class="nav-link" routerLink="/home" routerLinkActive="active">Home</a>
            <a class="nav-link" routerLink="/users" routerLinkActive="active">Users</a>
            <a class="nav-link" routerLink="/products" routerLinkActive="active">Products</a>
            <a class="nav-link" routerLink="/cart" routerLinkActive="active">Cart</a>
            
            <!-- Orders Tab - Only show after checkout -->
            <a *ngIf="hasCheckedOut" 
               class="nav-link" 
               routerLink="/orders" 
               routerLinkActive="active">
               Orders
               <span *ngIf="orderCount > 0" class="badge bg-primary ms-1">
                 {{ orderCount }}
               </span>
            </a>
            
            <a class="nav-link" routerLink="/payments" routerLinkActive="active">Payments</a>
          </div>
          
          <!-- Right Navigation - User Profile -->
          <div class="navbar-nav ms-auto">
            <div class="nav-item dropdown" *ngIf="currentUser; else loginButton">
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                <i class="bi bi-person-circle me-1"></i>
                {{ currentUser.firstName }} {{ currentUser.lastName }}
              </a>
              <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="#" (click)="viewProfile()"><i class="bi bi-person me-2"></i>Profile</a></li>
                <li><a *ngIf="hasCheckedOut" class="dropdown-item" routerLink="/orders"><i class="bi bi-bag me-2"></i>My Orders</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item text-danger" href="#" (click)="logout()"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
              </ul>
            </div>
            <ng-template #loginButton>
              <div class="nav-item">
                <a class="nav-link" href="#" (click)="login()">
                  <i class="bi bi-box-arrow-in-right me-1"></i>
                  Login
                </a>
              </div>
            </ng-template>
            
            <!-- Cart Icon -->
            <div class="nav-item">
              <a class="nav-link position-relative" routerLink="/cart">
                <i class="bi bi-cart3"></i>
                <span *ngIf="cartItemsCount > 0" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                  {{ cartItemsCount }}
                </span>
              </a>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <div class="container mt-4">
      <router-outlet></router-outlet>
    </div>

    <!-- Login Modal -->
    <div *ngIf="showLoginModal" class="modal show d-block" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Login</h5>
            <button type="button" class="btn-close" (click)="closeLoginModal()"></button>
          </div>
          <div class="modal-body">
            <form (ngSubmit)="handleLogin()">
              <div class="mb-3">
                <label class="form-label">Username or Email</label>
                <input type="text" class="form-control" [(ngModel)]="loginData.username" name="username" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Password</label>
                <input type="password" class="form-control" [(ngModel)]="loginData.password" name="password" required>
              </div>
              <div class="d-grid">
                <button type="submit" class="btn btn-primary">Login</button>
              </div>
            </form>
            <div class="mt-3 text-center">
              <p class="mb-2">Demo Users:</p>
              <div class="d-flex justify-content-center gap-2">
                <button class="btn btn-sm btn-outline-secondary" (click)="useDemoUser(1)">User 1</button>
                <button class="btn btn-sm btn-outline-secondary" (click)="useDemoUser(2)">User 2</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div *ngIf="showLoginModal" class="modal-backdrop show"></div>
  `,
  styles: [`
    .nav-link.active {
      font-weight: bold;
      color: #fff !important;
    }
    .dropdown-menu {
      margin-top: 8px;
    }
    .badge {
      font-size: 0.7em;
    }
  `]
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'E-Commerce Frontend';
  currentUser: UserDTO | null = null;
  showLoginModal = false;
  cartItemsCount = 0;
  hasCheckedOut = false;
  orderCount = 0;
  
  private checkoutSubscription: Subscription = new Subscription();
  
  loginData = {
    username: '',
    password: ''
  };

  constructor(
    private userService: UserService,
    private checkoutService: CheckoutService
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadCartCount();
    this.subscribeToCheckoutState();
  }

  ngOnDestroy(): void {
    this.checkoutSubscription.unsubscribe();
  }

  subscribeToCheckoutState(): void {
    this.checkoutSubscription.add(
      this.checkoutService.hasCheckedOut$.subscribe(hasCheckedOut => {
        this.hasCheckedOut = hasCheckedOut;
      })
    );

    this.checkoutSubscription.add(
      this.checkoutService.orderCount$.subscribe(orderCount => {
        this.orderCount = orderCount;
      })
    );
  }

  loadCurrentUser(): void {
    const demoUserId = 1;
    this.userService.getUserById(demoUserId).subscribe({
      next: (user) => {
        this.currentUser = user;
      },
      error: (error) => {
        this.currentUser = null;
      }
    });
  }

  loadCartCount(): void {
    this.cartItemsCount = 3;
  }

  login(): void {
    this.showLoginModal = true;
  }

  closeLoginModal(): void {
    this.showLoginModal = false;
    this.loginData = { username: '', password: '' };
  }

  handleLogin(): void {
    if (this.loginData.username) {
      this.userService.getUserById(1).subscribe(user => {
        this.currentUser = user;
        this.closeLoginModal();
      });
    }
  }

  useDemoUser(userId: number): void {
    this.userService.getUserById(userId).subscribe(user => {
      this.currentUser = user;
      this.closeLoginModal();
    });
  }

  logout(): void {
    this.currentUser = null;
    this.cartItemsCount = 0;
  }

  viewProfile(): void {
    alert(`Viewing profile of ${this.currentUser?.firstName} ${this.currentUser?.lastName}`);
  }
}