import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SignupComponent } from './signup/signup.component';
import { SigninComponent } from './signin/signin.component';
import { HomeComponent } from './home/home.component';

const routes: Routes = [
  {path: 'signup', component: SignupComponent},
  {path: 'signin', component: SigninComponent},
  { path: '', component: HomeComponent }
  //{ path: '**', redirectTo: '/signup' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
