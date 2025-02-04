import { Component } from '@angular/core';
import {LoginRequest, User} from "../../../model";
import {AuthService} from "../../service/auth.service";
import {UserService} from "../../service/user.service";
import {Router} from "@angular/router";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  error = '';

  loginRequest: LoginRequest = {

    email: '',
    password: ''
  };
  errorMessage = '';

  constructor(private authService: AuthService, private userService: UserService, private router: Router) {}

  submit(){

    this.authService.login(this.loginRequest).subscribe({
      next: (response: any) => {

        localStorage.setItem('token', response.jwt);

        this.userService.getUserByEmail(this.loginRequest.email).subscribe({
          next: (user: User) => {

            const userData = {

              userId: user.id,
              firstName: user.firstName,
              lastName: user.lastName,
              email: user.email,
              permissions: user.permissions.map((p: any) => p.name),
              role: user.role.role

            };
            localStorage.setItem('user', JSON.stringify(userData));
            this.errorMessage = '';
            this.router.navigate(["home"]);

          },
          error: (error: any) => {
            console.log("Greska pri dohvacanju korisnika ", error);
            this.errorMessage = "Greska pri dohvacanju korisnickih podataka!";

          }
        })
      },
      error: (error: any) => {
        console.log("Greska pri autentifikovanju ", error);
        this.errorMessage = "Greska pri prijavi!";
      }
    })

  }
}
