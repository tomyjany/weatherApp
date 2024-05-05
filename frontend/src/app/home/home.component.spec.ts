import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeComponent } from './home.component';
import { AuthService } from '../auth.service';
import { environment } from '../../environments/environment';
import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { NgZone } from '@angular/core';
import { Component } from '@angular/core';
@Component({
  selector: 'app-weather',
  template: '',

})
class WeatherComponentStub {}
class MockAuthService {
  isLoggedIn = jest.fn().mockReturnValue(false);
  getUserEmail = jest.fn().mockReturnValue(null);
  isSubscribed = jest.fn().mockReturnValue(false);
  logout = jest.fn();
  getToken = jest.fn().mockReturnValue('test-token');
  getApiKey = jest.fn().mockReturnValue('test-api-key');
}
let router: Router;
describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let mockAuthService:MockAuthService;
  let authService:AuthService;
  //let authService:Partial<AuthService>;
  let httpMock:HttpTestingController;
  beforeEach(async () => {
    mockAuthService = new MockAuthService();
    await TestBed.configureTestingModule({
      imports : [HttpClientTestingModule],
      declarations: [HomeComponent,WeatherComponentStub],
      providers: [{ provide: AuthService, useValue: mockAuthService }],
    }).compileComponents();
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService); 
    httpMock = TestBed.inject(HttpTestingController);
    jest.spyOn(console, 'log').mockImplementation(()=>{});
  });
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('should update user status on initialization', () => {
    (authService.isLoggedIn as jest.Mock).mockReturnValue(true);
    (authService.getUserEmail as jest.Mock).mockReturnValue('test@example.com');
    (authService.isSubscribed as jest.Mock).mockReturnValue(true);
  
    component.ngOnInit();
  
    expect(component.isLoggedIn).toBe(true);
    expect(component.userEmail).toBe('test@example.com');
    expect(component.isSubscribed).toBe(true);
  });
  
  it('should call logout and update user status', () => {
    component.logout();
  
    expect(authService.logout).toHaveBeenCalled();
    expect(component.isLoggedIn).toBe(false);
    expect(component.userEmail).toBeNull();
    expect(component.isSubscribed).toBe(false);
    httpMock.verify();
  });
  it('should make a payment when pay() is called', () => {
    component.pay();

    const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/user/pay`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${authService.getToken()}`);
    expect(req.request.body).toBe(authService.getToken());

    req.flush('Payment successful');
    httpMock.verify();
  });

  it('should handle payment failure', () => {
    const mockError = { status: 500, statusText: 'Server Error' };
  
    component.pay();
  
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/user/pay`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockError, { status: mockError.status, statusText: mockError.statusText });
  
    expect(console.log).toHaveBeenCalledWith(
      'Payment failed',
      expect.objectContaining({
        name: 'HttpErrorResponse',
        status: mockError.status,
        statusText: mockError.statusText,
      })
    );
  });
  
  
})
