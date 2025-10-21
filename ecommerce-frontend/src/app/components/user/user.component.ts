import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { UserDTO } from '../../models/user.model';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user.component.html'
})
export class UserComponent implements OnInit {
  users: UserDTO[] = [];
  selectedUser: UserDTO = {} as UserDTO;
  isEditing = false;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe(users => {
      this.users = users;
    });
  }

  createUser(): void {
    this.userService.createUser(this.selectedUser).subscribe(() => {
      this.loadUsers();
      this.selectedUser = {} as UserDTO;
    });
  }

  updateUser(): void {
    if (this.selectedUser.id) {
      this.userService.updateUser(this.selectedUser.id, this.selectedUser).subscribe(() => {
        this.loadUsers();
        this.cancelEdit();
      });
    }
  }

  deleteUser(id: number): void {
    this.userService.deleteUser(id).subscribe(() => {
      this.loadUsers();
    });
  }

  editUser(user: UserDTO): void {
    this.selectedUser = { ...user };
    this.isEditing = true;
  }

  cancelEdit(): void {
    this.selectedUser = {} as UserDTO;
    this.isEditing = false;
  }
}