import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SigninComponent } from './signin.component';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';
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
  saveToken(token: string) {
    // Mock the saveToken method
    console.log('Saving token:', token);
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

  it('should call authService.signIn with email and password on signIn', () => {
    const authService = TestBed.inject(AuthService);
  
    // Spy on the signIn method of the authService
    const signInSpy = jest.spyOn(authService, 'signIn').mockReturnValue(of('dummy-token'));
  
    // Set email and password
    component.email = 'test@example.com';
    component.password = 'password';
  
    // Call the signIn method
    component.signIn();
  
    // Check if signIn method of authService was called with the correct parameters
    expect(signInSpy).toHaveBeenCalledWith('test@example.com', 'password');
  });
  
 
it('should navigate to home page if token is valid', () => {
  const authService = TestBed.inject(AuthService);
  const router = TestBed.inject(Router);

  // Mock the signIn method to return an observable with a dummy token
  jest.spyOn(authService, 'signIn').mockReturnValue(of('dummy-token'));
  jest.spyOn(authService, 'saveToken');

  // Spy on the router navigate method
  const navigateSpy = jest.spyOn(router, 'navigate');

  // Set email and password
  component.email = 'test@example.com';
  component.password = 'password';

  // Call the signIn method
  component.signIn();

  // Check if signIn method of authService was called with the correct parameters
  expect(authService.signIn).toHaveBeenCalledWith('test@example.com', 'password');
  // Check if saveToken method of authService was called with the correct token
  expect(authService.saveToken).toHaveBeenCalledWith('dummy-token');
  // Check if router navigate method was called with the correct route
  expect(navigateSpy).toHaveBeenCalledWith(['']); // Check navigation
});

it('should handle invalid token', () => {
  const authService = TestBed.inject(AuthService);
  const router = TestBed.inject(Router);

  // Mock the signIn method to return an observable with a falsy token (null)
  jest.spyOn(authService, 'signIn').mockReturnValue(of(null));

  // Spy on the router navigate method
  const navigateSpy = jest.spyOn(router, 'navigate');

  // Set email and password
  component.email = 'test@example.com';
  component.password = 'password';

  // Call the signIn method
  component.signIn();

  // Check if signIn method of authService was called with the correct parameters
  expect(authService.signIn).toHaveBeenCalledWith('test@example.com', 'password');
  // Check if router navigate method was called with the correct route
  expect(navigateSpy).not.toHaveBeenCalled(); // Ensure navigation didn't happen
}); 
it('should handle sign-in error', () => {
  // Arrange
  const authService = TestBed.inject(AuthService);
  const errorResponse = { message: 'Failed to sign in' };
  const signInSpy = jest.spyOn(authService, 'signIn').mockReturnValue(throwError(errorResponse));
  const consoleSpy = jest.spyOn(console, 'log');

  // Act
  component.email = 'test@example.com';
  component.password = 'password';
  component.signIn();

  // Assert
  expect(signInSpy).toHaveBeenCalledWith('test@example.com', 'password');
  expect(consoleSpy).toHaveBeenCalledWith('error getting token', errorResponse);
  expect(component.errorMessage).toEqual(errorResponse.message);
});


})
