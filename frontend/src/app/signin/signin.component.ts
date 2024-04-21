import { Component } from '@angular/core';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrl: './signin.component.css'
})
export class SigninComponent {
  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService){}

  signIn(){
    this.authService.signIn(this.email, this.password).subscribe({
      next: token => {
        this.authService.saveToken(token);
        console.log('token saved');
      },
      error: error => {
        console.log('error getting token', error);
        this.errorMessage = error.error || 'Failed to sign in';
      }
    });
  }
  

}
