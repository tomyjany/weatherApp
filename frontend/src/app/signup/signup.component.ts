import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

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

  constructor(private http: HttpClient) {}

  signUp() {
    const body = {
      first_name: this.firstName,
      last_name: this.lastName,
      email: this.email,
      user_password: this.password
    };

    this.http.post('http://localhost:8080/user/signup', body)
      .subscribe(
        response => console.log('Signed up successfully', response),
        error => console.error('Error signing up ', error)
      );
  }
}

