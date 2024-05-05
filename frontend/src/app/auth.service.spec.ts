import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { environment } from '../environments/environment';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';


describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Verify that no unmatched requests are outstanding
    localStorage.removeItem('accessToken'); // Clear the token from storage
  });


   it('should return false if token does not exist in localStorage', () => {
    expect(service.isLoggedIn()).toBeFalsy();
  });

  it('should decode token and return user information', () => {
    localStorage.setItem('accessToken', 'dummy-token');
    const decodeTokenSpy = jest.spyOn(service, 'decodeToken').mockReturnValue({ email: 'test@example.com' });

    const decoded = service.decodeToken();
    
    expect(decodeTokenSpy).toHaveBeenCalled();
    expect(decoded).toBeDefined();
    expect(decoded.email).toEqual('test@example.com');
  });

  it('should return user email from decoded token', () => {
    const decodeTokenSpy = jest.spyOn(service, 'decodeToken').mockReturnValue({ email: 'test@example.com' });

    const email = service.getUserEmail();
    
    expect(decodeTokenSpy).toHaveBeenCalled();
    expect(email).toEqual('test@example.com');
  });

  it('should return true if user is subscribed', () => {
    const decodeTokenSpy = jest.spyOn(service, 'decodeToken').mockReturnValue({ role: 'ROLE_SUBSCRIBED' });

    expect(service.isSubscribed()).toBeTruthy();
  });

  it('should return false if user is not subscribed', () => {
    const decodeTokenSpy = jest.spyOn(service, 'decodeToken').mockReturnValue({ role: 'ROLE_NOT_SUBSCRIBED' });

    expect(service.isSubscribed()).toBeFalsy();
  });

  it('should remove token from localStorage and navigate to home page', () => {
    localStorage.setItem('accessToken', 'dummy-token');
    const navigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate');

    service.logout();
    
    expect(localStorage.getItem('accessToken')).toBeNull();
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  });
  it('should send a POST request with user credentials and return an observable', () => {
    const email = 'test@example.com';
    const password = 'password';
    const mockResponse = 'dummy-token';

    service.signIn(email, password).subscribe(token => {
      expect(token).toEqual(mockResponse); // Assert that the token returned matches the expected response
    });

    const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/user/signin`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email, user_password: password });

    req.flush(mockResponse); // Simulate a successful HTTP response
  });
  it('should return user email when decoded token exists', () => {
    const mockDecodedToken = { email: 'test@example.com' };
    jest.spyOn(service, 'decodeToken').mockReturnValue(mockDecodedToken);

    const userEmail = service.getUserEmail();

    expect(userEmail).toEqual(mockDecodedToken.email); // Ensure user email matches the one in the decoded token
  });

  it('should return null when decoded token does not exist', () => {
    jest.spyOn(service, 'decodeToken').mockReturnValue(null);

    const userEmail = service.getUserEmail();

    expect(userEmail).toBeNull(); // Ensure user email is null when decoded token does not exist
  });
  it('should decode token when it exists', () => {
    // Mock a valid JWT token
    const mockToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c';

    // Mock the getToken method to return the valid token
    jest.spyOn(service, 'getToken').mockReturnValue(mockToken);

    // Call the decodeToken method
    const decoded = service.decodeToken();

    // Assert that the decoded token is defined
    expect(decoded).toBeDefined();
    // Add more specific assertions about the decoded token as needed
  });

  it('should return null when token does not exist', () => {
    // Mock the getToken method to return null
    jest.spyOn(service, 'getToken').mockReturnValue(null);

    // Call the decodeToken method
    const decoded = service.decodeToken();

    // Assert that the decoded token is null
    expect(decoded).toBeNull();
  });


  it('should save token and apiKey in localStorage', () => {
    const response = { token: 'dummy-token', apiKey: 'dummy-apiKey' };
    service.saveTokenAndApiKey(response);
    expect(localStorage.getItem('accessToken')).toEqual('dummy-token');
    expect(localStorage.getItem('apiKey')).toEqual('dummy-apiKey');
  });
  
  it('should return apiKey from localStorage', () => {
    localStorage.setItem('apiKey', 'dummy-apiKey');
    const apiKey = service.getApiKey();
    expect(apiKey).toEqual('dummy-apiKey');
  });
  
  it('should send a POST request to add favorite city and return an observable', () => {
    const city = 'dummy-city';
    const mockResponse = { message: 'City added to favorites' };
    const token = 'dummy-token';
    jest.spyOn(service, 'getToken').mockReturnValue(token);
  
    service.addFavoriteCity(city).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });
  
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/user/addfavorite?c=${city}`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
  
    req.flush(mockResponse);
  });
  
  it('should send a GET request to retrieve favorite cities and return an observable', () => {
    const mockResponse = ['City1', 'City2'];
    const token = 'dummy-token';
    jest.spyOn(service, 'getToken').mockReturnValue(token);
  
    service.getFavoriteCities().subscribe(cities => {
      expect(cities).toEqual(mockResponse);
    });
  
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/user/favorites`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
  
    req.flush(mockResponse);
  });

  it('should throw an error when no token is found', () => {
    jest.spyOn(service, 'getToken').mockReturnValue(null);
  
    expect(() => service.addFavoriteCity('dummy-city')).toThrowError('No token found');
  });

  it('should throw an error when no token is found', () => {
    jest.spyOn(service, 'getToken').mockReturnValue(null);
  
    expect(() => service.getFavoriteCities()).toThrowError('No token found');
  });


});

