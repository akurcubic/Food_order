import {Component, OnInit} from '@angular/core';
import {OrderService} from "../../service/order.service";
import {ErrorMessage} from "../../../model";

@Component({
  selector: 'app-errors',
  templateUrl: './errors.component.html',
  styleUrls: ['./errors.component.css']
})
export class ErrorsComponent implements OnInit{

  errors: ErrorMessage[] = [];
  errorMessage = '';
  page = 0;

  constructor(private orderService: OrderService) {
  }

  ngOnInit() {

    this.loadErrors();
  }

  loadErrors(): void {

    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const userRole = user.role;

    if(userRole === 'ADMIN'){
      this.orderService.getAllErrors(this.page,10).subscribe({
        next: (errors: any) => {

          this.errors = errors.content;
          console.log("Greske " + errors.content);
          console.log("Uspesno dohvacene sve greske: " + this.errors);
          this.errorMessage = '';

        },
        error: (error: any) => {

          console.log("Greska prilikom dohvatanja gresaka: " + error.error);
          this.errorMessage = error.error
        }
      });
    }
    else{

      this.orderService.getErrorsForUser(user.userId, this.page,10).subscribe({
        next: (errors: any) => {

          this.errors = errors.content;
          console.log("Greske za korsinika " + errors.content);
          console.log("Uspesno dohvacene sve greske za korisnika: " + this.errors);
          this.errorMessage = '';

        },
        error: (error: any) => {

          console.log("Greska prilikom dohvatanja gresaka za korisnika: " + error.error);
          this.errorMessage = error.error
        }
      });
    }
  }

  previousPage() {
    if (this.page > 0) {
      this.page--;
      this.loadErrors();
    }
  }

  nextPage() {

    this.page++;
    this.loadErrors();

  }
}
