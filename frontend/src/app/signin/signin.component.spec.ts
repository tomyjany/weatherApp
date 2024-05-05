import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SigninComponent } from './signin.component';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { fakeAsync, tick } from '@angular/core/testing';
import { throwError } from 'rxjs';
class MockAuthService {
  signIn(email: string, password: string) {
    // Mock the signIn method to return an observable with a dummy token
    return of('dummy-token');
  }
  signUp(email: string, password: string) {
    // Mock the signUp method to return an observable with different messages based on the email
    if (email === 'test@example.com' && password === 'password') {
      return of({ message: 'Register success' });
    } else if (email === 'existing@example.com') {
      return of({ error: 'email is already registered' });
    } else if (email === 'invalid@example.com') {
      return of({ error: 'WRONG Credentials' });
    } else {
      return of({ error: 'Something went wrong' });
    }
  }
  saveTokenAndApiKey(response: any) {
    // Mock the saveTokenAndApiKey method
    console.log('Saving token and API key:', response);
  }
  getToken() {
    // Mock the getToken method to return a dummy token
    return 'dummy-token';
  }

  getApiKey() {
    // Mock the getApiKey method to return a dummy API key
    return 'dummy-apiKey';
  }
}

describe('SigninComponent', () => {
  let component: SigninComponent;
  let fixture: ComponentFixture<SigninComponent>;
  /*
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SigninComponent],
      providers: [
        { provide: AuthService, useClass: MockAuthService }, // Provide MockAuthService
        { provide: Router, useValue: {} } // Mock Router dependency
      ]
    }).compileComponents();
  
    fixture = TestBed.createComponent(SigninComponent);
    component = fixture.componentInstance;
  });
  */
  beforeEach(async () => {
    const router = {
      navigate: jest.fn()
    };
  
    await TestBed.configureTestingModule({
      declarations: [SigninComponent],
      providers: [
        { provide: AuthService, useClass: MockAuthService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();
  
    fixture = TestBed.createComponent(SigninComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('should initialize properties', () => {
    expect(component.email).toEqual('');
    expect(component.password).toEqual('');
    expect(component.errorMessage).toEqual('');
  });

  it('should call signIn on authService with email and password', () => {
    const authService = TestBed.inject(AuthService);
    const signInSpy = jest.spyOn(authService, 'signIn').mockReturnValue(of({ token: 'dummy-token', apiKey: 'dummy-apiKey' }));
  
    component.email = 'test@example.com';
    component.password = 'password';
    component.signIn();
  
    expect(signInSpy).toHaveBeenCalledWith('test@example.com', 'password');
  });
  

  it('should set errorMessage on sign in error', () => {
    const authService = TestBed.inject(AuthService);
    jest.spyOn(authService, 'signIn').mockReturnValue(throwError({ error: { error: 'Sign in failed' } }));
  
    component.signIn();
  
    expect(component.errorMessage).toEqual('Sign in failed');
  });
  it('should save token and apiKey and navigate to home on successful sign in', fakeAsync(() => {
    const authService = TestBed.inject(AuthService);
    const router = TestBed.inject(Router);
    jest.spyOn(authService, 'signIn').mockReturnValue(of({ token: 'dummy-token', apiKey: 'dummy-apiKey' }));
    const saveTokenAndApiKeySpy = jest.spyOn(authService, 'saveTokenAndApiKey');
    const navigateSpy = jest.spyOn(router, 'navigate');
    const logSpy = jest.spyOn(console, 'log');
  
    component.signIn();
  
    tick(); // Simulate the passage of time until all pending asynchronous activities finish
  
    expect(saveTokenAndApiKeySpy).toHaveBeenCalledWith({ token: 'dummy-token', apiKey: 'dummy-apiKey' });
    expect(logSpy).toHaveBeenCalledWith('Token saved: ', 'dummy-token');
    expect(logSpy).toHaveBeenCalledWith('API Key saved: ', 'dummy-apiKey');
    expect(navigateSpy).toHaveBeenCalledWith(['']);
    expect(logSpy).toHaveBeenCalledWith('token saved');
  }));

})
