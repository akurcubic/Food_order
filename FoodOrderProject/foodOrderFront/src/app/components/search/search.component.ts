import {Component, OnInit} from '@angular/core';
import {Order} from "../../../model";
import {OrderService} from "../../service/order.service";

import {CompatClient, Stomp} from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';


@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit{

  orders: Order[] = [];
  errorMessage = '';
  canTrackOrder = false;
  canCancelOrder = false;
  searchCriteria: string = 'STATUS';
  searchValue: string = '';
  userRole = "";
  startDate: string = "";
  endDate: string = "";

  stompClient: CompatClient | undefined;
  isConnected: boolean = false;

  constructor(private orderService: OrderService) {
  }

  ngOnInit() {

    this.loadOrders();
    this.connect();
  }

  connect(){

    const jwt = localStorage.getItem("token");
    const socket = SockJS(`http://localhost:8080/ws`);
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({}, this.onConnect.bind(this));
  }

  onConnect(frame: any) {
    console.log("Izvrsio sam konekciju");
    // @ts-ignore
    this.stompClient.subscribe('/topic/messages',(message) => {
      console.log("Stigle su promene!" + message.body);
      if (message.body) {
        const updatedOrder = JSON.parse(message.body);

        const index = this.orders.findIndex(order => order.id === updatedOrder.id);
        if (index !== -1) {
          this.orders[index] = updatedOrder;
        }
      }
    });
    this.isConnected = true;
    console.log('Connected: ' + frame);
  }

  loadOrders(): void {

    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.userRole = user.role;
    this.canTrackOrder = user.permissions.includes("can_track_order");
    this.canCancelOrder = user.permissions.includes("can_cancel_order");

    if(this.userRole === 'ADMIN'){
      this.orderService.getAllOrders().subscribe({
        next: (orders: Order[]) => {

          this.orders = orders;
          console.log("Uspesno dohvaceni sve porudzbine: " + this.orders);
          this.errorMessage = '';

        },
        error: (error: any) => {

          console.log("Greska prilikom dohvatanja porudzbina: " + error.error);
          this.errorMessage = error.error
        }
      });
    }
    else{

      this.orderService.getOrdersForUser(user.userId).subscribe({
        next: (orders: Order[]) => {

          this.orders = orders;
          console.log("Uspesno dohvaceni sve porudzbine za korisnika: " + this.orders);
          this.errorMessage = '';

        },
        error: (error: any) => {

          console.log("Greska prilikom dohvatanja porudzbina za korisnika: " + error.error);
          this.errorMessage = error.error
        }
      });
    }
  }

  searchOrders() {

    if (this.searchCriteria === 'USER ID') {

      let value = this.searchValue;

      if (this.searchValue === '') {
        value = '0';
      }

      this.orderService.ordersByUserId(value).subscribe({
        next: (orders: Order[]) => {

          this.orders = orders;
          console.log("Uspesno dohvaceni sve porudzbine po userId: " + this.orders);
          this.errorMessage = '';

        },
        error: (error: any) => {
          console.log(error.error);
          console.log("Greska prilikom dohvatanja porudzbina za userId: " + error.error);
          this.errorMessage = error.error
        }
      });
    } else if (this.searchCriteria === 'STATUS') {

      const searchByStatus = {

        listOfStatus: this.searchValue
      }

      this.orderService.ordersByStatus(searchByStatus).subscribe({
        next: (orders: Order[]) => {

          this.orders = orders;
          console.log("Uspesno dohvaceni sve porudzbine po statusu: " + this.orders);
          this.errorMessage = '';

        },
        error: (error: any) => {

          console.log("Greska prilikom dohvatanja porudzbina za status: " + error.error);
          this.errorMessage = error.error
        }
      });

    } else if (this.searchCriteria === 'DATE') {

      if (!this.startDate || !this.endDate) {
        alert('Please select both start and end dates.');
        return;
      }

      this.orderService.ordersBetweenDates(this.startDate, this.endDate).subscribe({
        next: (orders: Order[]) => {

          this.orders = orders;
          console.log("Uspesno dohvaceni sve porudzbine za datume: " + this.orders);
          this.errorMessage = '';

        },
        error: (error: any) => {

          console.log("Greska prilikom dohvatanja porudzbina za datume: " + error.error);
          this.errorMessage = error.error
        }
      });
    }
  }

  cancelOrder(orderId: number): void{

    this.orderService.cancelOrder(orderId).subscribe({
      next: (order: Order) => {

        window.alert("Porudzbina je uspesno otkazana!");
        console.log("Uspesno otkazana porudzbina: " + order);
        this.errorMessage = '';

      },
      error: (error: any) => {

        console.log("Greska prilikom otkazivanja porudzbine: " + error.error);
        this.errorMessage = error.error
      }
    });
  }
}
