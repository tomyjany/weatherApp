import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { environment } from '../../environments/environment';
import { WeatherComponent } from './weather.component';
import { AuthService } from '../auth.service';
import { of } from 'rxjs';

class MockAuthService {
  isSubscribed = jest.fn().mockReturnValue(false);
  getFavoriteCities = jest.fn().mockReturnValue(of([]));
  addFavoriteCity = jest.fn().mockReturnValue(of({}));
}

describe('WeatherComponent', () => {
  let component: WeatherComponent;
  let fixture: ComponentFixture<WeatherComponent>;
  let httpMock: HttpTestingController;
  let mockAuthService: MockAuthService;

  beforeEach(async () => {
    mockAuthService = new MockAuthService();

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [WeatherComponent],
      providers: [{ provide: AuthService, useValue: mockAuthService }],
    }).compileComponents();

    fixture = TestBed.createComponent(WeatherComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getCurrentWeather on init', () => {
    const spy = jest.spyOn(component, 'getCurrentWeather');
    component.ngOnInit();
    expect(spy).toHaveBeenCalledWith(component.defaultCity);
  });

  it('should call getForecastWeather on init', () => {
    const spy = jest.spyOn(component, 'getForecastWeather');
    component.ngOnInit();
    expect(spy).toHaveBeenCalledWith(component.defaultCity);
  });

  it('should call getHistoricalWeather on init if user is subscribed', () => {
    mockAuthService.isSubscribed.mockReturnValue(true);
    const spy = jest.spyOn(component, 'getHistoricalWeather');
    component.ngOnInit();
    expect(spy).toHaveBeenCalledWith(component.defaultCity, component.convertDateFormat(component.defaultDate));
  });

  it('should call getFavoriteCities on init if user is subscribed', () => {
    mockAuthService.isSubscribed.mockReturnValue(true);
    const spy = jest.spyOn(component, 'getFavoriteCities');
    component.ngOnInit();
    expect(spy).toHaveBeenCalled();
  });

  it('should get current weather', () => {
    const mockData = { icon: 'icon' };
    component.getCurrentWeather(component.defaultCity);
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/weather/current?c=${component.defaultCity}&k=${environment.apiKey}`);
    req.flush(mockData);
    expect(component.currentWeather).toEqual({ ...mockData, iconUrl: `https://openweathermap.org/img/wn/${mockData.icon}@2x.png` });
  });
  
  it('should get historical weather', () => {
    const mockData = { data: 'data' };
    mockAuthService.isSubscribed.mockReturnValue(true);
    component.getHistoricalWeather(component.defaultCity, component.convertDateFormat(component.defaultDate));
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/weather/historical?c=${component.defaultCity}&d=${component.convertDateFormat(component.defaultDate)}&k=${environment.apiKey}`);
    req.flush(mockData);
    expect(component.historicalWeather).toEqual(mockData);
  });
  
  it('should get forecast weather', () => {
    const mockData = { icon: 'icon' };
    component.getForecastWeather(component.defaultCity);
    const req = httpMock.expectOne(`${environment.apiBaseUrl}/api/weather/forecast?c=${component.defaultCity}&k=${environment.apiKey}`);
    req.flush(mockData);
    expect(component.forecastWeather).toEqual({ ...mockData, iconUrl: `https://openweathermap.org/img/wn/${mockData.icon}@2x.png` });
  });

  it('should get favorite cities', () => {
    const mockData = ['city1', 'city2'];
    mockAuthService.getFavoriteCities.mockReturnValue(of(mockData));
    component.getFavoriteCities();
    expect(component.favoriteCities).toEqual(mockData);
  });

  it('should add city to favorites', async () => {
    const mockData = { response: 'response' };
    mockAuthService.addFavoriteCity.mockReturnValue(of(mockData));
    component.currentWeather = { city: 'city' };
    await component.addCityToFavorites();
    expect(mockAuthService.addFavoriteCity).toHaveBeenCalledWith(component.currentWeather.city);
  });

  it('should update weather based on user input', () => {
    const city = 'Test City';
    const date = '2022-12-31';
    jest.spyOn(component, 'getCurrentWeather');
    jest.spyOn(component, 'getForecastWeather');
    jest.spyOn(component, 'getHistoricalWeather');
    mockAuthService.isSubscribed.mockReturnValue(true);
  
    component.updateWeatherUserInput(city, date);
  
    expect(component.selectedCity).toBe(city);
    expect(component.selectedDate).toBe(date);
    expect(component.getCurrentWeather).toHaveBeenCalledWith(city);
    expect(component.getForecastWeather).toHaveBeenCalledWith(city);
    expect(component.getHistoricalWeather).toHaveBeenCalledWith(city, component.convertDateFormat(date));
  });
  
  it('should convert Unix time to Date object', () => {
    const unixTime = 1640995200; // 01-01-2022 @ 00:00:00 (UTC)
    const expectedDate = new Date(Date.UTC(2022, 0, 1, 0, 0, 0));
  
    const result = component.convertUnixTime(unixTime);
  
    expect(result.toISOString()).toEqual(expectedDate.toISOString());
  });
  it('should convert date string to Date object', () => {
    const dateString = '31-12-2022'; // dd-mm-yyyy format
    const expectedDate = new Date(Date.UTC(2022, 11, 31)); // JavaScript months are 0-indexed
  
    const result = component.getDateObject(dateString);
  
    expect(result).toEqual(expectedDate);
  });
  it('should update weather from favorites', () => {
    const city = 'Test City';
    jest.spyOn(component, 'updateWeatherUserInput');
  
    component.updateWeatherFromFavorites(city);
  
    expect(component.selectedCity).toBe(city);
    expect(component.updateWeatherUserInput).toHaveBeenCalledWith(city, component.defaultDate);
  });


});