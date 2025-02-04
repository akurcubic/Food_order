import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './components/login/login.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {HttpClientModule} from "@angular/common/http";
import { HomeComponent } from './components/home/home.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { AllUsersComponent } from './components/all-users/all-users.component';
import { AddUserComponent } from './components/add-user/add-user.component';
import { EditUserComponent } from './components/edit-user/edit-user.component';
import { SearchComponent } from './components/search/search.component';
import { CreateOrderComponent } from './components/create-order/create-order.component';
import { ErrorsComponent } from './components/errors/errors.component';
import { ScheduleOrderComponent } from './components/schedule-order/schedule-order.component';
import {TimepickerModule} from "ngx-bootstrap/timepicker";
import {BsDatepickerModule} from "ngx-bootstrap/datepicker";
import {NgxMaterialTimepickerModule} from "ngx-material-timepicker";


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    NavbarComponent,
    AllUsersComponent,
    AddUserComponent,
    EditUserComponent,
    SearchComponent,
    CreateOrderComponent,
    ErrorsComponent,
    ScheduleOrderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatInputModule,
    MatButtonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BsDatepickerModule.forRoot(),
    TimepickerModule.forRoot(),
    NgxMaterialTimepickerModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
