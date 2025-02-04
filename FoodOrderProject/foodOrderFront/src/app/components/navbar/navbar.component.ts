import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit{

  canCreateUsers = false;
  canSearchOrders = false;
  canCreateOrder = false;
  canScheduleOrder = false;
  firstName = '';

  constructor(private router: Router) {
  }

  ngOnInit() {

    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.canCreateUsers = user.permissions.includes("can_create_users");
    this.canSearchOrders = user.permissions.includes("can_search_order");
    this.canCreateOrder = user.permissions.includes("can_place_order");
    this.canScheduleOrder = user.permissions.includes("can_schedule_order");
    this.firstName = user.firstName;
  }

  logout(){

    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/']);

  }


}
