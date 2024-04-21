import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Route, Router } from '@angular/router';
@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  firstName: string = '';
  lastName: string = '';
  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  signUp() {
    //console.log("Signing up with:", this.firstName, this.lastName, this.email, this.password);
    const body = {
      first_name: this.firstName,
      last_name: this.lastName,
      email: this.email,
      user_password: this.password
  };

  this.http.post(`${environment.apiBaseUrl}/user/signup`, body)
    .subscribe({
      next: response => {
        console.log('Signed up successfully', response);
        this.router.navigate(['home']);
        this.errorMessage = '';  // Clear any existing error message
      },
      error: error => {
        console.error('Error during sign up', error);
        this.errorMessage = error.error.message || 'Unknown error occurred';  // Update to use the correct error message property
      }
    });
  }
}

