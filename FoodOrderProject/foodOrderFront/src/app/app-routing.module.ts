import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {HomeComponent} from "./components/home/home.component";
import {AllUsersComponent} from "./components/all-users/all-users.component";
import {AddUserComponent} from "./components/add-user/add-user.component";
import {authGuard} from "./guards/auth.guard";
import {EditUserComponent} from "./components/edit-user/edit-user.component";
import {SearchComponent} from "./components/search/search.component";
import {CreateOrderComponent} from "./components/create-order/create-order.component";
import {ErrorsComponent} from "./components/errors/errors.component";
import {ScheduleOrderComponent} from "./components/schedule-order/schedule-order.component";

const routes: Routes = [

  {path: '', component: LoginComponent},
  {path: 'home', component: HomeComponent},
  {path: 'allUsers', component: AllUsersComponent,canActivate: [authGuard], data: {permissions: ["can_read_users"]}},
  {path: 'addUser', component: AddUserComponent,canActivate: [authGuard], data: {permissions: ["can_create_users"]}},
  {path: 'editUser/:id', component: EditUserComponent, canActivate: [authGuard], data: {permissions: ["can_update_users"]}},
  {path: 'search', component: SearchComponent, canActivate: [authGuard], data: {permissions: ["can_search_order"]}},
  {path: 'createOrder', component: CreateOrderComponent, canActivate: [authGuard], data: {permissions: ["can_place_order"]}},
  {path: 'scheduleOrder', component: ScheduleOrderComponent, canActivate: [authGuard], data: {permissions: ["can_schedule_order"]}},
  {path: 'errors', component: ErrorsComponent},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
