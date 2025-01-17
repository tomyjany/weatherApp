import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { SignupComponent } from './signup/signup.component';
import {MatFormFieldModule} from '@angular/material/form-field'
import { FormsModule } from '@angular/forms';
import {MatInputModule} from '@angular/material/input'
import {NoopAnimationsModule} from '@angular/platform-browser/animations'
import { SigninComponent } from './signin/signin.component';
import { HomeComponent } from './home/home.component';
import { SubscribeSuccessComponent } from './subscribe-success/subscribe-success.component';
import { WeatherComponent } from './weather/weather.component';

@NgModule({
  declarations: [
    AppComponent,
    SignupComponent,
    SigninComponent,
    HomeComponent,
    SubscribeSuccessComponent,
    WeatherComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    MatFormFieldModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
