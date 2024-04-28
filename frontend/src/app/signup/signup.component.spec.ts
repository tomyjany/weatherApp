// signup.component.spec.ts
// signup.component.spec.ts

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SignupComponent } from './signup.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

import { environment } from '../../environments/environment'; // Import the environment object

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SignupComponent],
      imports: [HttpClientTestingModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    //fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call signUp() and navigate to home on successful signup', () => {
    const mockResponse = { /* Your mock response object here */ };
    const httpPostMock = jest.spyOn(component['http'], 'post').mockReturnValue(of(mockResponse));
    const routerNavigateMock = jest.spyOn(component['router'], 'navigate');

    component.firstName = 'John';
    component.lastName = 'Doe';
    component.email = 'john.doe@example.com';
    component.password = 'securePassword';

    component.signUp();

    expect(httpPostMock).toHaveBeenCalledWith(`${environment.apiBaseUrl}/api/user/signup`, {
      first_name: 'John',
      last_name: 'Doe',
      email: 'john.doe@example.com',
      user_password: 'securePassword',
    });
    expect(routerNavigateMock).toHaveBeenCalledWith(['home']);
    expect(component.errorMessage).toBe('');
  });

it('should handle sign-up error with no message', () => {
  // Arrange
  const errorResponse = { error: {} };
  const httpPostMock = jest.spyOn(component['http'], 'post').mockReturnValue(throwError(errorResponse));
  const consoleErrorMock = jest.spyOn(console, 'error');

  // Act
  component.signUp();

  // Assert
  expect(httpPostMock).toHaveBeenCalled();
  expect(consoleErrorMock).toHaveBeenCalledWith('Error during sign up', errorResponse);
  expect(component.errorMessage).toEqual('Unknown error occurred');
});

 
});