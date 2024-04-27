import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HomeComponent } from './home.component';
import { AuthService } from '../auth.service';

// Define a mock class for AuthService
class MockAuthService {
  isLoggedIn = jest.fn().mockReturnValue(false);
  getUserEmail = jest.fn().mockReturnValue(null);
  isSubscribed = jest.fn().mockReturnValue(false);
  logout = jest.fn();
}

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let mockAuthService:MockAuthService;
  let authService:AuthService;
  //let authService:Partial<AuthService>;
  beforeEach(async () => {
    mockAuthService = new MockAuthService();
    await TestBed.configureTestingModule({
      declarations: [HomeComponent],
      providers: [{ provide: AuthService, useValue: mockAuthService }],
    }).compileComponents();
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService); 
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
  });
  
})
