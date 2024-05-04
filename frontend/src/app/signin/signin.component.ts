import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrl: './signin.component.css'
})
export class SigninComponent {
  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router){}

  signIn(){
    this.authService.signIn(this.email, this.password).subscribe({
      next: response => {
        if (response){
          this.authService.saveTokenAndApiKey(response);
          console.log('Token saved: ', this.authService.getToken());
          console.log('API Key saved: ', this.authService.getApiKey());
          this.router.navigate(['']);
          console.log('token saved');
        }
      },
      error: error => {
        console.log('error getting token', error);
        this.errorMessage = error.error || 'Failed to sign in';
      }
    });
  }
  

}
