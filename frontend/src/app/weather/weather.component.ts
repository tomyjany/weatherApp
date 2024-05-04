import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-weather',
  templateUrl: './weather.component.html',
  styleUrl: './weather.component.css'
})
export class WeatherComponent {
  currentWeather: any;
  historicalWeather: any;
  forecastWeather: any;
  defaultCity: string = 'Liberec';
  defaultDate: string = this.getYesterdaysDate(); // get yesterday's date in 'yyyy-MM-dd' format
  selectedDate: string = this.defaultDate;

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.getCurrentWeather(this.defaultCity);
    this.getHistoricalWeather(this.defaultCity, this.convertDateFormat(this.defaultDate));
    this.getForecastWeather(this.defaultCity);
  }

  getCurrentWeather(city: string): void {
    this.http.get(`${environment.apiBaseUrl}/api/weather/current?c=${city}&k=${environment.apiKey}`).subscribe(data => {
      this.currentWeather = data;
      this.currentWeather.iconUrl = `https://openweathermap.org/img/wn/${this.currentWeather.icon}@2x.png`;
    });
  }

  getHistoricalWeather(city: string, date: string): void {
    console.log(`Requesting historical weather data for date: ${date}`);
    this.http.get(`${environment.apiBaseUrl}/api/weather/historical?c=${city}&d=${date}&k=${environment.apiKey}`).subscribe(data => {
      this.historicalWeather = data;
    });
  }
  getForecastWeather(city: string): void {
    this.http.get(`${environment.apiBaseUrl}/api/weather/forecast?c=${city}&k=${environment.apiKey}`).subscribe(data => {
      this.forecastWeather = data;
      this.forecastWeather.iconUrl = `https://openweathermap.org/img/wn/${this.forecastWeather.icon}@2x.png`;
    });
  }

  updateWeatherUserInput(city: string, date: string): void {
    this.selectedDate = date;

    this.getCurrentWeather(city);
    this.getHistoricalWeather(city, this.convertDateFormat(date));
    this.getForecastWeather(city);
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
}
