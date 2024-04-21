import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit{
  isLoggedIn: boolean = false;
  userEmail: string | null = null;
  isSubscribed: boolean = false;

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.updateUserStatus();
  }
  updateUserStatus(){
    this.isLoggedIn = this.authService.isLoggedIn();
    if (this.isLoggedIn) {
      this.userEmail = this.authService.getUserEmail();
      this.isSubscribed = this.authService.isSubscribed();
    }

  }
  logout(){
    this.authService.logout();
    this.updateUserStatus();
  }
}
