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

  signIn(email: string, password: string): Observable<{ token: string, apiKey: string | null }> {
    const body = { email: email, user_password: password };
    return this.http.post<{ token: string, apiKey: string | null }>(`${environment.apiBaseUrl}/api/user/signin`, body);
  }

  saveTokenAndApiKey(response: { token: string, apiKey: string | null }): void {
    console.log('Response: ', response);
    localStorage.setItem('accessToken', response.token);
    if (response.apiKey) {
      localStorage.setItem('apiKey', response.apiKey);
    }
  }


  getToken(): string | null {
    const token = localStorage.getItem('accessToken');
    console.log('Saved token: ', token);
    return token;
  }

  getApiKey(): string | null {
    return localStorage.getItem('apiKey');
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
  addFavoriteCity(city: string): Observable<any> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No token found');
    }
  
    const headers = { 'Authorization': `Bearer ${token}` };
    return this.http.post(`${environment.apiBaseUrl}/api/user/addfavorite?c=${city}`, null, { headers });
  }
  
  getFavoriteCities(): Observable<any> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No token found');
    }
  
    const headers = { 'Authorization': `Bearer ${token}` };
    return this.http.get(`${environment.apiBaseUrl}/api/user/favorites`, { headers });
  }
}
