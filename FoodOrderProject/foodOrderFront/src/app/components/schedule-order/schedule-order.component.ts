import {Component, OnInit} from '@angular/core';
import {OrderService} from "../../service/order.service";
import {Router} from "@angular/router";
import {Dish, Order} from "../../../model";

@Component({
  selector: 'app-schedule-order',
  templateUrl: './schedule-order.component.html',
  styleUrls: ['./schedule-order.component.css']
})
export class ScheduleOrderComponent implements OnInit{

  dishes: string[] = [];
  errorMessage = '';
  dishesForOrder: string[] = [];
  selectedDate: string = '';
  selectedTime: string = '';

  constructor(private orderService: OrderService, private router: Router) {
  }

  ngOnInit() {

    this.orderService.getDishes().subscribe({
      next: (dishes: Dish[]) => {

        this.dishes = dishes.map(d => d.name);
        console.log("Uspesno dohvacena sva jela: " + this.dishes);
        this.errorMessage = '';

      },
      error: (error: any) => {

        console.log("Greska prilikom dohvatanja jela: " + error.error);
        this.errorMessage = error.error
      }
    });
  };

  onDishChange(dish: string, event: Event): void {
    const isChecked = (event.target as HTMLInputElement).checked;

    if (isChecked) {

      this.dishesForOrder.push(dish);

    } else {

      this.dishesForOrder = this.dishesForOrder.filter((d) => d !== dish);

    }
    console.log("Jela korisnika nakon promena: ", this.dishesForOrder);
  }

  scheduleOrder() {

    const order = {
      order: this.dishesForOrder.map(dish => dish).join(',')
    };

    const scheduleOrder = {

      scheduledTime: this.selectedDate+"T"+this.selectedTime,
      dishes: order.order
    }


    this.orderService.scheduleOrder(scheduleOrder).subscribe({
      next: (o: Order) => {

        console.log("Uspesno zakazana porudzbina: " + o);
        this.errorMessage = '';
        window.alert("Porudzbina je uspesno zakazana!");
        this.router.navigate(['/search']);

      },
      error: (error: any) => {

        console.log("Greska prilikom zakazivanja porudzbine: " + error.error);
        this.errorMessage = error.error
      }
    });

  }
}
