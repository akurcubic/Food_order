import {Component, OnInit} from '@angular/core';
import {Dish, Order} from "../../../model";
import {OrderService} from "../../service/order.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-create-order',
  templateUrl: './create-order.component.html',
  styleUrls: ['./create-order.component.css']
})
export class CreateOrderComponent implements OnInit{

  dishes: string[] = [];
  errorMessage = '';
  dishesForOrder: string[] = [];


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

  createOrder() {

    const order = {
      order: this.dishesForOrder.map(dish => dish).join(',')
    };

    console.log(order);


    this.orderService.createOrder(order).subscribe({
      next: (o: Order) => {

        console.log("Uspesno napravljena porudzbina: " + o);
        this.errorMessage = '';
        window.alert("Porudzbina je uspesno kreirana!");
        this.router.navigate(['/search']);

      },
      error: (error: any) => {

        console.log("Greska prilikom pravljenja porudzbine: " + error.error);
        this.errorMessage = error.error
      }
    });

  }


}
