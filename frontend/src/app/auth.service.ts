import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient,private router: Router) {}

  signIn(email: string, password: string): Observable<any> {
    const body = { email: email, user_password: password };
    return this.http.post(`${environment.apiBaseUrl}/api/user/signin`, body, { responseType: 'text' });
  }

  saveToken(token: string): void {
    localStorage.setItem('accessToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
    // Decode token to access user information
  decodeToken(): any {
    const token = this.getToken();
    if (!token) return null;
    return jwtDecode(token);
  }

  // Get user's email from token
  getUserEmail(): string | null {
    const decoded = this.decodeToken();
    return decoded ? decoded.email : null;
  }

  // Check if the user has the 'ROLE_SUBSCRIBED' authority
  isSubscribed(): boolean {
    const decoded = this.decodeToken();
    return decoded && decoded.role === 'ROLE_SUBSCRIBED';
  }
  logout(): void {
    localStorage.removeItem('accessToken');  // Clear the token from storage
    this.router.navigate(['']);       // Redirect to the sign-in page
  }
}
