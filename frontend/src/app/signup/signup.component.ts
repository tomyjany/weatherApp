import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
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

    this.http.post(`${environment.apiBaseUrl}/user/signup`, body)
      .subscribe({

        next:response => console.log('Signed up successfully', response),
        error:error => console.error('Error signing up ', error)
  });
  }
}

