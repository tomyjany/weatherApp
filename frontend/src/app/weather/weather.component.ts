import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { AuthService } from '../auth.service';
import { EventEmitter, Output } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';
import { tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-weather',
  templateUrl: './weather.component.html',
  styleUrl: './weather.component.css'
})
export class WeatherComponent {
  currentWeather: any;
  historicalWeather: any;
  forecastWeather: any;
  favoriteCities: string[] = []; // Add a new property to store the favorite cities
  defaultCity: string = 'Liberec';
  defaultDate: string = this.getYesterdaysDate(); // get yesterday's date in 'yyyy-MM-dd' format
  selectedDate: string = this.defaultDate;
  selectedCity: string = this.defaultCity;
  @Output() currentWeatherLoaded = new EventEmitter<boolean>();

  constructor(private cdr:ChangeDetectorRef,private http: HttpClient, private authService: AuthService) { } // Inject AuthService

  ngOnInit(): void {
    this.getCurrentWeather(this.defaultCity);
    this.getForecastWeather(this.defaultCity);

    if (this.authService.isSubscribed()) {
      this.getHistoricalWeather(this.defaultCity, this.convertDateFormat(this.defaultDate));
      this.getFavoriteCities();
    }
  }

  getCurrentWeather(city: string): void {
    this.http.get(`${environment.apiBaseUrl}/api/weather/current?c=${city}&k=${environment.apiKey}`).subscribe(data => {
      this.currentWeather = data;
      this.currentWeather.iconUrl = `https://openweathermap.org/img/wn/${this.currentWeather.icon}@2x.png`;
      this.currentWeatherLoaded.emit(true); 
    });
  }
/*
  getHistoricalWeather(city: string, date: string): void {
    if (!this.authService.isSubscribed()) return; // Check if the user is subscribed

    console.log(`Requesting historical weather data for date: ${date}`);
    this.http.get(`${environment.apiBaseUrl}/api/weather/historical?c=${city}&d=${date}&k=${environment.apiKey}`).subscribe(data => {
      this.historicalWeather = data;
    });
  }

  */
  getHistoricalWeather(city: string, date: string): Observable<any> {

    if (!this.authService.isSubscribed()) {
      return of(null); // Return an Observable of null if the user is not subscribed
    }

  
    console.log(`Requesting historical weather data for date: ${date}`);
    return this.http.get(`${environment.apiBaseUrl}/api/weather/historical?c=${city}&d=${date}&k=${environment.apiKey}`).pipe(
      tap(data => {
        this.historicalWeather = data;
      })
    );
  }
  getForecastWeather(city: string): void {
    this.http.get(`${environment.apiBaseUrl}/api/weather/forecast?c=${city}&k=${environment.apiKey}`).subscribe(data => {
      this.forecastWeather = data;
      this.forecastWeather.iconUrl = `https://openweathermap.org/img/wn/${this.forecastWeather.icon}@2x.png`;
    });
  }

  getFavoriteCities(): void {
    this.authService.getFavoriteCities().subscribe(cities => {
      this.favoriteCities = cities;
      this.cdr.detectChanges(); 
    }, error => {
      console.error('Error fetching favorite cities:', error);
    });
  }

  /*
  addCityToFavorites(): void {
    if (!this.currentWeather) {
      console.error('No current weather data available');
      return;
    }
  
    this.authService.addFavoriteCity(this.currentWeather.city).subscribe(response => {
      console.log('City added to favorites:', response);
      this.getFavoriteCities(); // Refresh the list of favorite cities
    }, error => {
      console.error('Error adding city to favorites:', error);
    });
  }
  */
  async addCityToFavorites(): Promise<void> {
    if (!this.currentWeather) {
      console.error('No current weather data available');
      return;
    }
  
    try {
      const response = await this.authService.addFavoriteCity(this.currentWeather.city).toPromise();
      console.log('City added to favorites:', response);

      this.getFavoriteCities(); // Refresh the list of favorite cities
      location.reload();
      this.cdr.detectChanges();
    } catch (error) {
      console.error('Error adding city to favorites:', error);
    }
}
  updateWeatherUserInput(city: string = this.selectedCity, date: string): void {
    this.selectedCity = city;
    this.selectedDate = date;
    this.getCurrentWeather(city);
    this.getForecastWeather(city);
    if (this.authService.isSubscribed()) {
      //this.getHistoricalWeather(city, this.convertDateFormat(date));
      this.getHistoricalWeather(city, this.convertDateFormat(date)).subscribe(
        () => {
          this.selectedDate = date;
        },
        (error: any) => { // Explicitly specify the type of 'error' as 'any'
          console.error('Error fetching historical weather:', error);
          this.selectedDate = this.defaultDate;
        }
      );
    }
  }

  convertUnixTime(unixTime: number): Date {
    return new Date(unixTime * 1000);
  }
  getYesterdaysDate(): string {
    const date = new Date();
    date.setDate(date.getDate() - 1);
    let day = date.getDate();
    let month = date.getMonth() + 1;
    let year = date.getFullYear();

    return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
  }

  convertDateFormat(date: string): string {
    const [year, month, day] = date.split('-');
    return `${day}-${month}-${year}`;
  }

  getDateObject(dateString: string): Date {
    console.log('Date string:', dateString);
    const [day, month, year] = dateString.split('-').map(Number);
    return new Date(Date.UTC(year, month - 1, day));
  }
  isUserSubscribed(): boolean {
    return this.authService.isSubscribed();
  }
  updateWeatherFromFavorites(city: string): void {
    this.selectedCity = city;
    this.updateWeatherUserInput(city, this.defaultDate);
  }
}
