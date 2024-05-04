import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit{
  isLoggedIn: boolean = false;
  userEmail: string | null = null;
  isSubscribed: boolean = false;
  apiKey: string | null = null;

  constructor(private authService: AuthService, private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.updateUserStatus();
  }
  updateUserStatus(){
    this.isLoggedIn = this.authService.isLoggedIn();
    if (this.isLoggedIn) {
      this.userEmail = this.authService.getUserEmail();
      this.isSubscribed = this.authService.isSubscribed();
      if (this.isSubscribed) {
        this.apiKey = this.authService.getApiKey();
      }
    }

  }
  pay() {
    const token = this.authService.getToken(); // Assuming you have a method to get the token
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.put(`${environment.apiBaseUrl}/api/user/pay`,token, {headers: headers, responseType: 'text'})
      .subscribe(
        res => {
          console.log('Payment successful', res);
          // Logout after successful payment
          this.logout();
          this.router.navigate(['subscribe-success']);
        },
        err => {
          console.log('Payment failed', err);
          // Handle error here
        }
      );
  }
  logout(){
    this.authService.logout();
    this.updateUserStatus();
  }
}
